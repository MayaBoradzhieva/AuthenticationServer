package commands.secured;

import java.nio.channels.SocketChannel;

import commands.Command;
import commands.exceptions.InvalidCommandException;
import server.data.ServerData;
import server.exceptions.IncorrectPasswordException;
import server.exceptions.IncorrectUsernameException;
import server.exceptions.SessionExpiredException;

public class ResetPasswordCommand implements Command {

    private static final int COMMAND_LENGTH = 9;

    @Override
    public void execute(String[] tokens, ServerData serverData, SocketChannel socketChannel)
            throws InvalidCommandException {

        if (tokens.length != COMMAND_LENGTH) {
            throw new InvalidCommandException("The command is invalid.");
        }

        Integer currentUserSessionID = Integer.parseInt(tokens[2].strip());
        String username = tokens[4].strip();
        String oldPassword = tokens[6].strip();
        String newPassword = tokens[8].strip();

        try {
            serverData.changePassword(currentUserSessionID, username, oldPassword, newPassword, socketChannel);
        } catch (SessionExpiredException | IncorrectPasswordException | IncorrectUsernameException e) {
            e.printStackTrace();
        }

    }
}
