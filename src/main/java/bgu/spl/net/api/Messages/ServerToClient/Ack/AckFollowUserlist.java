package bgu.spl.net.api.Messages.ServerToClient.Ack;

public class AckFollowUserlist extends Ack {
    private short numOfUsers;
    private String[] userNameList;

    public AckFollowUserlist(short opcodeRespose, short numOfUsers, String[] userNameList) {
        super(opcodeRespose);
        this.numOfUsers = numOfUsers;
        this.userNameList = userNameList;
    }


    public short getNumOfUsers() {
        return numOfUsers;
    }

    public String[] getUserNameList() {
        return userNameList;
    }
}
