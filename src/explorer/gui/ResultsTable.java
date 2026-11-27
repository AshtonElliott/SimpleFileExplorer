package explorer.gui;

import explorer.file.FileInfo;
import explorer.ui.Icons;
import explorer.ui.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * The styled results table: roomy rows, zebra stripes, hover highlight,
 * file-type icons, formatted sizes/dates, a flat header with sort arrows,
 * and a centered message when there is nothing to show.
 */
public class ResultsTable extends JTable {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());

    private final FileTableModel model;
    private int hoverRow = -1;
    private String emptyText = "";

    public ResultsTable(FileTableModel model) {
        super(model);
        this.model = model;

        setAutoCreateRowSorter(true);
        setFillsViewportHeight(true);
        setRowHeight(30);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setBackground(Theme.BACKGROUND);
        setSelectionBackground(Theme.SELECTION);
        setSelectionForeground(Theme.TEXT);
        setDefaultRenderer(Object.class, new Cell());
        setDefaultRenderer(Long.class, new Cell());
        setDefaultRenderer(Instant.class, new Cell());

        // {preferred, minimum} width per column; Path takes whatever space is left
        int[][] widths = {{280, 160}, {110, 90}, {95, 85}, {150, 145}, {150, 145}, {360, 120}};
        for (int i = 0; i < widths.length; i++) {
            getColumnModel().getColumn(i).setPreferredWidth(widths[i][0]);
            getColumnModel().getColumn(i).setMinWidth(widths[i][1]);
        }

        JTableHeader header = getTableHeader();
        header.setDefaultRenderer(new HeaderCell());
        header.setPreferredSize(new Dimension(0, 34));
        header.setReorderingAllowed(false);
        header.setBackground(Theme.BACKGROUND);

        MouseAdapter hover = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int r = rowAtPoint(e.getPoint());
                if (r != hoverRow) {
                    hoverRow = r;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverRow = -1;
                repaint();
            }
        };
        addMouseListener(hover);
        addMouseMotionListener(hover);
    }

    /** Message shown in the middle of the table when it has no rows. */
    public void setEmptyText(String text) {
        emptyText = text;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getRowCount() == 0 && emptyText != null && !emptyText.isEmpty()) {
            Graphics2D g2 = Theme.smooth(g);
            g2.setFont(Theme.FONT.deriveFont(14f));
            g2.setColor(new Color(0x9CA3AF));
            FontMetrics fm = g2.getFontMetrics();
            Rectangle view = getVisibleRect();
            int x = view.x + (view.width - fm.stringWidth(emptyText)) / 2;
            int y = view.y + Math.min(view.height / 2, 140);
            g2.drawString(emptyText, x, y);
            g2.dispose();
        }
    }

    // ---------------- cells ----------------

    private class Cell extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                       boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, value, selected, false, row, col);
            int modelCol = convertColumnIndexToModel(col);
            FileInfo info = model.get(convertRowIndexToModel(row));

            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            setBackground(selected ? Theme.SELECTION
                    : row == hoverRow ? Theme.HOVER
                    : row % 2 == 1 ? Theme.ROW_STRIPE : Theme.BACKGROUND);
            setForeground(modelCol == FileTableModel.COL_NAME ? Theme.TEXT : Theme.MUTED);
            setHorizontalAlignment(modelCol == FileTableModel.COL_SIZE ? RIGHT : LEFT);
            setIcon(null);
            setToolTipText(null);
            setIconTextGap(8);

            switch (modelCol) {
                case FileTableModel.COL_NAME -> setIcon(Icons.forEntry(info.isFolder(), info.type()));
                case FileTableModel.COL_SIZE -> {
                    long bytes = value instanceof Long l ? l : -1;
                    setText(bytes < 0 ? "—" : humanSize(bytes));
                }
                case FileTableModel.COL_CREATED, FileTableModel.COL_MODIFIED ->
                        setText(value instanceof Instant i ? DATE_FORMAT.format(i) : "");
                case FileTableModel.COL_PATH -> setToolTipText((String) value);
                default -> { }
            }
            return this;
        }
    }

    private class HeaderCell extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                       boolean focus, int row, int col) {
            super.getTableCellRendererComponent(t, value, false, false, row, col);
            setFont(Theme.FONT_SMALL_BOLD.deriveFont(12f));
            setForeground(Theme.MUTED);
            setBackground(Theme.BACKGROUND);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)));
            setHorizontalAlignment(convertColumnIndexToModel(col) == FileTableModel.COL_SIZE ? RIGHT : LEFT);
            setHorizontalTextPosition(LEFT);
            setIconTextGap(6);
            setIcon(sortIcon(col));
            return this;
        }

        private Icon sortIcon(int viewCol) {
            if (getRowSorter() == null) return null;
            List<? extends RowSorter.SortKey> keys = getRowSorter().getSortKeys();
            if (keys.isEmpty() || keys.get(0).getColumn() != convertColumnIndexToModel(viewCol)) return null;
            SortOrder order = keys.get(0).getSortOrder();
            return order == SortOrder.UNSORTED ? null : Icons.sortArrow(order == SortOrder.ASCENDING);
        }
    }

    static String humanSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        String[] units = {"KB", "MB", "GB", "TB"};
        double v = bytes;
        int u = -1;
        do {
            v /= 1024;
            u++;
        } while (v >= 1024 && u < units.length - 1);
        return String.format("%.1f %s", v, units[u]);
    }
}
