package server.exceptions;

public class UserLockedException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 5992920689766284317L;

    public UserLockedException() {
        super();
    }

    public UserLockedException(String message) {
        super(message);
    }
}
