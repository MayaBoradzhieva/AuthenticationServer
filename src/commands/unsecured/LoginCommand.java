package commands.unsecured;

import java.nio.channels.SocketChannel;

import commands.Command;
import commands.exceptions.InvalidCommandException;
import server.data.ServerData;
import server.exceptions.IncorrectPasswordException;
import server.exceptions.IncorrectUsernameException;
import server.exceptions.SessionExpiredException;
import server.exceptions.UserLockedException;

public class LoginCommand implements Command {

    private static final String USERNAME = "--username";
    private static final String SESSION_ID = "--session-id";
    private static final int LOGIN_USERNAME_COMMAND_LENGTH = 5;
    private static final int LOGIN_SESSION_ID_COMMAND_LENGTH = 3;

    @Override
    public void execute(String[] tokens, ServerData serverData, SocketChannel socketChannel)
            throws InvalidCommandException {
        // with username and password or with session id
        String loginWith = tokens[1].strip();

        if (loginWith.equals(USERNAME) && tokens.length == LOGIN_USERNAME_COMMAND_LENGTH) {
            String username = tokens[2].strip();
            String password = tokens[4].strip();

            try {
                serverData.loginWithUsernameAndPassword(username, password, socketChannel);
            } catch (IncorrectPasswordException | IncorrectUsernameException | UserLockedException e) {
                e.printStackTrace();
            }

        } else if (loginWith.equals(SESSION_ID) && tokens.length == LOGIN_SESSION_ID_COMMAND_LENGTH) {
            Integer sessionID = Integer.parseInt(tokens[2].strip());

            try {
                serverData.loginWithSessionID(sessionID, socketChannel);
            } catch (SessionExpiredException e) {
                e.printStackTrace();
            }
        } else {
            throw new InvalidCommandException("The command is invalid. Try again.");
        }
    }

}
