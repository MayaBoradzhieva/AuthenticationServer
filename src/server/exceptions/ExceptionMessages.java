package server.exceptions;

public enum ExceptionMessages {
    USERNAME_EXISTS("Username already exists."),
    INCORRECT_USERNAME("Incorrect username."),
    INCORRECT_PASSWORD("Incorrect password."),
    LOCKED_USER("Too many invalid attempts to log in. Try again later."),
    SESSION_EXPIRED("Your session has expired. Please log in with username and password."),
    ALREADY_LOGGED_OUT("You are already logged out."), 
    NO_AUTHORIZATION("Not authorized to make the operation."),
    USER_NOT_FOUND("No user with such username"),
    ADMIN_NOT_FOUND("No administrator with such username");

    private String message;

    private ExceptionMessages(String message) {
        this.message = message;
    }

    public String getExceptionMessage() {
        return message;
    }
}
