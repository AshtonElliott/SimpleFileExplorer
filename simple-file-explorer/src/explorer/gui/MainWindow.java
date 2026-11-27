package explorer.gui;

import explorer.error.AppException;
import explorer.error.ErrorHandler;
import explorer.file.FileController;
import explorer.file.FileInfo;
import explorer.search.SearchService;
import explorer.search.SearchService.ListFolder;
import explorer.search.SearchService.NameSearch;
import explorer.search.SearchService.Plan;
import explorer.search.SearchService.Result;
import explorer.sidebar.RecentHistory;
import explorer.sidebar.RecentHistory.RecentItem;
import explorer.sidebar.SidebarPanel;
import explorer.ui.Icons;
import explorer.ui.Theme;
import explorer.ui.Widgets;
import explorer.ui.Widgets.FlatButton;
import explorer.ui.Widgets.SearchField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * The GUI: takes user input, shows results. All real work is delegated to
 * SearchService / FileController; slow work runs in a SwingWorker so the window
 * never freezes. Starting a new job always cancels the previous one.
 *
 * Layout:
 *   ┌──────────────────────────────────────────────────────────┐
 *   │ [↑]  [ 🔍 Type a path or a name...      ] [Go] ☐ All  [■] │  header
 *   │ 📁 C:\ › Users › me                                       │  breadcrumb
 *   ├───────────┬──────────────────────────────────────────────┤
 *   │ DRIVES    │  Name        Type    Size   Created  Modified │
 *   │ RECENT    │  ...                                          │  results
 *   │ SEARCHES  │                                               │
 *   ├───────────┴──────────────────────────────────────────────┤
 *   │ 12 items                                     [=====   ]  │  status bar
 *   └──────────────────────────────────────────────────────────┘
 */
public class MainWindow {

    private final FileController files;
    private final SearchService search;
    private final RecentHistory history = new RecentHistory();

    private final JFrame frame = new JFrame("Simple File Explorer");
    private final SearchField inputField =
            new SearchField("Type a folder path to open it, or a name to search for…", 40);
    private final JCheckBox allDrivesBox = new JCheckBox("Search all drives");
    private final FlatButton stopButton = new FlatButton("Stop", Icons.stop(12, Theme.ERROR), false);
    private final JPanel breadcrumb = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
    private final JLabel statusLabel = new JLabel(" ");
    private final JProgressBar progress = new JProgressBar();
    private final FileTableModel tableModel = new FileTableModel();
    private final ResultsTable table = new ResultsTable(tableModel);

    private String currentFolder;
    private SwingWorker<Result, FileInfo> currentJob;

    public MainWindow(FileController files) {
        this.files = files;
        this.search = new SearchService(files);
        this.currentFolder = files.startFolder();
        ErrorHandler.setNotifier(this::showError);
        buildLayout();
        wireEvents();
    }

    public void show() {
        frame.setVisible(true);
        navigate(currentFolder);
        inputField.requestFocusInWindow();
    }

    // ================= layout =================

    private void buildLayout() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(Icons.appImage());
        frame.setSize(1200, 700);
        frame.setMinimumSize(new Dimension(900, 480));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(buildHeader(), BorderLayout.NORTH);

        SidebarPanel sidebar = new SidebarPanel(files.roots(), history,
                this::navigate, this::openRecentItem, this::rerunSearch);
        JScrollPane sidebarScroll = new JScrollPane(sidebar,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScroll.getViewport().setBackground(Theme.SIDEBAR);
        frame.add(sidebarScroll, BorderLayout.WEST);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.getViewport().setBackground(Theme.BACKGROUND);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        frame.add(tableScroll, BorderLayout.CENTER);

        frame.add(buildStatusBar(), BorderLayout.SOUTH);
    }

    private JComponent buildHeader() {
        FlatButton upButton = new FlatButton(null, Icons.arrowUp(16, Theme.TEXT), false);
        upButton.setToolTipText("Up one folder");
        upButton.addActionListener(e -> goUp());

        FlatButton goButton = new FlatButton("Go", null, true);
        goButton.addActionListener(e -> runInput());

        stopButton.setEnabled(false);
        stopButton.setToolTipText("Stop the current search");
        stopButton.addActionListener(e -> cancelCurrentJob());

        allDrivesBox.setOpaque(false);
        allDrivesBox.setForeground(Theme.TEXT);
        allDrivesBox.setFocusPainted(false);
        allDrivesBox.setToolTipText("Search every drive instead of just the current folder (slower)");

        JPanel right = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        right.setOpaque(false);
        right.add(goButton);
        right.add(allDrivesBox);
        right.add(stopButton);

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.add(upButton, BorderLayout.WEST);
        row.add(inputField, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);

        breadcrumb.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout(0, 10));
        header.setBackground(Theme.BACKGROUND);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(12, 14, 10, 14)));
        header.add(row, BorderLayout.NORTH);
        header.add(breadcrumb, BorderLayout.SOUTH);
        return header;
    }

    private JComponent buildStatusBar() {
        statusLabel.setForeground(Theme.MUTED);
        statusLabel.setIconTextGap(6);

        progress.setIndeterminate(true);
        progress.setPreferredSize(new Dimension(120, 4));
        progress.setVisible(false);
        JPanel progressHolder = new JPanel(new GridBagLayout()); // centers the bar vertically
        progressHolder.setOpaque(false);
        progressHolder.add(progress);

        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.SIDEBAR);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        bar.add(statusLabel, BorderLayout.CENTER);
        bar.add(progressHolder, BorderLayout.EAST);
        return bar;
    }

    /** Clickable path: C:\ › Users › me */
    private void updateBreadcrumb() {
        breadcrumb.removeAll();
        breadcrumb.add(new JLabel(Icons.folder(16)));
        List<String> chain = files.pathChain(currentFolder);
        for (int i = 0; i < chain.size(); i++) {
            String path = chain.get(i);
            boolean last = i == chain.size() - 1;
            if (i > 0) {
                JLabel sep = new JLabel("›");
                sep.setForeground(new Color(0x9CA3AF));
                breadcrumb.add(sep);
            }
            JLabel part = new JLabel(files.displayName(path));
            part.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            if (last) {
                part.setFont(Theme.FONT_BOLD);
                part.setForeground(Theme.TEXT);
            } else {
                part.setForeground(Theme.MUTED);
                part.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                part.setToolTipText(path);
                part.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) { navigate(path); }

                    @Override public void mouseEntered(MouseEvent e) { part.setForeground(Theme.ACCENT); }

                    @Override public void mouseExited(MouseEvent e) { part.setForeground(Theme.MUTED); }
                });
            }
            breadcrumb.add(part);
        }
        breadcrumb.revalidate();
        breadcrumb.repaint();
    }

    private void wireEvents() {
        inputField.addActionListener(e -> runInput()); // Enter key

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewRow = table.rowAtPoint(e.getPoint());
                if (e.getClickCount() == 2 && viewRow >= 0) {
                    FileInfo info = tableModel.get(table.convertRowIndexToModel(viewRow));
                    if (info.isFolder()) navigate(info.path());
                    else openFile(info.name(), info.path());
                }
            }
        });
    }

    // ================= user actions =================

    private void runInput() {
        try {
            execute(search.plan(inputField.getText(), currentFolder, allDrivesBox.isSelected()));
        } catch (AppException e) {
            ErrorHandler.report("reading input", e);
        }
    }

    private void navigate(String folder) {
        execute(new ListFolder(folder));
    }

    private void goUp() {
        files.parent(currentFolder).ifPresentOrElse(this::navigate,
                () -> setStatus("Already at the top: no parent folder."));
    }

    private void openRecentItem(RecentItem item) {
        if (item.folder()) navigate(item.path());
        else openFile(item.name(), item.path());
    }

    private void rerunSearch(String query) {
        inputField.setText(query);
        runInput();
    }

    private void openFile(String name, String path) {
        try {
            files.open(path);
            history.recordAccess(name, path, false);
            setStatus("Opened " + name);
        } catch (AppException e) {
            ErrorHandler.report("opening file", e);
        }
    }

    // ================= background jobs =================

    private void execute(Plan plan) {
        cancelCurrentJob();
        tableModel.clear();
        stopButton.setEnabled(true);
        progress.setVisible(true);

        if (plan instanceof ListFolder lf) {
            setStatus("Loading " + lf.folder() + " …");
            table.setEmptyText("Loading…");
        } else if (plan instanceof NameSearch ns) {
            history.recordSearch(ns.query());
            setStatus("Searching for \"" + ns.query() + "\" in " + ns.scope() + " …");
            table.setEmptyText("Searching…");
        } else {
            setStatus("Loading …");
            table.setEmptyText("Loading…");
        }

        SwingWorker<Result, FileInfo> job = new SwingWorker<Result, FileInfo>() {
            @Override
            protected Result doInBackground() throws Exception {
                return search.run(plan, this::publish, this::isCancelled);
            }

            @Override
            protected void process(List<FileInfo> chunk) {
                // Ignore late results from a job that was replaced or stopped.
                if (this == currentJob && !isCancelled()) tableModel.addAll(chunk);
            }

            @Override
            protected void done() {
                if (this != currentJob) return;
                stopButton.setEnabled(false);
                progress.setVisible(false);
                finish(plan, this);
            }
        };
        currentJob = job;
        job.execute();
    }

    private void finish(Plan plan, SwingWorker<Result, FileInfo> job) {
        try {
            Result r = job.get();
            String text;
            if (plan instanceof ListFolder lf) {
                currentFolder = lf.folder();
                updateBreadcrumb();
                FileInfo folder = files.info(currentFolder);
                history.recordAccess(folder.name(), folder.path(), true);
                text = plural(r.found(), "item");
                table.setEmptyText("This folder is empty");
            } else if (plan instanceof NameSearch ns) {
                text = plural(r.found(), "result") + " for \"" + ns.query() + "\" in " + ns.scope();
                table.setEmptyText("No matches for \"" + ns.query() + "\"");
            } else {
                text = "1 file";
            }
            if (r.skipped() > 0) {
                String warning = r.skipped() + " item(s) could not be read (no permission?), "
                        + "so results may be incomplete.";
                ErrorHandler.warn(warning + " Plan: " + plan);
                setStatus(text + "  ·  " + warning, Theme.WARNING, Icons.alert(14, Theme.WARNING));
            } else {
                setStatus(text);
            }
        } catch (CancellationException e) {
            setStatus("Stopped. " + tableModel.getRowCount() + " shown.");
            table.setEmptyText("Stopped");
        } catch (ExecutionException e) {
            table.setEmptyText("Couldn't load this");
            ErrorHandler.report(describe(plan), e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (AppException e) {
            ErrorHandler.report(describe(plan), e);
        }
    }

    private void cancelCurrentJob() {
        if (currentJob != null && !currentJob.isDone()) currentJob.cancel(false);
    }

    private static String describe(Plan plan) {
        if (plan instanceof ListFolder) return "listing folder";
        if (plan instanceof NameSearch) return "searching";
        return "reading file";
    }

    private static String plural(int n, String word) {
        return n + " " + word + (n == 1 ? "" : "s");
    }

    // ================= status & errors =================

    private void setStatus(String text) {
        setStatus(text, Theme.MUTED, null);
    }

    private void setStatus(String text, Color color, Icon icon) {
        statusLabel.setForeground(color);
        statusLabel.setIcon(icon);
        statusLabel.setText(text);
    }

    /** Called by ErrorHandler from any thread. */
    private void showError(String message, boolean serious) {
        Runnable show = () -> {
            boolean running = currentJob != null && !currentJob.isDone();
            stopButton.setEnabled(running);
            progress.setVisible(running);
            setStatus(message, Theme.ERROR, Icons.alert(14, Theme.ERROR));
            if (serious) {
                JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) show.run();
        else SwingUtilities.invokeLater(show);
    }
}
