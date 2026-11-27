package explorer.file;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

/** Information about a folder (directory or drive root). */
public record FolderEntry(String name, String path, Instant created, Instant modified)
        implements FileInfo {

    @Override public String type() { return "Folder"; }

    @Override public long size() { return -1; }

    static FolderEntry from(Path p, BasicFileAttributes a) {
        return new FolderEntry(FileController.nameOf(p), p.toAbsolutePath().toString(),
                FileController.instant(a.creationTime()), FileController.instant(a.lastModifiedTime()));
    }
}
