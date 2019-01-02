package bgu.spl.net.api.Messages;

public class Login extends Message {
    private String username;
    private String password;

    public Login(String username, String password) {
        super((short)2);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
