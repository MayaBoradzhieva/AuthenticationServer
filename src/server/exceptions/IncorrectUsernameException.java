package server.exceptions;

public class IncorrectUsernameException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -411907570621111605L;

    public IncorrectUsernameException() {
        super();
    }

    public IncorrectUsernameException(String message) {
        super(message);
    }
}
