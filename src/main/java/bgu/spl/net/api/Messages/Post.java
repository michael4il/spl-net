package bgu.spl.net.api.Messages;

public class Post extends Message {
    private String content;
    public Post(String content){
        super((short)5);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}

