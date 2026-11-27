package explorer.file;

import explorer.error.AppException;

/** A filesystem problem with a user-friendly message. */
public class FileAccessException extends AppException {
    public FileAccessException(String userMessage) {
        super(userMessage);
    }

    public FileAccessException(String userMessage, Throwable cause) {
        super(userMessage, cause);
    }
}
