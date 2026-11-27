package explorer.search;

import explorer.error.AppException;
import explorer.file.FileController;
import explorer.file.FileInfo;

import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Decides what the single input box means, then carries it out through the File Controller.
 *
 *   input looks like a path  -> folder: list everything directly inside it
 *                            -> file:   show just that file
 *   input is a plain name    -> search names containing it (case-insensitive) in the
 *                               current folder and all subfolders, or on all drives
 *
 * "Looks like a path" = absolute, contains / or \, starts with ~, a drive like C:, or . / ..
 *
 * Work is split in two steps so the GUI can update immediately and run the slow part
 * in the background: {@link #plan} is quick, {@link #run} may take a long time.
 * No Swing code here.
 */
public class SearchService {

    /** What the input was understood to mean. */
    public sealed interface Plan permits ListFolder, ShowFile, NameSearch {}

    public record ListFolder(String folder) implements Plan {}

    public record ShowFile(String file) implements Plan {}

    /** @param scope human-readable, e.g. "C:\\Users\\me" or "all drives" */
    public record NameSearch(String query, List<String> roots, String scope) implements Plan {}

    /** @param skipped entries that could not be read: if > 0, results may be incomplete */
    public record Result(int found, int skipped) {}

    private final FileController files;

    public SearchService(FileController files) {
        this.files = files;
    }

    /** Quick: interprets the input. Throws with a user-friendly message if it can't. */
    public Plan plan(String input, String currentFolder, boolean allDrives) throws AppException {
        String text = input == null ? "" : input.trim();
        if (text.isEmpty()) {
            throw new AppException("Type a folder path to open it, or a name to search for.");
        }

        if (looksLikePath(text)) {
            String path = files.resolve(currentFolder, text);
            if (files.isFolder(path)) return new ListFolder(path);
            if (files.exists(path)) return new ShowFile(path);
            throw new AppException("Not found: " + path);
        }

        return allDrives
                ? new NameSearch(text, files.roots(), "all drives")
                : new NameSearch(text, List.of(currentFolder), currentFolder);
    }

    /** Slow: executes the plan, delivering results one by one to {@code onResult}. */
    public Result run(Plan plan, Consumer<FileInfo> onResult, BooleanSupplier isCancelled)
            throws AppException {
        int[] found = {0};
        Consumer<FileInfo> counting = info -> {
            found[0]++;
            onResult.accept(info);
        };

        int skipped;
        if (plan instanceof ListFolder lf) {
            skipped = files.listFolder(lf.folder(), counting, isCancelled);
        } else if (plan instanceof ShowFile sf) {
            counting.accept(files.info(sf.file()));
            skipped = 0;
        } else if (plan instanceof NameSearch ns) {
            skipped = files.walk(ns.roots(), containsIgnoreCase(ns.query()), counting, isCancelled);
        } else {
            throw new IllegalStateException("Unknown plan: " + plan);
        }
        return new Result(found[0], skipped);
    }

    static Predicate<String> containsIgnoreCase(String query) {
        String q = query.toLowerCase(Locale.ROOT);
        return name -> name.toLowerCase(Locale.ROOT).contains(q);
    }

    static boolean looksLikePath(String text) {
        return text.contains("/") || text.contains("\\")
                || text.startsWith("~")
                || text.equals(".") || text.equals("..")
                || text.matches("^[A-Za-z]:.*"); // Windows drive, e.g. C: or D:\Music
    }
}
