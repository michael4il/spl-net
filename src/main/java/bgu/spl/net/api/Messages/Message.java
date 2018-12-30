package bgu.spl.net.api.Messages;

import bgu.spl.net.api.MessageMarker;

public class Message implements MessageMarker {
    public Message(short opcode){
        this.opcode = opcode;
    }

    private short opcode;

    public short getOpcode() {
        return opcode;
    }

    public void setOpcode(short opcode) {
        this.opcode = opcode;
    }
}
