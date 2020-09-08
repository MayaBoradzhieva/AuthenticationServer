package user;

import session.Session;

// authenticated user -> logged user
public class AuthenticatedUser {

    private String username;
    private Session currentSession;
    
    public AuthenticatedUser(String username) {
        this.username = username;
        this.currentSession = new Session();
    }

    public String getUsername() {
        return username;
    }

    public Session getUserSession() {
        return currentSession;
    }

    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

}
