package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Messages.Message;

public class Post extends Message {
    private String postMessage;
    public Post(String postMessage){
        super((short)5);
        this.postMessage = postMessage;
    }

    public String getPostMessage() {
        return postMessage;
    }
}

