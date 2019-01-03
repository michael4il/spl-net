package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Messages.Message;

public class Stat extends Message {
    private String username;

    public Stat( String username) {
        super((short)8);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
