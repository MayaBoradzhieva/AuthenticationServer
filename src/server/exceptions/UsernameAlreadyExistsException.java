package server.exceptions;

public class UsernameAlreadyExistsException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 949302012582863103L;

    public UsernameAlreadyExistsException() {
        super();
    }

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
