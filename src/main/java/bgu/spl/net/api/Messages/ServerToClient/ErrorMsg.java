package bgu.spl.net.api.Messages.ServerToClient;

import bgu.spl.net.api.Messages.Message;

public class ErrorMsg extends Message {
    private short opcodeRespose;

    public ErrorMsg( short opcodeRespose) {
        super((short)11);
        this.opcodeRespose = opcodeRespose;
    }

    public short getOpcodeRespose() {
        return opcodeRespose;
    }
}
