package explorer.file;

import java.time.Instant;

/**
 * Read-only information about one file or folder.
 * This is the only thing other modules ever receive from the File Controller;
 * they never see a java.nio Path.
 */
public sealed interface FileInfo permits FileEntry, FolderEntry {
    String name();

    /** Absolute path as text. */
    String path();

    /** e.g. "Folder", "PDF file", "File". */
    String type();

    /** Size in bytes, or -1 for folders. */
    long size();

    /** May be null if the system does not provide it. */
    Instant created();

    /** May be null if the system does not provide it. */
    Instant modified();

    default boolean isFolder() {
        return this instanceof FolderEntry;
    }
}
