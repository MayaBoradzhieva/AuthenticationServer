package server.exceptions;

public class AdminNotFoundException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -7403040815709894629L;

    public AdminNotFoundException() {
        super();
    }

    public AdminNotFoundException(String message) {
        super(message);
    }
}
