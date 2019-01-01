package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Messages.*;
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
            connections.getIdToHandler().get(connectionId).setPassword(register.getPassword());
            connections.getIdToHandler().get(connectionId).setUsername(register.getUsername());
            //Ack
            Ack ack = new Ack((short) 10, (short) 1);
            connections.send(connectionId,ack);
        }
    }
    private void processPM(PM pm)
    {

    }
    private void processFollow(Follow follow)
    {
        System.out.println(follow.getOpcode()+" " + follow.getNumOfUsers());

        for(String use : follow.getUserlist()){
            System.out.println(use);
        }
    }
    private void processPost(Post post)
    {
        System.out.println(post.getPostMessage());
    }
    private void processLogin(Login login){
        //TODO
        System.out.println("We entered the Login method in the protocol");
        //Does the username exist?
        if((connections.getNameToId()).get(login.getUsername()) == null){
            ErrorMsg errorMsg = new ErrorMsg((short)11, (short)2);
            connections.send(connectionId,errorMsg);
            System.out.println(login.getUsername()+login.getUsername());
        }//So the username exist.
        //if the password isn't match with the password that in the connection handler that match to of the id that match to the username from login.
        else {
            //the connection handler that match to of the id that match to the username from login.
            ConnectionHandlerTPC handlerTPC = connections.getIdToHandler().get((connections.getNameToId()).get(login.getUsername()));
            String password = handlerTPC.getPassword();
            boolean isLogin = handlerTPC.isLogin();
            if (!password.equals(login.getPassword()) || isLogin) {
                System.out.println("lll"+login.getPassword() + login.getPassword());
                ErrorMsg errorMsg = new ErrorMsg((short) 11, (short) 2);
                connections.send(connectionId, errorMsg);
            } else {
                //The acts we do when a user logs in
                connections.getIdToHandler().get(connectionId).setLogin(true);
                //Ack
                System.out.println(login.getUsername() + login.getPassword());
                Ack ack = new Ack((short) 10, (short) 2);
                connections.send(connectionId, ack);
            }
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
                processLogin((Login)message);
                break;
            }
            case 4:{
                processFollow((Follow) message);
                break;
            }
            case 5:{
                processPost((Post)message);
            }
            case 6: {
                processPM((PM)message );

            }

        }
    }
    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }


}
