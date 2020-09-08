package user;

public class User {

    private String username;
    private transient String password;
    private String encryptedPassword;
    private String firstName;
    private String lastName;
    private String email;

    public User(String username, String password, String firstName, String lastName, String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.encryptedPassword = encryptPassword(password);
    }

    private String encryptPassword(String password) {

        final char key = 'M';
        int[] tempPassword = new int[password.length()];

        for (int i = 0; i < password.length(); i++) {
            tempPassword[i] = (password.charAt(i) ^ key); // using XOR crypting
        }

        return tempPassword.toString();
    }

    public String getUsername() {
        return username;
    }

    public boolean isValidPassword(String password) {
        return this.password.equals(password);
    }

    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    public boolean setPassword(String oldPassword, String newPassword) {
        if (oldPassword.equals(password)) {
            this.password = newPassword;
            return true;
        }
        return false;
    }

    public void setFirstName(String newFirstName) {
        this.firstName = newFirstName;
    }

    public void setLastName(String newLastName) {
        this.lastName = newLastName;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}
