package explorer.sidebar;

import explorer.error.ErrorHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * The sidebar's data: the 5 most recently accessed files/folders and the 5 most
 * recent searches. Newest first; re-visiting an item moves it back to the top.
 *
 * Saved between runs with java.util.prefs (the OS's per-user settings store),
 * so this module never writes files itself.
 *
 * Use from the Swing thread only.
 */
public class RecentHistory {

    public record RecentItem(String name, String path, boolean folder) {}

    public static final int MAX_ITEMS = 5;
    public static final int MAX_SEARCHES = 5;

    private static final String KEY_ITEMS = "recentItems";
    private static final String KEY_SEARCHES = "recentSearches";

    private final Preferences prefs = Preferences.userRoot().node("simple-explorer");
    private final List<RecentItem> items = new ArrayList<>();
    private final List<String> searches = new ArrayList<>();
    private final List<Runnable> listeners = new ArrayList<>();

    public RecentHistory() {
        load();
    }

    public List<RecentItem> items() {
        return Collections.unmodifiableList(items);
    }

    public List<String> searches() {
        return Collections.unmodifiableList(searches);
    }

    public void addListener(Runnable onChange) {
        listeners.add(onChange);
    }

    public void recordAccess(String name, String path, boolean folder) {
        items.removeIf(i -> i.path().equals(path));
        items.add(0, new RecentItem(name, path, folder));
        trim(items, MAX_ITEMS);
        changed();
    }

    public void recordSearch(String query) {
        searches.removeIf(s -> s.equalsIgnoreCase(query));
        searches.add(0, query);
        trim(searches, MAX_SEARCHES);
        changed();
    }

    private static void trim(List<?> list, int max) {
        while (list.size() > max) list.remove(list.size() - 1);
    }

    private void changed() {
        save();
        listeners.forEach(Runnable::run);
    }

    // ---------- persistence: one line per entry, fields separated by tabs ----------

    private void save() {
        StringBuilder sb = new StringBuilder();
        for (RecentItem i : items) {
            sb.append(i.folder() ? 'D' : 'F').append('\t')
              .append(i.name()).append('\t').append(i.path()).append('\n');
        }
        prefs.put(KEY_ITEMS, sb.toString());
        prefs.put(KEY_SEARCHES, String.join("\n", searches));
        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            ErrorHandler.report("saving recent history", e);
        }
    }

    private void load() {
        try {
            for (String line : prefs.get(KEY_ITEMS, "").split("\n")) {
                String[] f = line.split("\t", 3);
                if (f.length == 3) items.add(new RecentItem(f[1], f[2], f[0].equals("D")));
            }
            for (String s : prefs.get(KEY_SEARCHES, "").split("\n")) {
                if (!s.isBlank()) searches.add(s);
            }
            trim(items, MAX_ITEMS);
            trim(searches, MAX_SEARCHES);
        } catch (RuntimeException e) {
            // Corrupt or unreadable settings: start with empty history rather than crash.
            items.clear();
            searches.clear();
            ErrorHandler.report("loading recent history", e);
        }
    }
}
