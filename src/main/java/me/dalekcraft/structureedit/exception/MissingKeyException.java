package me.dalekcraft.structureedit.exception;

/**
 * Thrown if a file is missing a required key for its schema.
 */
public class MissingKeyException extends Exception {

    public MissingKeyException() {
    }

    public MissingKeyException(String message) {
        super(message);
    }

    public MissingKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingKeyException(Throwable cause) {
        super(cause);
    }

    protected MissingKeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
