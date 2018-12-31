package bgu.spl.net.api.Messages.ServerToClient.Ack;

import bgu.spl.net.api.Messages.Message;

public class Ack extends Message {
    private short opcodeRespose;

    public Ack(short opcode, short opcodeRespose, String optionalMsg) {
        super(opcode);
        this.opcodeRespose = opcodeRespose;
    }


    public short getOpcodeRespose() {
        return opcodeRespose;
    }
}
