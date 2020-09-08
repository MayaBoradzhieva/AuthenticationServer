package server.exceptions;

public class UserNotFoundException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -5124200239009089574L;

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
