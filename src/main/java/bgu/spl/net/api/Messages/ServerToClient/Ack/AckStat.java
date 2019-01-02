package bgu.spl.net.api.Messages.ServerToClient.Ack;

import bgu.spl.net.api.Messages.Message;

public class AckStat extends Ack {
    private short numOfPosts;
    private short numOfFollowers;
    private short numOfFollowing;

    public AckStat(short opcodeRespose, short numOfPosts, short numOfFollowers, short numOfFollowing) {
        super(opcodeRespose);
        this.numOfPosts = numOfPosts;
        this.numOfFollowers = numOfFollowers;
        this.numOfFollowing = numOfFollowing;
    }


    public short getNumOfPosts() {
        return numOfPosts;
    }

    public short getNumOfFollowers() {
        return numOfFollowers;
    }

    public short getNumOfFollowing() {
        return numOfFollowing;
    }

}
