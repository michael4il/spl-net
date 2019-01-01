package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Messages.Message;
import bgu.spl.net.api.Messages.Register;
import bgu.spl.net.api.Messages.ServerToClient.Ack.Ack;
import bgu.spl.net.api.Messages.ServerToClient.ErrorMsg;

/*~~~~~~~The protocol of the server~~~~~~~~~~~*/
public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {
    private boolean shouldTerminate;
    private int connectionId;
    private ConnectionsImpl connections;

    private void processRegister(Register register){
        System.out.println("We entered the Register method in the protocol");
        if(connections.getNameToId().get(register.getUsername())!= null){//means the name is already exist
            ErrorMsg errorMsg = new ErrorMsg((short)11, (short)1);
            connections.send(connectionId,errorMsg);
        }else {
            Ack ack = new Ack((short) 10, (short) 1);
            connections.send(connectionId,ack);
        }
    }


    @Override
    public void start(int connectionId, Connections connections) {
        this.connectionId = connectionId;
        this.connections = (ConnectionsImpl)connections;
    }

    @Override
    public void process(Message message) {
        //many private functions. each of them tells what to do
        //for each message.
        switch (message.getOpcode()){
            case 1:{
                processRegister((Register)message);
                break;
            }case 10:{
                System.out.println("WR: process case 10");
                break;
            }
        }
    }
    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }


}
