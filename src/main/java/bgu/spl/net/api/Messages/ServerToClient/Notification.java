package bgu.spl.net.api.Messages.ServerToClient;

import bgu.spl.net.api.Messages.Message;

public class Notification extends Message {
    private char PMorPost;//0 pm 1 post
    private String sendingUser;
    private String content;

    public Notification( char PMorPost, String sendingUser, String content) {
        super((short)9);
        this.PMorPost = PMorPost;
        this.sendingUser = sendingUser;
        this.content = content;
    }

    public char getPMorPost() {
        return PMorPost;
    }

    public String getSendingUser() {
        return sendingUser;
    }

    public String getContent() {
        return content;
    }
}
