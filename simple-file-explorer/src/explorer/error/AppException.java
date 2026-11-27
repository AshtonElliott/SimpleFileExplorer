package explorer.error;

/**
 * An expected problem with a message that is safe to show to the user
 * (folder not found, permission denied, ...). Anything that is NOT an
 * AppException is treated as an unexpected bug by the ErrorHandler.
 */
public class AppException extends Exception {
    public AppException(String userMessage) {
        super(userMessage);
    }

    public AppException(String userMessage, Throwable cause) {
        super(userMessage, cause);
    }
}
