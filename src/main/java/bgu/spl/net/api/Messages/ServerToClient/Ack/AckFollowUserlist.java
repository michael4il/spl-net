package bgu.spl.net.api.Messages.ServerToClient.Ack;

import java.util.Vector;

public class AckFollowUserlist extends Ack {
    private short numOfUsers;
    private Vector<String> userNameList;

    public AckFollowUserlist(short opcodeRespose, short numOfUsers, Vector<String> userNameList) {
        super(opcodeRespose);
        this.numOfUsers = numOfUsers;
        this.userNameList = userNameList;
    }


    public short getNumOfUsers() {
        return numOfUsers;
    }

    public Vector<String> getUserNameList() {
        return userNameList;
    }
}
