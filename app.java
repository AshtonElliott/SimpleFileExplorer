// Java Imports
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class app {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(app::createGui);
    }
    private static void createGui() {
        // Setup
        JFrame frame = new JFrame("Simple File Explorer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(2000, 1000);
        frame.setLayout(new BorderLayout());
        JLabel label = new JLabel("Welcome to Simple File Explorer.");

        // Search controls
        JTextField folderField = new JTextField(System.getProperty("user.home"), 30);
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(label);
        topPanel.add(new JLabel("Folder:"));
        topPanel.add(folderField);
        topPanel.add(new JLabel("Name contains:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);

        // Results
        DefaultListModel<String> results = new DefaultListModel<>();
        JList<String> resultList = new JList<>(results);
        JLabel status = new JLabel(" ");
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(resultList), BorderLayout.CENTER);
        frame.add(status, BorderLayout.SOUTH);
        Runnable runSearch = () -> {
            String query = searchField.getText().trim().toLowerCase();
            Path root = Paths.get(folderField.getText().trim());
            if (query.isEmpty()) {
                status.setText("Type something to search for.");
                return;
            }
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
        searchButton.addActionListener(e -> runSearch.run());
        searchField.addActionListener(e -> runSearch.run()); // pressing Enter also searches
        // Double-click a result to open it
        resultList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && resultList.getSelectedValue() != null) {
                    try {
                        Desktop.getDesktop().open(new File(resultList.getSelectedValue()));
                    } catch (IOException ex) {
                        status.setText("Could not open file: " + ex.getMessage());
                    }
                }
            }
        });
        frame.setVisible(true);
    }
}