package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Messages.*;
import bgu.spl.net.api.Messages.ServerToClient.Ack.Ack;
import bgu.spl.net.api.Messages.ServerToClient.ErrorMsg;
import bgu.spl.net.api.Messages.ServerToClient.Notification;

import java.util.concurrent.ConcurrentLinkedQueue;

/*~~~~~~~The protocol of the server~~~~~~~~~~~*/
public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {
    private boolean shouldTerminate;
    //TODO make different connectionId for everyone.
    private int connectionId;
    private Connections connections;
    private DataBase<Message> dataBase;
    private String user;



    public BidiMessagingProtocolImpl(DataBase dataBase){
        this.dataBase = dataBase;
    }

    public void setConnections(Connections connections) {
        this.connections = connections;
    }

    @Override
    public void process(Message message) {
        //many private functions. each of them tells what to do
        //for each message.
        switch (message.getOpcode()){

            case 1:{ processRegister((Register)message);
            break;
            }case 2:{ processLogin((Login)message);
                break;
            }case 3:{ processLogout((Logout)message);
                break;
            }
            case 4:{ processFollow((Follow)message);
                break;
            }case 5:{ processPost((Post)message);
                break;
            }
            case 6:{ processPM((PM)message);
                break;
            }
            case 7:{ processUserlist((Userlist)message);
                break;
            }
            case 8:{ processStat((Stat)message);
               break;
            }
            case 9:{ processNotification((Notification)message);
              break;
            }
        }
    }

    @Override
    public void start(int connectionId, Connections connections) {
        this.connectionId = connectionId;
        this.connections = connections;
    }
    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

//---------------------------------------------------------------REGISTER------------------------------------------------------------------
    private void processRegister(Register register){

        //Should be Sync on username
        System.out.println("We entered the Register method in the protocol");
        if(dataBase.getUsernameToPassword().get(register.getUsername())!= null){//means the name is already exist
            ErrorMsg errorMsg = new ErrorMsg((short)1);
            connections.send(connectionId,errorMsg);
        }else {
            //The acts we do when a user Register
            //Maybe this line is unnecessary
            dataBase.getUsernameToId().put(register.getUsername(),connectionId);
            //Necessary
            dataBase.getUsernameToPassword().put(register.getUsername(),register.getPassword());
            dataBase.onlineUsers().put(register.getUsername(),false);
            dataBase.getUsernameToWaitingT().put(register.getUsername(), new ConcurrentLinkedQueue<>());
            //Ack
            Ack ack = new Ack((short)1);
            connections.send(connectionId, ack);
        }
    }
//-----------------------------------------------------------------LOGIN---------------------------------------------------------------
    private void processLogin(Login login){
        //TODO
        System.out.println("We entered the Login method in the protocol");
        //Does the username exist?
        if((dataBase.getUsernameToPassword()).get(login.getUsername()) == null){
            System.out.println(login.getUsername());
            System.out.println(dataBase.getUsernameToPassword().get(login.getUsername()));
            ErrorMsg errorMsg = new ErrorMsg((short)2);
            connections.send(connectionId,errorMsg);
        }//So the username exist.
        //if the password isn't match with the password that in the connection handler that match to of the id that match to the username from login.
        else {

            String password = dataBase.getUsernameToPassword().get(login.getUsername());
            boolean isLogin = dataBase.onlineUsers().get(login.getUsername());

            if (!password.equals(login.getPassword()) || isLogin) {
                ErrorMsg errorMsg = new ErrorMsg((short) 2);
                connections.send(connectionId, errorMsg);
            } else {
                //TODO add getting the messages before
                //The acts we do when a user logs in
                user = login.getUsername();
                dataBase.getUsernameToWaitingT().get(user).forEach(message-> {
                    connections.send(connectionId, message);
                });
                dataBase.onlineUsers().replace(login.getUsername(),true);
                //Ack
                Ack ack = new Ack((short) 2);
                connections.send(connectionId, ack);
            }
        }
    }
//-------------------------------------------------------------------LOG OUT-------------------------------------------------------------------
private void processLogout(Logout logout){
    System.out.println("We entered the Logout method in the protocol");
    if(dataBase.onlineUsers().get(user))
        {
            dataBase.onlineUsers().replace(user,false);
            shouldTerminate = true;
            Ack ack = new Ack((short) 2);
            connections.send(connectionId, ack);
            user = null;
        }
        else connections.send(connectionId, new ErrorMsg((short) 2));
}
    //---------------------------------------------------------------FOLLOW---------------------------------------------------------------------
    private void processFollow(Follow follow){
        System.out.println("We entered the Follow method in the protocol");
        for (String username: follow.getUserlist()) {
            System.out.println(username);
        }
        System.out.println(follow.isFollow());
/*        //TODO
        System.out.println("We entered the Follow method in the protocol");
        ConnectionHandler handler =  .get(connectionId);
        short succsefullUsers = 0;
        if(follow.isFollow()){//follow
            for (String username: follow.getUserlist()) {
                ConnectionHandler userHandler = (ConnectionHandler) dataBase.getIdToHandler().get(dataBase.getUsernameToId().get(username));
            }

        }//unfollow
        else {

        }*/
    }
    //---------------------------------------------------------------POST---------------------------------------------------------------------
    private void processPost(Post Post){

    }

    //---------------------------------------------------------------PM---------------------------------------------------------------------
    private void processPM(PM pm){

    }

    //---------------------------------------------------------------USER LIST---------------------------------------------------------------------
    private void processUserlist(Userlist userlist){

    }
    //---------------------------------------------------------------STAT---------------------------------------------------------------------
    private void processStat(Stat stat){

    }
    //---------------------------------------------------------------NOTIFICATION---------------------------------------------------------------------
    private void processNotification(Notification notification){

    }



}
