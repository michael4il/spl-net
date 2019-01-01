package bgu.spl.net.api.Messages;

public class Follow extends Message {
    private String[] userlist;
    private short numOfUsers;
    private boolean follow;

    public Follow(short opcode, String[] userlist, short numOfUsers,boolean follow) {
        super(opcode);
        this.userlist = userlist;
        this.numOfUsers = numOfUsers;
        this.follow = follow;
    }

    public String[] getUserlist() {
        return userlist;
    }

    public short getNumOfUsers() {
        return numOfUsers;
    }

    public boolean isFollow() {
        return follow;
    }
}

