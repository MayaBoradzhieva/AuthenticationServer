package commands.secured;

import java.nio.channels.SocketChannel;

import commands.Command;
import commands.exceptions.InvalidCommandException;
import server.data.ServerData;
import server.exceptions.SessionExpiredException;

public class UpdateUserCommand implements Command {

    private static final int COMMAND_LENGTH_MINIMUM = 3;
    private static final String NEW_USERNAME_COMMAND = "--new-username";
    private static final String NEW_FIRST_NAME_COMMAND = "--new-first-name";
    private static final String NEW_LAST_NAME_COMMAND = "--new-last-name";
    private static final String NEW_EMAIL_COMMAND = "--new-email";

    @Override
    public void execute(String[] tokens, ServerData serverData, SocketChannel socketChannel)
            throws InvalidCommandException {

        if (tokens.length < COMMAND_LENGTH_MINIMUM) {
            throw new InvalidCommandException("The command is invalid.");
        }

        int numberOfCommands = tokens.length;
        Integer currentUserSessionID = Integer.parseInt(tokens[2].strip());

        for (int i = 3; i < numberOfCommands; i += 2) {
            String currentCommand = tokens[i].strip();
            String data = tokens[i + 1].strip();

            switch (currentCommand) {
            case NEW_USERNAME_COMMAND:
                try {
                    serverData.updateUsername(currentUserSessionID, data, socketChannel);
                } catch (SessionExpiredException e) {
                    e.printStackTrace();
                }
                break;
            case NEW_FIRST_NAME_COMMAND:
                try {
                    serverData.updateFirstName(currentUserSessionID, data, socketChannel);
                } catch (SessionExpiredException e) {
                    e.printStackTrace();
                }
                break;
            case NEW_LAST_NAME_COMMAND:
                try {
                    serverData.updateLastName(currentUserSessionID, data, socketChannel);
                } catch (SessionExpiredException e) {
                    e.printStackTrace();
                }
                break;
            case NEW_EMAIL_COMMAND:
                try {
                    serverData.updateEmail(currentUserSessionID, data, socketChannel);
                } catch (SessionExpiredException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

    }

}
