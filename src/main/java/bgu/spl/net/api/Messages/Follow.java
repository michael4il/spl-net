package bgu.spl.net.api.Messages;

import java.util.Vector;

public class Follow extends Message {
    private Vector<String> userlist;
    private short numOfUsers;
    private boolean follow;

    public Follow( Vector<String> userlist, short numOfUsers,boolean follow) {
        super((short)4);
        this.userlist = userlist;
        this.numOfUsers = numOfUsers;
        this.follow = follow;
    }

    public Vector<String> getUserlist() {
        return userlist;
    }

    public short getNumOfUsers() {
        return numOfUsers;
    }

    public boolean isFollow() {
        return follow;
    }
}

