package commands.enums;

public enum MainCommandsEnum {

    REGISTER("register"),
    LOGIN("login"),
    UPDATE_USER("update-user"),
    RESET_PASSWORD("reset-password"),
    LOGOUT("logout"),
    ADD_ADMIN_USER("add-admin-user"),
    REMOVE_ADMIN_USER("remove-admin-user"),
    DELETE_USER("delete-user");

    private String command;

    private MainCommandsEnum(String command) {
        this.command = command;
    }
    
    public String getCommand() {
        return command;
    }
}
