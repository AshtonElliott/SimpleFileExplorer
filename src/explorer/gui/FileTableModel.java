package explorer.gui;

import explorer.file.FileInfo;

import javax.swing.table.AbstractTableModel;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Table rows backed directly by FileInfo objects (no Object[] copies). */
public class FileTableModel extends AbstractTableModel {

    public static final int COL_NAME = 0, COL_TYPE = 1, COL_SIZE = 2,
                            COL_CREATED = 3, COL_MODIFIED = 4, COL_PATH = 5;

    private static final String[] COLUMNS =
            {"Name", "Type", "Size", "Date Created", "Date Modified", "Path"};

    private final List<FileInfo> rows = new ArrayList<>();

    public void clear() {
        int n = rows.size();
        if (n == 0) return;
        rows.clear();
        fireTableRowsDeleted(0, n - 1);
    }

    public void addAll(List<FileInfo> more) {
        if (more.isEmpty()) return;
        int first = rows.size();
        rows.addAll(more);
        fireTableRowsInserted(first, rows.size() - 1);
    }

    public FileInfo get(int modelRow) {
        return rows.get(modelRow);
    }

    @Override public int getRowCount() { return rows.size(); }

    @Override public int getColumnCount() { return COLUMNS.length; }

    @Override public String getColumnName(int c) { return COLUMNS[c]; }

    @Override public boolean isCellEditable(int r, int c) { return false; }

    /** Real types so the table sorts numbers and dates correctly. */
    @Override
    public Class<?> getColumnClass(int c) {
        return switch (c) {
            case COL_SIZE -> Long.class;
            case COL_CREATED, COL_MODIFIED -> Instant.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int r, int c) {
        FileInfo f = rows.get(r);
        return switch (c) {
            case COL_NAME -> f.name();
            case COL_TYPE -> f.type();
            case COL_SIZE -> f.size();           // -1 for folders; shown as "—"
            case COL_CREATED -> f.created();
            case COL_MODIFIED -> f.modified();
            case COL_PATH -> f.path();
            default -> null;
        };
    }
}
