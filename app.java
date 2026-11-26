// Java Imports
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class app {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(app::createGui);
    }
    private static void createGui() {
        // Setup
        JFrame frame = new JFrame("Simple File Explorer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 500);
        frame.setLayout(new BorderLayout());
        //JLabel label = new JLabel("Welcome to Simple File Explorer.");

        // Header controls
        //JTextField folderField = new JTextField(System.getProperty("user.home"), 30);
        JTextField folderField = new JTextField(System.getProperty("user.dir"), 30);
        //Path workingDir = Paths.get(folderField.toString().trim());
        JButton upButton = new JButton("Up");

        // Button for folder field
        JButton goButton = new JButton("Go");

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //topPanel.add(label);
        
        // Table for file data
        String[] columnNames = {"Name", "Date Modified", "Size", "Path"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
            @Override public Class<?> getColumnClass(int c) {
                switch (c) {
                    case 1: return Date.class;
                    case 2: return Long.class;
                    default: return String.class;
                }
            }
        };

        // Results
        //DefaultListModel<String> results = new DefaultListModel<>();
        //JList<String> resultList = new JList<>(results);

        // Table to hold results
        JTable table = new JTable(model);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(table);

        // Directory controls
        topPanel.add(upButton);
        topPanel.add(new JLabel("Folder:"));
        topPanel.add(folderField);
        topPanel.add(goButton);

        // Quick access
        

        // Divider
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));

        // Search controls
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);

        // Add GUI elements to frame
        JLabel status = new JLabel(" ");
        frame.add(topPanel, BorderLayout.NORTH);
        //frame.add(quickAccess, BorderLayout.WEST);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(status, BorderLayout.SOUTH);

        // Get contents of working directory
        Runnable runWorkingDir = () -> {
            //Path root = Paths.get(folderField.getText().trim());
            model.setRowCount(0); // Clear previous results
            searchButton.setEnabled(false);
            status.setText("Working...");

            new SwingWorker<Void, Object[]>() {
                @Override
                protected Void doInBackground() throws IOException {
                    // Working directory
                    Path workingDir = Paths.get(folderField.getText());
                    //System.out.println("Working directory: " + workingDir.toString());
                    //JTextField workingDirField = new JTextField(workingDir.toString(), 30);

                    try (Stream<Path> resultsStream = Files.list(workingDir)) {
                        // Process each path
                        resultsStream.forEach(path -> publish(toRow(path)));
                    }
                    return null;
                }

                @Override
                protected void process(List<Object[]> rows) {
                    rows.forEach(model::addRow);
                }
                @Override
                protected void done() {
                    searchButton.setEnabled(true);
                    status.setText(model.getRowCount() + " item(s).");
                }
            }.execute();
        };

        // Searching
        Runnable runSearch = () -> {
            String query = searchField.getText().trim().toLowerCase();
            Path root = Paths.get(folderField.getText().trim());

            if (!Files.isDirectory(root)) {
                status.setText("Folder not found: " + root);
                return;
            }
            model.setRowCount(0); // Clear previous results
            searchButton.setEnabled(false);
            status.setText("Searching...");
            new SwingWorker<Void, Object[]>() {
                @Override
                protected Void doInBackground() throws IOException {
                    Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                        // Include directories themselves
                        @Override 
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                            //System.out.println(dir.getFileName().toString());
                            if (dir.getFileName().toString().toLowerCase().contains(query)) {
                                publish(toRow(dir));
                            }
                            return FileVisitResult.CONTINUE;
                        }
                        
                        // Checking files for matches
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            //System.out.println(file.getFileName().toString());
                            if (file.getFileName().toString().toLowerCase().contains(query)) {
                                publish(toRow(file));
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException e) {
                            return FileVisitResult.CONTINUE; // skip folders we can't read
                        }
                    });
                    return null;
                }
                @Override
                protected void process(List<Object[]> rows) {
                    rows.forEach(model::addRow);
                }
                @Override
                protected void done() {
                    searchButton.setEnabled(true);
                    status.setText(model.getRowCount() + " result(s) found.");
                }
            }.execute();
        };
        
        Runnable upDir = () -> {
            String currentPath = folderField.getText();
            File currentDir = new File(currentPath);
            File parentDir = currentDir.getParentFile();
            if (parentDir != null) {
                folderField.setText(parentDir.getAbsolutePath());
                runWorkingDir.run();
            } else {
                status.setText("No parent directory.");
            }
        };

        // Search button and enter key trigger search
        searchButton.addActionListener(e -> runSearch.run());
        searchField.addActionListener(e -> runSearch.run()); // pressing Enter also searches
       
        // Back button returns up to parent directory
        upButton.addActionListener(e -> upDir.run());

        // Typing path in folder field and pressing enter updates working directory
        folderField.addActionListener(e -> runWorkingDir.run());
        // Also works with go button
        goButton.addActionListener(e -> runWorkingDir.run());

        // Double-click a result to open it or change directory
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // On double click
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    
                    int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                    File f = new File((String) model.getValueAt(modelRow, 3));
                        
                    try {
                        // If directory, change working directory
                        if (f.isDirectory()) {
                            //System.out.println("Changing working directory to: " + f.getAbsolutePath());
                            folderField.setText(f.getAbsolutePath());
                            //workingDir = Paths.get(f.getAbsolutePath());
                            runWorkingDir.run();
                            //return;
                        }
                        // Else, open file with default application
                        else {
                            Desktop.getDesktop().open(f);
                        }
                    } catch (IOException ex) {
                        status.setText("Could not open file: " + ex.getMessage());
                    }
                }
            }
        });
        
        // Run the working directory on startup
        runWorkingDir.run();

        frame.setVisible(true);
    }

    
    private static Object[] toRow(Path p) {
        String name = p.getFileName() != null ? p.getFileName().toString() : p.toString();
        try {
            BasicFileAttributes a = Files.readAttributes(p, BasicFileAttributes.class);
            return new Object[] {
                name,
                new Date(a.lastModifiedTime().toMillis()),
                a.isDirectory() ? 0L : a.size(),
                p.toString()
            };
        } catch (IOException e) {
            return new Object[] { name, null, 0L, p.toString() };
        }
    }
}