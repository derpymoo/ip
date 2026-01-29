package shinchan.exception;

/**
 * Represents an exception specific to the Shinchan application.
 */
public class ShinchanException extends Exception {

    /**
     * Creates a ShinchanException with the specified error message.
     *
     * @param message Error message describing the exception
     */
    public ShinchanException(String message) {
        super(message);
    }
}
