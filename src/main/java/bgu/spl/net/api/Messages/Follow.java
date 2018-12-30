package bgu.spl.net.api.Messages;

public class Follow extends Message {
    private String userlist;
    private short numOfUsers;

    public Follow(short opcode, String userlist, short numOfUsers) {
        super(opcode);
    }
}
