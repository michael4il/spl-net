package bgu.spl.net.api.Messages;

public class Stat extends Message {
    private String username;

    public Stat(short opcode, String username) {
        super(opcode);
        this.username = username;
    }
}
