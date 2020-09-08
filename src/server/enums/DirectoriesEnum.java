package server.enums;

public enum DirectoriesEnum {
    USER_INFO_DIR("C:\\Users\\ROG STRIX SCAR II\\eclipse-workspace\\ModernJavaTechnologies\\MyProjectMJT\\resources\\UserInformation.txt"),
    AUDIT_LOG_DIR("C:\\Users\\ROG STRIX SCAR II\\eclipse-workspace\\ModernJavaTechnologies\\MyProjectMJT\\resources\\AuditLog.txt"),
    EXCEPTION_LOGGER_DIR("C:\\Users\\ROG STRIX SCAR II\\eclipse-workspace\\ModernJavaTechnologies\\MyProjectMJT\\resources\\ExceptionLogger.txt");
    
    private String directory;

    private DirectoriesEnum(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }
}
