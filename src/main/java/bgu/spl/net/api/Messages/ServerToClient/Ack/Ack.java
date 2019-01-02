package bgu.spl.net.api.Messages.ServerToClient.Ack;

import bgu.spl.net.api.Messages.Message;

public class Ack extends Message {
    private short opcodeRespose;

    public Ack( short opcodeRespose) {
        super((short)10);
        this.opcodeRespose = opcodeRespose;
    }


    public short getOpcodeRespose() {
        return opcodeRespose;
    }
}
