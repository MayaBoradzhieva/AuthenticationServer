package commands.secured;

import java.nio.channels.SocketChannel;

import commands.Command;
import commands.exceptions.InvalidCommandException;
import server.data.ServerData;
import server.exceptions.AlreadyLoggedOutException;

public class LogOutCommand implements Command {

    private static final int COMMAND_LENGTH = 3;

    @Override
    public void execute(String[] tokens, ServerData serverData, SocketChannel socketChannel)
            throws InvalidCommandException {

        if (tokens.length != COMMAND_LENGTH) {
            throw new InvalidCommandException("The command is invalid.");
        }

        Integer currentSessionID = Integer.parseInt(tokens[2].strip());

        try {
            serverData.logOut(currentSessionID, socketChannel);
        } catch (AlreadyLoggedOutException e) {
            e.printStackTrace();
            
        }
    }

}
