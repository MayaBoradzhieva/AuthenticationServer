package commands;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import commands.admin.AddAdminUserCommand;
import commands.admin.DeleteUserCommand;
import commands.admin.RemoveAdminUserCommand;
import commands.enums.MainCommandsEnum;
import commands.exceptions.InvalidCommandException;
import commands.secured.LogOutCommand;
import commands.secured.ResetPasswordCommand;
import commands.secured.UpdateUserCommand;
import commands.unsecured.LoginCommand;
import commands.unsecured.RegisterCommand;
import server.data.ServerData;

public class CommandExecutor {

    private Map<String, Command> commands;

    public CommandExecutor() {
        commands = new HashMap<String, Command>();
        setUpCommands();
    }

    private void setUpCommands() {
        commands.put(MainCommandsEnum.REGISTER.getCommand(), new RegisterCommand());
        commands.put(MainCommandsEnum.LOGIN.getCommand(), new LoginCommand());
        commands.put(MainCommandsEnum.UPDATE_USER.getCommand(), new UpdateUserCommand());
        commands.put(MainCommandsEnum.RESET_PASSWORD.getCommand(), new ResetPasswordCommand());
        commands.put(MainCommandsEnum.LOGOUT.getCommand(), new LogOutCommand());
        commands.put(MainCommandsEnum.ADD_ADMIN_USER.getCommand(), new AddAdminUserCommand());
        commands.put(MainCommandsEnum.REMOVE_ADMIN_USER.getCommand(), new RemoveAdminUserCommand());
        commands.put(MainCommandsEnum.DELETE_USER.getCommand(), new DeleteUserCommand());
    }

    public void processLine(String line, ServerData serverData, SocketChannel socketChannel)
            throws InvalidCommandException {

        if (line == null) {
            throw new InvalidCommandException("Invalid command. Try again.");
        }

        String[] tokens = line.split(" ");

        String currentCommand = tokens[0].strip();

        if (!commands.containsKey(currentCommand)) {
            throw new InvalidCommandException("Invalid command. Try again.");
        }

        commands.get(currentCommand).execute(tokens, serverData, socketChannel);

    }

}
