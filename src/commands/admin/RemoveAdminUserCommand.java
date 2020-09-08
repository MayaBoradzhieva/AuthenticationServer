package commands.admin;

import java.nio.channels.SocketChannel;

import commands.Command;
import commands.exceptions.InvalidCommandException;
import server.data.ServerData;
import server.exceptions.AdminNotFoundException;
import server.exceptions.NoAuthorizationException;
import server.exceptions.SessionExpiredException;

public class RemoveAdminUserCommand implements Command {

    private static final int COMMAND_LENGTH = 5;

    @Override
    public void execute(String[] tokens, ServerData serverData, SocketChannel socketChannel)
            throws InvalidCommandException {

        if (tokens.length != COMMAND_LENGTH) {
            throw new InvalidCommandException("The command is invalid.");
        }

        Integer currentSessionID = Integer.parseInt(tokens[2].strip());
        String username = tokens[4].strip();

        try {
            serverData.removeAdminUser(currentSessionID, username, socketChannel);
        } catch (AdminNotFoundException | NoAuthorizationException | SessionExpiredException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
