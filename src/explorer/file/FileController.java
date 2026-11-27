package explorer.file;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The ONLY gateway to the filesystem. Other modules pass paths as text and
 * get FileInfo objects back; they never touch java.nio.file themselves.
 *
 * Methods that can take a long time accept an "isCancelled" check so the
 * caller can stop them, and a consumer that receives results as they are found.
 * This class does no threading itself.
 */
public class FileController {

    /** Linux/macOS virtual filesystems: huge, not real files, and slow to walk. */
    private static final Set<String> SKIP_WHEN_WALKING = Set.of("/proc", "/sys", "/dev", "/run");

    // ---------- simple queries ----------

    public String startFolder() {
        return Paths.get(System.getProperty("user.dir")).toAbsolutePath().toString();
    }

    public List<String> roots() {
        List<String> list = new ArrayList<>();
        for (File r : File.listRoots()) list.add(r.getAbsolutePath());
        return list;
    }

    public boolean isFolder(String path) {
        try {
            return Files.isDirectory(Paths.get(path));
        } catch (InvalidPathException e) {
            return false;
        }
    }

    public boolean exists(String path) {
        try {
            return Files.exists(Paths.get(path));
        } catch (InvalidPathException e) {
            return false;
        }
    }

    /**
     * Turns user input into an absolute, clean path.
     * Relative input ("docs", "../x") is resolved against {@code base}; "~" means the home folder.
     */
    public String resolve(String base, String input) throws FileAccessException {
        try {
            String text = input.trim();
            if (text.equals("~") || text.startsWith("~/") || text.startsWith("~\\")) {
                text = System.getProperty("user.home") + text.substring(1);
            }
            Path p = Paths.get(text);
            if (!p.isAbsolute()) p = Paths.get(base).resolve(p);
            return p.toAbsolutePath().normalize().toString();
        } catch (InvalidPathException e) {
            throw new FileAccessException("Not a valid path: " + input, e);
        }
    }

    public Optional<String> parent(String path) {
        try {
            Path parent = Paths.get(path).toAbsolutePath().getParent();
            return Optional.ofNullable(parent).map(Path::toString);
        } catch (InvalidPathException e) {
            return Optional.empty();
        }
    }

    /** Every folder from the drive root down to {@code path}, e.g. [C:\, C:\Users, C:\Users\me]. */
    public List<String> pathChain(String path) {
        List<String> chain = new ArrayList<>();
        try {
            for (Path p = Paths.get(path).toAbsolutePath(); p != null; p = p.getParent()) {
                chain.add(0, p.toString());
            }
        } catch (InvalidPathException e) {
            chain.add(path);
        }
        return chain;
    }

    /** Last part of a path ("me" for C:\Users\me), or the whole thing for a drive root. */
    public String displayName(String path) {
        try {
            return nameOf(Paths.get(path));
        } catch (InvalidPathException e) {
            return path;
        }
    }

    /** Information about a single file or folder. */
    public FileInfo info(String path) throws FileAccessException {
        Path p = toPath(path);
        try {
            return toInfo(p, Files.readAttributes(p, BasicFileAttributes.class));
        } catch (IOException e) {
            throw translate(e, path);
        }
    }

    // ---------- long-running operations ----------

    /**
     * Lists the direct contents of a folder.
     * @return number of entries that could not be read (results are incomplete if > 0)
     */
    public int listFolder(String folder, Consumer<FileInfo> onResult, BooleanSupplier isCancelled)
            throws FileAccessException {
        Path dir = toPath(folder);
        int skipped = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path child : stream) {
                if (isCancelled.getAsBoolean()) break;
                BasicFileAttributes a = readAttributesLenient(child);
                if (a == null) {
                    skipped++;
                } else {
                    onResult.accept(toInfo(child, a));
                }
            }
        } catch (IOException e) {
            throw translate(e, folder);
        }
        return skipped;
    }

    /**
     * Walks every folder under each root and reports entries whose NAME matches.
     * The roots themselves are not reported. Unreadable folders are skipped, not fatal.
     * @return number of folders/files that could not be read
     */
    public int walk(List<String> roots, Predicate<String> nameMatches,
                    Consumer<FileInfo> onResult, BooleanSupplier isCancelled) {
        int[] skipped = {0};
        for (String rootText : roots) {
            if (isCancelled.getAsBoolean()) break;
            Path root;
            try {
                root = toPath(rootText);
            } catch (FileAccessException e) {
                skipped[0]++;
                continue;
            }
            try {
                Files.walkFileTree(root, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes a) {
                        if (isCancelled.getAsBoolean()) return FileVisitResult.TERMINATE;
                        if (dir.equals(root)) return FileVisitResult.CONTINUE;
                        if (SKIP_WHEN_WALKING.contains(dir.toString())) return FileVisitResult.SKIP_SUBTREE;
                        if (nameMatches.test(nameOf(dir))) onResult.accept(FolderEntry.from(dir, a));
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes a) {
                        if (isCancelled.getAsBoolean()) return FileVisitResult.TERMINATE;
                        // Reuse the attributes the walker already read: no second disk access.
                        if (nameMatches.test(nameOf(file))) onResult.accept(toInfo(file, a));
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException e) {
                        skipped[0]++; // e.g. permission denied; keep going
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                skipped[0]++;
            }
        }
        return skipped[0];
    }

    // ---------- open (kept from the original version) ----------

    /** Opens a file with the system's default application. */
    public void open(String path) throws FileAccessException {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            throw new FileAccessException("Opening files is not supported on this system.");
        }
        File f = toPath(path).toFile();
        if (!f.exists()) throw new FileAccessException("File no longer exists: " + path);
        try {
            Desktop.getDesktop().open(f);
        } catch (IOException | SecurityException e) {
            throw new FileAccessException("Could not open " + f.getName() + ": " + e.getMessage(), e);
        }
    }

    // ---------- helpers (package-private ones are used by FileEntry / FolderEntry) ----------

    static String nameOf(Path p) {
        Path name = p.getFileName();
        return name != null ? name.toString() : p.toString(); // drive roots have no file name
    }

    static Instant instant(FileTime t) {
        return t == null ? null : t.toInstant();
    }

    private static FileInfo toInfo(Path p, BasicFileAttributes a) {
        return a.isDirectory() ? FolderEntry.from(p, a) : FileEntry.from(p, a);
    }

    /** Follows links; for a broken link falls back to the link itself; null if unreadable. */
    private static BasicFileAttributes readAttributesLenient(Path p) {
        try {
            return Files.readAttributes(p, BasicFileAttributes.class);
        } catch (IOException e) {
            try {
                return Files.readAttributes(p, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            } catch (IOException e2) {
                return null;
            }
        }
    }

    private static Path toPath(String path) throws FileAccessException {
        try {
            return Paths.get(path);
        } catch (InvalidPathException e) {
            throw new FileAccessException("Not a valid path: " + path, e);
        }
    }

    private static FileAccessException translate(IOException e, String path) {
        if (e instanceof NoSuchFileException) return new FileAccessException("Not found: " + path, e);
        if (e instanceof AccessDeniedException) return new FileAccessException("Permission denied: " + path, e);
        if (e instanceof NotDirectoryException) return new FileAccessException("Not a folder: " + path, e);
        return new FileAccessException("Could not read " + path + " (" + e.getMessage() + ")", e);
    }
}
