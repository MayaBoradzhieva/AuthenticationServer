package server.exceptions;

public class SessionExpiredException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 5073873016722956053L;

    public SessionExpiredException() {
        super();
    }

    public SessionExpiredException(String message) {
        super(message);
    }
}
