package bgu.spl.net.api.Messages.ServerToClient.Ack;

import bgu.spl.net.api.Messages.Message;

public class Ack extends Message {
    private short opcodeRespose;
    private String optionalMsg;

    public Ack(short opcode, short opcodeRespose, String optionalMsg) {
        super(opcode);
        this.opcodeRespose = opcodeRespose;
        this.optionalMsg = optionalMsg;
    }

    public String getOptionalMsg() {
        return optionalMsg;
    }

    public short getOpcodeRespose() {
        return opcodeRespose;
    }
}
