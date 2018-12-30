package bgu.spl.net.api.Messages;

public class Login extends Message {
    private String username;
    private String password;

    public Login(short opcode, String username, String password) {
        super(opcode);
        this.username = username;
        this.password = password;
    }
}
