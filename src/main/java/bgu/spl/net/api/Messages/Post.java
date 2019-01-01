package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Messages.Message;

public class Post extends Message {
    private String postMessage;
    public Post(short opcode, String postMessage){
        super(opcode);
        this.postMessage = postMessage;
    }

    public String getPostMessage() {
        return postMessage;
    }
}

