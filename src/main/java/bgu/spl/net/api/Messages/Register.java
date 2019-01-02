package bgu.spl.net.api.Messages;

public class Register extends Message {

    private String username;
    private String password;

    public Register( String username, String password) {
        super((short)1);
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
