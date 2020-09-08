package server.enums;

public enum OperationResults {

    OPERATION_SUCCESSFUL("Operation is successful."),
    OPERATION_FAILED("Operation failed.");

    private String result;

    private OperationResults(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

}
