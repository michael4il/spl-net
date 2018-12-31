package bgu.spl.net.api.Messages;

public class PM extends Message {
    private String username;
    private String content;

    public PM(short opcode, String username, String content) {
        super(opcode);
        this.username = username;
        this.content = content;
    }
}
