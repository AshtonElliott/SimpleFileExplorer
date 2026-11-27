package explorer.sidebar;

import explorer.sidebar.RecentHistory.RecentItem;
import explorer.ui.Icons;
import explorer.ui.Theme;
import explorer.ui.Widgets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

/**
 * Quick-access sidebar with three sections:
 *   Drives  |  Recent (5 files/folders)  |  Recent searches (5)
 * Single-click to use an entry. The panel only displays and forwards clicks;
 * what a click does is decided by the GUI through the callbacks.
 */
public class SidebarPanel extends JPanel {

    private final RecentHistory history;
    private final DefaultListModel<RecentItem> recentModel = new DefaultListModel<>();
    private final DefaultListModel<String> searchModel = new DefaultListModel<>();
    private final JLabel recentEmpty = Widgets.muted("Nothing opened yet");
    private final JLabel searchEmpty = Widgets.muted("No searches yet");

    public SidebarPanel(List<String> drives,
                        RecentHistory history,
                        Consumer<String> onDrive,
                        Consumer<RecentItem> onRecentItem,
                        Consumer<String> onRecentSearch) {
        super(new GridBagLayout());
        this.history = history;
        setBackground(Theme.SIDEBAR);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER));

        // Drives
        DefaultListModel<String> driveModel = new DefaultListModel<>();
        drives.forEach(driveModel::addElement);
        JList<String> driveList = new JList<>(driveModel);
        Widgets.styleList(driveList, d -> Icons.drive(16), d -> d, d -> "Open " + d);
        onClick(driveList, onDrive);

        // Recent files/folders
        JList<RecentItem> recentList = new JList<>(recentModel);
        Widgets.styleList(recentList,
                item -> item.folder() ? Icons.folder(16) : Icons.forEntry(false, typeFromName(item.name())),
                RecentItem::name, RecentItem::path);
        onClick(recentList, onRecentItem);

        // Recent searches
        JList<String> searchList = new JList<>(searchModel);
        Widgets.styleList(searchList, q -> Icons.search(14, Theme.MUTED), q -> q, q -> "Search again for \"" + q + "\"");
        onClick(searchList, onRecentSearch);

        int row = 0;
        row = addRow(Widgets.sectionHeader("Drives", null), row);
        row = addRow(driveList, row);
        row = addRow(Widgets.sectionHeader("Recent", Icons.clock(12, Theme.MUTED)), row);
        row = addRow(recentList, row);
        row = addRow(recentEmpty, row);
        row = addRow(Widgets.sectionHeader("Recent searches", Icons.search(12, Theme.MUTED)), row);
        row = addRow(searchList, row);
        row = addRow(searchEmpty, row);

        // filler pushes everything to the top
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridy = row;
        filler.weighty = 1;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        add(spacer, filler);

        setPreferredSize(new Dimension(210, 0));
        history.addListener(this::refresh);
        refresh();
    }

    private int addRow(JComponent c, int row) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = row;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.NORTHWEST;
        add(c, gc);
        return row + 1;
    }

    private void refresh() {
        recentModel.clear();
        history.items().forEach(recentModel::addElement);
        searchModel.clear();
        history.searches().forEach(searchModel::addElement);
        recentEmpty.setVisible(recentModel.isEmpty());
        searchEmpty.setVisible(searchModel.isEmpty());
        revalidate();
        repaint();
    }

    /** Text-only guess of a file's type from its name, just for picking an icon color. */
    private static String typeFromName(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(dot + 1) + " file" : "File";
    }

    private static <T> void onClick(JList<T> list, Consumer<T> action) {
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());
                if (index >= 0 && list.getCellBounds(index, index).contains(e.getPoint())) {
                    action.accept(list.getModel().getElementAt(index));
                    list.clearSelection();
                }
            }
        });
    }
}
