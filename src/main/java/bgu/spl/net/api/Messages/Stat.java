package bgu.spl.net.api.Messages;

import bgu.spl.net.api.Messages.Message;

public class Stat extends Message {
    private String username;

    public Stat(short opcode, String username) {
        super(opcode);
        this.username = username;
    }
}
