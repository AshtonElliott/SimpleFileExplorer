// Java Imports
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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
        frame.setSize(1500, 1000);
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

        // Directory controls
        topPanel.add(upButton);
        topPanel.add(new JLabel("Folder:"));
        topPanel.add(folderField);
        topPanel.add(goButton);

        // Divider
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));

        // Search controls
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        

        // Results
        DefaultListModel<String> results = new DefaultListModel<>();
        JList<String> resultList = new JList<>(results);

        // Gui elements
        JLabel status = new JLabel(" ");
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(resultList), BorderLayout.CENTER);
        frame.add(status, BorderLayout.SOUTH);

        // Get contents of working directory
        Runnable runWorkingDir = () -> {
            //Path root = Paths.get(folderField.getText().trim());
            results.clear();
            searchButton.setEnabled(false);
            status.setText("Working...");

            new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws IOException {
                    // Working directory
                    Path workingDir = Paths.get(folderField.getText());
                    //System.out.println("Working directory: " + workingDir.toString());
                    //JTextField workingDirField = new JTextField(workingDir.toString(), 30);

                    try (Stream<Path> resultsStream = Files.list(workingDir)) {
                        resultsStream.forEach(path -> {
                            results.addElement(path.toString());
                        });
                    }
                    return null;
                }
                @Override
                protected void process(List<String> found) {
                    found.forEach(results::addElement);
                }
                @Override
                protected void done() {
                    searchButton.setEnabled(true);
                    status.setText(results.size() + " item(s).");
                }
            }.execute();
        };

        // Run the working directory on startup
        runWorkingDir.run();

        // Searching
        Runnable runSearch = () -> {
            String query = searchField.getText().trim().toLowerCase();
            Path root = Paths.get(folderField.getText().trim());
            /*if (query.isEmpty()) {
                status.setText("Type something to search for.");
                return;
            }*/
            if (!Files.isDirectory(root)) {
                status.setText("Folder not found: " + root);
                return;
            }
            results.clear();
            searchButton.setEnabled(false);
            status.setText("Searching...");
            new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws IOException {
                    Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            if (file.getFileName().toString().toLowerCase().contains(query)) {
                                publish(file.toString());
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
                protected void process(List<String> found) {
                    found.forEach(results::addElement);
                }
                @Override
                protected void done() {
                    searchButton.setEnabled(true);
                    status.setText(results.size() + " result(s) found.");
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
       
        // Back button returns up to parent directory
        upButton.addActionListener(e -> upDir.run());

        // Typing path in folder field and pressing enter updates working directory
        folderField.addActionListener(e -> runWorkingDir.run());
        // Also works with go button
        goButton.addActionListener(e -> runWorkingDir.run());

        // Search button and enter key trigger search
        searchButton.addActionListener(e -> runSearch.run());
        searchField.addActionListener(e -> runSearch.run()); // pressing Enter also searches

        // Double-click a result to open it or change directory
        resultList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // On double click
                if (e.getClickCount() == 2 && resultList.getSelectedValue() != null) {
                    try {
                        File f = new File(resultList.getSelectedValue());
                        
                        // If directory, change working directory
                        if (f.isDirectory()) {
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
        frame.setVisible(true);
    }
}