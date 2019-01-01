package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Messages.Follow;
import bgu.spl.net.api.Messages.Login;
import bgu.spl.net.api.Messages.Message;
import bgu.spl.net.api.Messages.Register;
import bgu.spl.net.api.Messages.ServerToClient.Ack.Ack;
import bgu.spl.net.api.Messages.ServerToClient.ErrorMsg;

/*~~~~~~~The protocol of the server~~~~~~~~~~~*/
public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {
    private boolean shouldTerminate;
    //TODO make different connectionId for everyone.
    private int connectionId;
    private Connections connections;
    private DataBase<Message> dataBase;

    public BidiMessagingProtocolImpl(DataBase dataBase){
        this.dataBase = dataBase;
        connections = dataBase.getConnections();
    }

    private void processRegister(Register register){
        //TODO
        //Should be Sync
        System.out.println("We entered the Register method in the protocol");
        if(dataBase.getUsernameToPassword().get(register.getUsername())!= null){//means the name is already exist
            ErrorMsg errorMsg = new ErrorMsg((short)11, (short)1);
            connections.send(connectionId,errorMsg);
        }else {
            //The acts we do when a user Register
            //Maybe this line is unnecessary
            dataBase.getNameToId().put(register.getUsername(),connectionId);
            //Necessary
            dataBase.getUsernameToPassword().put(register.getUsername(),register.getPassword());
            dataBase.getUsernameToLogin().put(register.getUsername(),false);
            //Ack
            Ack ack = new Ack((short) 10, (short) 1);
            connections.send(connectionId,ack);
        }
    }

    private void processLogin(Login login){
        //TODO
        System.out.println("We entered the Login method in the protocol");
        //Does the username exist?
        if((dataBase.getUsernameToPassword()).get(login.getUsername()) == null){
            ErrorMsg errorMsg = new ErrorMsg((short)11, (short)2);
            connections.send(connectionId,errorMsg);
        }//So the username exist.
        //if the password isn't match with the password that in the connection handler that match to of the id that match to the username from login.
        else {

            String password = dataBase.getUsernameToPassword().get(login.getUsername());
            boolean isLogin = dataBase.getUsernameToLogin().get(login.getUsername());

            if (!password.equals(login.getPassword()) || isLogin) {
                ErrorMsg errorMsg = new ErrorMsg((short) 11, (short) 2);
                connections.send(connectionId, errorMsg);
            } else {
                //The acts we do when a user logs in
                dataBase.getUsernameToLogin().replace(login.getUsername(),true);
                //Ack
                Ack ack = new Ack((short) 10, (short) 2);
                connections.send(connectionId, ack);
            }
        }
    }

    private void processFollow(Follow follow){
        //TODO
        System.out.println("We entered the Follow method in the protocol");
        ConnectionHandler handler = (ConnectionHandler) dataBase.getIdToHandler().get(connectionId);
        short succsefullUsers = 0;
        if(follow.isFollow()){//follow
            for (String username: follow.getUserlist()) {
                ConnectionHandler userHandler = (ConnectionHandler) dataBase.getIdToHandler().get(dataBase.getNameToId().get(username));
            }

        }//unfollow
        else {

        }
    }


    @Override
    public void start(int connectionId, Connections connections) {
        this.connectionId = connectionId;
        this.connections = connections;
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
                processLogin((Login)message);
                break;
            }case 3:{

                break;
            }
            case 4:{

                break;
            }
        }
    }
    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    @Override
    public Connections getConnections() {
        return connections;
    }
}
