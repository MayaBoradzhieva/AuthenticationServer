package server.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import server.enums.AdminOperations;
import server.enums.DirectoriesEnum;
import server.enums.OperationResults;
import server.exceptions.AdminNotFoundException;
import server.exceptions.AlreadyLoggedOutException;
import server.exceptions.ExceptionMessages;
import server.exceptions.IncorrectPasswordException;
import server.exceptions.IncorrectUsernameException;
import server.exceptions.NoAuthorizationException;
import server.exceptions.SessionExpiredException;
import server.exceptions.UserLockedException;
import server.exceptions.UserNotFoundException;
import server.exceptions.UsernameAlreadyExistsException;
import session.Session;
import user.Admin;
import user.AuthenticatedUser;
import user.User;

public class ServerData {
    private static final int BUFFER_SIZE = 1024;

    private Map<String, User> registeredUsers; // username and user
    private Map<Integer, AuthenticatedUser> loggedInUsers; // sessionId and user
    private Map<String, Admin> administrators;
    private FailedAuthentications failedAuthentications;
    private AuditLog auditLog;
    private ByteBuffer buffer;

    private static long operationID = 0;

    public ServerData() {
        registeredUsers = new HashMap<String, User>();
        loggedInUsers = new HashMap<Integer, AuthenticatedUser>();
        administrators = new HashMap<String, Admin>();
        auditLog = new AuditLog();
        failedAuthentications = new FailedAuthentications();

        buffer = ByteBuffer.allocate(BUFFER_SIZE);
    }

    private void saveUserInfo(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);

        try (BufferedWriter userInfo = new BufferedWriter(
                new FileWriter(DirectoriesEnum.USER_INFO_DIR.getDirectory(), true))) {
            userInfo.newLine();
            userInfo.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void send(String message, SocketChannel socketChannel) {
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();

        try {
            socketChannel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isRegistered(String username) {
        return registeredUsers.containsKey(username);
    }

    private boolean isLoggedIn(Integer sessionID) {
        if (loggedInUsers.containsKey(sessionID)) {
            Session session = loggedInUsers.get(sessionID).getUserSession();

            if (session.checkIfExpired()) {
                loggedInUsers.remove(sessionID);
                return false; // if the user is not logged in -> session has expired
            }
            return true; // the user is logged in
        }

        return false;
    }

    private void checkForExpiredSession() {
        for (Map.Entry<Integer, AuthenticatedUser> entry : loggedInUsers.entrySet()) {
            Integer currentSessionID = entry.getKey();
            Session currentUserSession = entry.getValue().getUserSession();

            if (currentUserSession.checkIfExpired()) {
                loggedInUsers.remove(currentSessionID);
            }

        }
    }

    private void invalidateSession(String username) {

        if (loggedInUsers.isEmpty()) {
            return;
        }

        for (Map.Entry<Integer, AuthenticatedUser> entry : loggedInUsers.entrySet()) {
            Integer currentSessionID = entry.getKey();
            String currentUsername = entry.getValue().getUsername();

            if (currentUsername.equals(username)) {
                loggedInUsers.remove(currentSessionID);
                return;
            }
        }
    }

    private boolean isAdmin(String username) {
        return administrators.containsKey(username);
    }

    public void registerUser(String username, String password, String firstName, String lastName, String email,
            SocketChannel socketChannel) throws UsernameAlreadyExistsException {
        if (!isRegistered(username)) {

            if (registeredUsers.isEmpty()) { // the first registered user is the first Admin
                Admin firstAdmin = new Admin(username);
                administrators.put(username, firstAdmin);
            }

            User newUser = new User(username, password, firstName, lastName, email);
            registeredUsers.put(username, newUser);
            saveUserInfo(newUser); // save user info into file

            AuthenticatedUser newAuthenticatedUser = new AuthenticatedUser(username);
            Session currentSession = newAuthenticatedUser.getUserSession();

            loggedInUsers.put(currentSession.getSessionID(), newAuthenticatedUser);

            final String message = "Registration successful. Your current session ID is: "
                    + currentSession.getSessionID();

            send(message, socketChannel);
        } else {
            send(ExceptionMessages.USERNAME_EXISTS.getExceptionMessage(), socketChannel);

            throw new UsernameAlreadyExistsException(ExceptionMessages.USERNAME_EXISTS.getExceptionMessage());
        }
    }

    public void loginWithUsernameAndPassword(String username, String password, SocketChannel socketChannel)
            throws IncorrectPasswordException, IncorrectUsernameException, UserLockedException {
        if (!isRegistered(username)) { // no user with such username

            send(ExceptionMessages.INCORRECT_USERNAME.getExceptionMessage(), socketChannel);
            auditLog.logFailedLogin(username, socketChannel);
            throw new IncorrectUsernameException(ExceptionMessages.INCORRECT_USERNAME.getExceptionMessage());
        }

        User currentUser = registeredUsers.get(username);
        boolean isCorrectPassword = currentUser.isValidPassword(password);

        if (!isCorrectPassword) {
            send(ExceptionMessages.INCORRECT_PASSWORD.getExceptionMessage(), socketChannel);
            failedAuthentications.addFailedAuthentication(username);

            auditLog.logFailedLogin(username, socketChannel);
            throw new IncorrectPasswordException(ExceptionMessages.INCORRECT_PASSWORD.getExceptionMessage());
        }

        if (failedAuthentications.isLocked(username)) {
            auditLog.logFailedLogin(username, socketChannel);
            send(ExceptionMessages.LOCKED_USER.getExceptionMessage(), socketChannel);

            throw new UserLockedException(ExceptionMessages.LOCKED_USER.getExceptionMessage());
        }

        // first check if the user is not already logged in and then invalidate session
        invalidateSession(username);

        AuthenticatedUser newAuthenticatedUser = new AuthenticatedUser(username);
        Session currentSession = newAuthenticatedUser.getUserSession();

        loggedInUsers.put(currentSession.getSessionID(), newAuthenticatedUser);

        final String message = "Login with username and password successful. Your current session ID is: "
                + currentSession.getSessionID();

        send(message, socketChannel);
    }

    public void loginWithSessionID(Integer sessionID, SocketChannel socketChannel) throws SessionExpiredException {
        // means the user is already
        // authenticated (has sessionID)

        if (!isLoggedIn(sessionID)) {
            send(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage(), socketChannel);

            throw new SessionExpiredException(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage());
        }

        final String message = "Login with session ID: " + sessionID + " successful.";
        send(message, socketChannel);

    }

    public void updateUsername(Integer sessionID, String newUsername, SocketChannel socketChannel)
            throws SessionExpiredException {
        if (!isLoggedIn(sessionID)) {
            send(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage(), socketChannel);

            throw new SessionExpiredException(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage());
        }

        String correspondingUsername = loggedInUsers.get(sessionID).getUsername();
        registeredUsers.get(correspondingUsername).setUsername(newUsername);
        loggedInUsers.get(sessionID).setUsername(newUsername);

        final String message = "Username successfully updated.";
        send(message, socketChannel);
    }

    public void updateFirstName(Integer sessionID, String newFirstName, SocketChannel socketChannel)
            throws SessionExpiredException {
        if (!isLoggedIn(sessionID)) {
            send(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage(), socketChannel);

            throw new SessionExpiredException(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage());
        }

        String correspondingUsername = loggedInUsers.get(sessionID).getUsername();
        registeredUsers.get(correspondingUsername).setFirstName(newFirstName);

        final String message = "First name successfully updated.";
        send(message, socketChannel);
    }

    public void updateLastName(Integer sessionID, String newLastName, SocketChannel socketChannel)
            throws SessionExpiredException {
        if (!isLoggedIn(sessionID)) {
            send(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage(), socketChannel);

            throw new SessionExpiredException(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage());
        }

        String correspondingUsername = loggedInUsers.get(sessionID).getUsername();
        registeredUsers.get(correspondingUsername).setLastName(newLastName);

        final String message = "Last name successfully updated.";
        send(message, socketChannel);
    }

    public void updateEmail(Integer sessionID, String newEmail, SocketChannel socketChannel)
            throws SessionExpiredException {
        if (!isLoggedIn(sessionID)) {
            send(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage(), socketChannel);

            throw new SessionExpiredException(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage());
        }

        String correspondingUsername = loggedInUsers.get(sessionID).getUsername();
        registeredUsers.get(correspondingUsername).setEmail(newEmail);

        final String message = "Email successfully updated.";
        send(message, socketChannel);
    }

    public void changePassword(Integer sessionID, String username, String oldPassword, String newPassword,
            SocketChannel socketChannel)
            throws SessionExpiredException, IncorrectPasswordException, IncorrectUsernameException {
        if (!isLoggedIn(sessionID)) {
            send(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage(), socketChannel);

            throw new SessionExpiredException(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage());
        }

        String correspondingUsername = loggedInUsers.get(sessionID).getUsername();

        if (!correspondingUsername.equals(username)) {
            send(ExceptionMessages.INCORRECT_USERNAME.getExceptionMessage(), socketChannel);

            throw new IncorrectUsernameException(ExceptionMessages.INCORRECT_USERNAME.getExceptionMessage());
        }

        boolean changeIsSuccessful = registeredUsers.get(username).setPassword(oldPassword, newPassword);

        if (!changeIsSuccessful) {
            send(ExceptionMessages.INCORRECT_PASSWORD.getExceptionMessage(), socketChannel);

            throw new IncorrectPasswordException(ExceptionMessages.INCORRECT_PASSWORD.getExceptionMessage());
        }

        final String message = "Your password has been changed successfully.";

        send(message, socketChannel);
    }

    public void logOut(Integer sessionID, SocketChannel socketChannel) throws AlreadyLoggedOutException {

        if (!isLoggedIn(sessionID)) {

            send(ExceptionMessages.ALREADY_LOGGED_OUT.getExceptionMessage(), socketChannel);

            throw new AlreadyLoggedOutException(ExceptionMessages.ALREADY_LOGGED_OUT.getExceptionMessage());
        }

        loggedInUsers.remove(sessionID);
        final String message = "Logout successful.";
        send(message, socketChannel);

    }

    public void addAdminUser(Integer sessionID, String username, SocketChannel socketChannel)
            throws UserNotFoundException, NoAuthorizationException, SessionExpiredException {
        if (!isLoggedIn(sessionID)) {
            send(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage(), socketChannel);

            throw new SessionExpiredException(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage());
        }

        // get admin username
        String adminUsername = loggedInUsers.get(sessionID).getUsername();
        auditLog.logStartOfConfigurationChange(operationID, adminUsername, username, AdminOperations.ADD_RIGHTS,
                socketChannel);

        // with adminUsername check the administrators -> if he really is and admin
        if (!isAdmin(adminUsername)) {
            send(ExceptionMessages.NO_AUTHORIZATION.getExceptionMessage(), socketChannel);
            auditLog.logResultOfConfigurationChange(operationID, adminUsername, username,
                    OperationResults.OPERATION_FAILED, socketChannel);

            throw new NoAuthorizationException(ExceptionMessages.NO_AUTHORIZATION.getExceptionMessage());
        }

        // check if the admin candidate is in registered users
        if (!isRegistered(username)) { // add to administrators
            send(ExceptionMessages.USER_NOT_FOUND.getExceptionMessage(), socketChannel);
            auditLog.logResultOfConfigurationChange(operationID, adminUsername, username,
                    OperationResults.OPERATION_FAILED, socketChannel);

            throw new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND.getExceptionMessage());
        }

        final String message = "Successfully made " + username + " an admin.";
        send(message, socketChannel);

        administrators.put(username, new Admin(username));
        auditLog.logResultOfConfigurationChange(operationID, adminUsername, username,
                OperationResults.OPERATION_SUCCESSFUL, socketChannel);

        operationID++;
    }

    public void removeAdminUser(Integer sessionID, String username, SocketChannel socketChannel)
            throws AdminNotFoundException, NoAuthorizationException, SessionExpiredException {
        if (!isLoggedIn(sessionID)) {

            send(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage(), socketChannel);

            throw new SessionExpiredException(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage());
        }
        
        String adminUsername = loggedInUsers.get(sessionID).getUsername();
        auditLog.logStartOfConfigurationChange(operationID, adminUsername, username, AdminOperations.REMOVE_RIGHTS,
                socketChannel);

        if (!isAdmin(adminUsername)) {
            send(ExceptionMessages.NO_AUTHORIZATION.getExceptionMessage(), socketChannel);
            auditLog.logResultOfConfigurationChange(operationID, adminUsername, username,
                    OperationResults.OPERATION_FAILED, socketChannel);

            throw new NoAuthorizationException(ExceptionMessages.NO_AUTHORIZATION.getExceptionMessage());
        }
        
        if (!isAdmin(username)) {
           
            send(ExceptionMessages.ADMIN_NOT_FOUND.getExceptionMessage(), socketChannel);
            auditLog.logResultOfConfigurationChange(operationID, adminUsername, username,
                    OperationResults.OPERATION_FAILED, socketChannel);

            throw new AdminNotFoundException(ExceptionMessages.ADMIN_NOT_FOUND.getExceptionMessage());
        }
 
        administrators.remove(username);

        final String message = "The admin was removed successfully.";
        send(message, socketChannel);

        auditLog.logResultOfConfigurationChange(operationID, adminUsername, username,
                OperationResults.OPERATION_SUCCESSFUL, socketChannel);


        operationID++;

    }

    public void deleteUser(Integer sessionID, String username, SocketChannel socketChannel)
            throws UserNotFoundException, NoAuthorizationException, SessionExpiredException {

        if (!isLoggedIn(sessionID)) {

            send(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage(), socketChannel);
            throw new SessionExpiredException(ExceptionMessages.SESSION_EXPIRED.getExceptionMessage());
        }
        

        String adminUsername = loggedInUsers.get(sessionID).getUsername();

        if (!isAdmin(adminUsername)) {
            
            send(ExceptionMessages.NO_AUTHORIZATION.getExceptionMessage(), socketChannel);
            throw new NoAuthorizationException(ExceptionMessages.NO_AUTHORIZATION.getExceptionMessage());
        }
        
        if (!isRegistered(username)) {
            
            send(ExceptionMessages.USER_NOT_FOUND.getExceptionMessage(), socketChannel);
            throw new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND.getExceptionMessage());
        }
        
        // first log out the user
        invalidateSession(username);

        // then remove it from registered users
        registeredUsers.remove(username);

        final String message = "The user was deleted successfully.";
        send(message, socketChannel);

    }

}
