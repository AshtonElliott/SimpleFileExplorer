package explorer.file;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Locale;

/** Information about a regular file. */
public record FileEntry(String name, String path, String type, long size,
                        Instant created, Instant modified) implements FileInfo {

    /** Built only by the File Controller, from attributes it already has (no extra disk read). */
    static FileEntry from(Path p, BasicFileAttributes a) {
        String name = FileController.nameOf(p);
        return new FileEntry(name, p.toAbsolutePath().toString(), typeOf(name, a), a.size(),
                FileController.instant(a.creationTime()), FileController.instant(a.lastModifiedTime()));
    }

    /** Type from the extension. Deliberately cheap: no content probing, for speed. */
    private static String typeOf(String name, BasicFileAttributes a) {
        if (a.isSymbolicLink()) return "Link";
        if (a.isOther()) return "Special file";
        int dot = name.lastIndexOf('.');
        if (dot > 0 && dot < name.length() - 1) {
            return name.substring(dot + 1).toUpperCase(Locale.ROOT) + " file";
        }
        return "File";
    }
}
