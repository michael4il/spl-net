package bgu.spl.net.api.Messages.ServerToClient;

import bgu.spl.net.api.Messages.Message;

public class Notification extends Message {
    private char PMorPost;
    private String postingUser;
    private String content;

    public Notification(short opcode, char PMorPost, String postingUser, String content) {
        super(opcode);
        this.PMorPost = PMorPost;
        this.postingUser = postingUser;
        this.content = content;
    }

    public char getPMorPost() {
        return PMorPost;
    }

    public String getPostingUser() {
        return postingUser;
    }

    public String getContent() {
        return content;
    }
}
