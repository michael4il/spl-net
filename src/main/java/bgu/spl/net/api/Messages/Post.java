package bgu.spl.net.api.Messages;

public class Post extends Message {
    private String postMessage;
    public Post(short opcode, String postMessage){
        super(opcode);
        this.postMessage = postMessage;
    }
}
