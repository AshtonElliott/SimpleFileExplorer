package explorer.error;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Central error handling: tells the user something went wrong (short text)
 * and stores the full details in a log file.
 *
 * Log location: ~/.simple-explorer/error.log
 *
 * Note: this is the only module besides the File Controller that touches the
 * disk, and it only ever writes its own log file.
 */
public final class ErrorHandler {

    /** How the GUI receives messages. Implementations must be thread-safe. */
    public interface UserNotifier {
        /** @param serious true for crashes / bugs, false for expected problems */
        void showError(String message, boolean serious);
    }

    private static final Logger LOG = Logger.getLogger("explorer");
    private static UserNotifier notifier =
            (msg, serious) -> System.err.println((serious ? "[ERROR] " : "[WARN] ") + msg);
    private static String logPath = "(log unavailable)";

    private ErrorHandler() {}

    /** Call once at startup, before anything else. */
    public static void install() {
        try {
            File dir = new File(System.getProperty("user.home"), ".simple-explorer");
            if (!dir.isDirectory() && !dir.mkdirs()) {
                throw new IOException("Cannot create " + dir);
            }
            File logFile = new File(dir, "error.log");
            FileHandler handler = new FileHandler(logFile.getPath(), 1_000_000, 1, true);
            handler.setFormatter(new SimpleFormatter());
            LOG.addHandler(handler);
            LOG.setUseParentHandlers(false); // keep console clean; everything goes to file
            logPath = logFile.getPath();
        } catch (IOException e) {
            System.err.println("Could not open log file, logging to console only: " + e.getMessage());
        }

        // Last line of defence: any exception nobody caught (including on the Swing thread)
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            LOG.log(Level.SEVERE, "Uncaught exception on thread " + thread.getName(), ex);
            notifier.showError("Unexpected error: " + ex + ". Details saved to " + logPath, true);
        });
    }

    public static void setNotifier(UserNotifier n) {
        notifier = n;
    }

    /**
     * Report a problem.
     * @param context what the program was doing, e.g. "listing folder"
     */
    public static void report(String context, Throwable t) {
        if (t instanceof AppException) {
            LOG.log(Level.WARNING, context + ": " + t.getMessage(), t);
            notifier.showError(t.getMessage(), false);
        } else {
            LOG.log(Level.SEVERE, "Unexpected error while " + context, t);
            notifier.showError("Unexpected error while " + context
                    + ". Details saved to " + logPath, true);
        }
    }

    /**
     * A warning that results may be incomplete (e.g. some folders could not be read).
     * Logged, and returned as text for the caller to show.
     */
    public static void warn(String message) {
        LOG.warning(message);
    }

    public static String logPath() {
        return logPath;
    }
}
