package server.exceptions;

public class AlreadyLoggedOutException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 3666726676153723434L;

    public AlreadyLoggedOutException() {
        super();
    }

    public AlreadyLoggedOutException(String message) {
        super(message);
    }
}
