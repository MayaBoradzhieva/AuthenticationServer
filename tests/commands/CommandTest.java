package commands;

import java.nio.channels.SocketChannel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import server.data.ServerData;
import server.exceptions.AdminNotFoundException;
import server.exceptions.AlreadyLoggedOutException;
import server.exceptions.IncorrectPasswordException;
import server.exceptions.IncorrectUsernameException;
import server.exceptions.NoAuthorizationException;
import server.exceptions.SessionExpiredException;
import server.exceptions.UserLockedException;
import server.exceptions.UserNotFoundException;
import server.exceptions.UsernameAlreadyExistsException;

public class CommandTest {

    // private ServerData serverData;

    @Before
    public void setUp() {
        // serverData = new ServerData();
    }

    @Test(expected = UsernameAlreadyExistsException.class)
    public void testSimilarUsernamesThrowsException() throws UsernameAlreadyExistsException {
        ServerData serverDataMock = Mockito.mock(ServerData.class);
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);

        String username = "username123";
        String password = "password123";
        String firstName = "firstName1";
        String lastName = "lastName1";
        String email = "email@gmail.com";

        serverDataMock.registerUser(username, password, firstName, lastName, email, null);

        String secondUsername = "username123";
        String secondPassword = "password123";
        String secondFirstName = "firstName1";
        String secondLastName = "lastName1";
        String secondEmail = "email@gmail.com";

        serverDataMock.registerUser(secondUsername, secondPassword, secondFirstName, secondLastName, secondEmail, null);
    }

    @Test(expected = IncorrectUsernameException.class)
    public void testIncorrectUsernameThrowsException() throws UsernameAlreadyExistsException,
            IncorrectPasswordException, IncorrectUsernameException, UserLockedException {
        ServerData serverDataMock = Mockito.mock(ServerData.class);

        String username = "username123";
        String password = "password123";
        String firstName = "firstName1";
        String lastName = "lastName1";
        String email = "email@gmail.com";

        serverDataMock.registerUser(username, password, firstName, lastName, email, null);

        serverDataMock.loginWithUsernameAndPassword("differentUsername", password, null);
    }

    @Test(expected = IncorrectPasswordException.class)
    public void testIncorrectPasswordThrowsException() throws UsernameAlreadyExistsException,
            IncorrectPasswordException, IncorrectUsernameException, UserLockedException {
        ServerData serverDataMock = Mockito.mock(ServerData.class);

        String username = "username123";
        String password = "password123";
        String firstName = "firstName1";
        String lastName = "lastName1";
        String email = "email@gmail.com";

        serverDataMock.registerUser(username, password, firstName, lastName, email, null);

        serverDataMock.loginWithUsernameAndPassword(username, "differentPassword", null);
    }

    @Test(expected = AlreadyLoggedOutException.class)
    public void testLogOutThrowsException() throws AlreadyLoggedOutException {
        ServerData serverDataMock = Mockito.mock(ServerData.class);

        final int sessionID = 1;
        serverDataMock.logOut(sessionID, null);
    }

    @Test(expected = NoAuthorizationException.class)
    public void testNoAuthorizationThrowsException() throws UsernameAlreadyExistsException, UserNotFoundException,
            NoAuthorizationException, SessionExpiredException {
        ServerData serverDataMock = Mockito.mock(ServerData.class);

        String username = "AdminUsername"; // first registered user is admin
        String password = "password123";
        String firstName = "firstName1";
        String lastName = "lastName1";
        String email = "email@gmail.com";

        serverDataMock.registerUser(username, password, firstName, lastName, email, null);

        String secondUsername = "username123";
        String secondPassword = "password123";
        String secondFirstName = "firstName1";
        String secondLastName = "lastName1";
        String secondEmail = "email@gmail.com";

        serverDataMock.registerUser(secondUsername, secondPassword, secondFirstName, secondLastName, secondEmail, null);

        serverDataMock.addAdminUser(2, "randomName", null);
    }

    @Test(expected = AdminNotFoundException.class)
    public void testRemoveAdminThrowsException() throws UsernameAlreadyExistsException, AdminNotFoundException,
            NoAuthorizationException, SessionExpiredException {
        ServerData serverDataMock = Mockito.mock(ServerData.class);

        String username = "AdminUsername"; // first registered user is admin
        String password = "password123";
        String firstName = "firstName1";
        String lastName = "lastName1";
        String email = "email@gmail.com";

        serverDataMock.registerUser(username, password, firstName, lastName, email, null);

        String secondUsername = "username123";
        String secondPassword = "password123";
        String secondFirstName = "firstName1";
        String secondLastName = "lastName1";
        String secondEmail = "email@gmail.com";

        serverDataMock.registerUser(secondUsername, secondPassword, secondFirstName, secondLastName, secondEmail, null);

        serverDataMock.removeAdminUser(2, secondUsername, null);
    }

}
