package commands.exceptions;

public class InvalidCommandException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 2066461654398946053L;

    public InvalidCommandException() {
        super();
    }

    public InvalidCommandException(String message) {
        super(message);
    }
}
