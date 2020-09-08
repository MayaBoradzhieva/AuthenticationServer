package commands.unsecured;

import java.nio.channels.SocketChannel;

import commands.Command;
import commands.exceptions.InvalidCommandException;
import server.data.ServerData;
import server.exceptions.UsernameAlreadyExistsException;

public class RegisterCommand implements Command {

    private static final int COMMAND_LENGTH = 11;

    @Override
    public void execute(String[] tokens, ServerData serverData, SocketChannel socketChannel)
            throws InvalidCommandException {

        if (tokens.length != COMMAND_LENGTH) {
            throw new InvalidCommandException("The command is invalid.");
        }

        String username = tokens[2].strip();
        String password = tokens[4].strip();
        String firstName = tokens[6].strip();
        String lastName = tokens[8].strip();
        String email = tokens[10].strip();

        try {
            serverData.registerUser(username, password, firstName, lastName, email, socketChannel);
        } catch (UsernameAlreadyExistsException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
