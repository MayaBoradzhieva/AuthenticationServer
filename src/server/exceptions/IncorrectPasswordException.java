package server.exceptions;

public class IncorrectPasswordException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -5815002540314877203L;

    public IncorrectPasswordException() {
        super();
    }

    public IncorrectPasswordException(String message) {
        super(message);
    }
}
