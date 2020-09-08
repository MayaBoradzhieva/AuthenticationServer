package server.exceptions;

public class NoAuthorizationException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -2624711339070071284L;

    public NoAuthorizationException() {
        super();
    }

    public NoAuthorizationException(String message) {
        super(message);
    }
}
