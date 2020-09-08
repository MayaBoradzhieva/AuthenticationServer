package server.enums;

public enum AdminOperations {

    ADD_RIGHTS("Added rights."),
    REMOVE_RIGHTS("Removed rights.");

    private String operation;

    private AdminOperations(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }
}
