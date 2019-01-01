package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Messages.Login;
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
        //TODO
        System.out.println("We entered the Register method in the protocol");
        if(connections.getNameToId().get(register.getUsername())!= null){//means the name is already exist
            ErrorMsg errorMsg = new ErrorMsg((short)11, (short)1);
            connections.send(connectionId,errorMsg);
        }else {
            //The acts we do when a user Register
            connections.getNameToId().put(register.getUsername(),connectionId);
            //Ack
            Ack ack = new Ack((short) 10, (short) 1);
            connections.send(connectionId,ack);
        }
    }
    private void processLogin(Login login){
        //TODO
        System.out.println("We entered the Login method in the protocol");
        if((connections.getNameToId()).get(login.getUsername()) != null){
            ErrorMsg errorMsg = new ErrorMsg((short)11, (short)2);
            connections.send(connectionId,errorMsg);
        }//So the username exist.
        //if the password isn't match with the password that in the connection handler that match to of the id that match to the username.
        else if(!((connections.getIdToHandler().get((connections.getNameToId()).get(login.getUsername()))).getPassword().equals(login.getPassword())) || !connections.getIdToHandler().get((connections.getNameToId()).get(login.getUsername())).isLogin()) {
            ErrorMsg errorMsg = new ErrorMsg((short)11, (short)2);
            connections.send(connectionId,errorMsg);
        }else {
            //The acts we do when a user logs in
            connections.getIdToHandler().get(connectionId).setLogin(true);
            //Ack
            Ack ack = new Ack((short) 10, (short) 2);
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
            }case 2:{
                ;
                break;
            }
        }
    }
    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }


}
