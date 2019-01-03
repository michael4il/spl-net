package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Messages.*;
import bgu.spl.net.api.Messages.ServerToClient.Ack.Ack;
import bgu.spl.net.api.Messages.ServerToClient.Ack.AckFollowUserlist;
import bgu.spl.net.api.Messages.ServerToClient.Ack.AckStat;
import bgu.spl.net.api.Messages.ServerToClient.ErrorMsg;
import bgu.spl.net.api.Messages.ServerToClient.Notification;

import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

/*~~~~~~~The protocol of the server~~~~~~~~~~~*/
public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {
    private boolean shouldTerminate;
    //TODO make different connectionId for everyone.
    private int connectionId;
    private Connections connections;
    private DataBase<Message> dataBase;
    private String user;


    public BidiMessagingProtocolImpl(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    public void setConnections(Connections connections) {
        this.connections = connections;
    }

    @Override
    public void start(int connectionId, Connections connections) {
        this.connectionId = connectionId;
        this.connections = connections;
    }

    private Vector<String> taggedPeople(String postContent){
        Vector<String> names = new Vector<>();
        return names;
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    //---------------------------------------------------------------REGISTER------------------------------------------------------------------
    private void processRegister(Register register) {

        //Should be Sync on username
        System.out.println("We entered the Register method in the protocol");
        if (dataBase.getUsernameToPassword().get(register.getUsername()) != null) {//means the name is already exist
            ErrorMsg errorMsg = new ErrorMsg((short) 1);
            connections.send(connectionId, errorMsg);
        } else {
            //The acts we do when a user Register
            //Maybe this line is unnecessary
            dataBase.getUsernameToId().put(register.getUsername(), connectionId);
            //Necessary
            dataBase.getUsernameToPassword().put(register.getUsername(), register.getPassword());
            dataBase.onlineUsers().put(register.getUsername(), false);
            dataBase.getUsernameToWaitingT().put(register.getUsername(), new ConcurrentLinkedQueue<>());
            //Ack
            Ack ack = new Ack((short) 1);
            connections.send(connectionId, ack);
        }
    }

    //-----------------------------------------------------------------LOGIN---------------------------------------------------------------
    private void processLogin(Login login) {
        //TODO
        System.out.println("We entered the Login method in the protocol");
        //Does the username exist?
        synchronized (login.getUsername()) {
            if ((dataBase.getUsernameToPassword()).get(login.getUsername()) == null) {
                System.out.println(login.getUsername());
                System.out.println(dataBase.getUsernameToPassword().get(login.getUsername()));
                ErrorMsg errorMsg = new ErrorMsg((short) 2);
                connections.send(connectionId, errorMsg);
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
                    while (!dataBase.getUsernameToWaitingT().get(user).isEmpty()){
                        connections.send(connectionId,dataBase.getUsernameToWaitingT().get(user).poll());
                    }
/*                    dataBase.getUsernameToWaitingT().get(user).forEach(message -> {
                        connections.send(connectionId, message);
                    });*/
                    dataBase.onlineUsers().replace(login.getUsername(), true);
                    //Ack
                    Ack ack = new Ack((short) 2);
                    connections.send(connectionId, ack);
                }
            }
        }
    }

    //-------------------------------------------------------------------LOG OUT-------------------------------------------------------------------
    private void processLogout(Logout logout) {
        System.out.println("We entered the Logout method in the protocol");
        if(user==null){
            System.out.println("WR: in processLogout user is null");
        }
        String lock = user;
        synchronized (lock){
            if (dataBase.onlineUsers().get(user)) {
                dataBase.onlineUsers().replace(user, false);
                shouldTerminate = true;
                Ack ack = new Ack((short) 2);
                connections.send(connectionId, ack);
                user = null;
            } else connections.send(connectionId, new ErrorMsg((short) 2));
        }
    }

    //---------------------------------------------------------------FOLLOW---------------------------------------------------------------------
    private void processFollow(Follow follow) {
        System.out.println("We entered the Follow method in the protocol");
        for (String username : follow.getUserlist()) {
            System.out.println(username);
        }
        System.out.println(follow.isFollow());
        if(user==null){
            connections.send(connectionId,new ErrorMsg((short)4));
        }else {
            short successfulUsers = 0;
            for (String followHim : follow.getUserlist()) {
                if (follow.isFollow()) {
                    //do follow, change twice
                    //check if followHim exist.
                    if(dataBase.getUsernameToPassword().get(followHim) != null) {
                        dataBase.getWhoIfollow().get(user).add(followHim);
                        dataBase.getWhoFollowsMe().get(followHim).add(user);
                        successfulUsers++;
                    }
                } else {
                    if (dataBase.getUsernameToPassword().get(followHim) != null) {
                        ConcurrentLinkedQueue<String> iFollowThey = dataBase.getWhoIfollow().get(user);
                        for(String iFollowHim : iFollowThey){
                            if(iFollowHim.equals(followHim)){
                                iFollowThey.remove(iFollowHim);
                                dataBase.getWhoFollowsMe().get(iFollowHim).remove(user);
                                successfulUsers++;
                            }
                        }
                    }
                }
            }

            if (successfulUsers > 0) {
                //send ack
            } else {
                //send error
            }
        }
    }


    private void sendPost(String peopleToSendTo, Notification postNotification){
        int idTosend = dataBase.getUsernameToId().get(peopleToSendTo);
        synchronized (peopleToSendTo){
            if(dataBase.onlineUsers().get(peopleToSendTo))
                connections.send(idTosend, postNotification);
            else dataBase.getUsernameToWaitingT().get(peopleToSendTo).add(postNotification);
        }
    }

    //---------------------------------------------------------------POST---------------------------------------------------------------------
    private void processPost(Post post) {
        System.out.println("We entered the PM method in the protocol");
        char c = 1;
        Notification postNotification = new Notification(c,user,post.getContent());
        //TODO check if timestamp needed.
        dataBase.getTimestampToT().put(0,post);
        Vector<String> taggedPeople = taggedPeople(post.getContent());
        for (String follower: dataBase.getWhoFollowsMe().get(user)) {
            sendPost(follower,postNotification);
        }
        while (!taggedPeople.isEmpty()){
            String tagged = taggedPeople.remove(0);
            sendPost(tagged,postNotification);
        }
    }

    //---------------------------------------------------------------PM---------------------------------------------------------------------
    private void processPM(PM pm) {
        System.out.println("We entered the PM method in the protocol");
        //TODO check if timestamp needed.
        dataBase.getTimestampToT().put(0,pm);
        Notification notification = new Notification('\n' ,user,pm.getContent());
        int idTosend = dataBase.getUsernameToId().get(pm.getUsername());
        //If the user logged in
        synchronized (pm.getUsername()) {
            if (dataBase.onlineUsers().get(pm.getUsername())) {
                connections.send(idTosend, notification);
                connections.send(connectionId,new Ack((short)6));
            } else {
                dataBase.getUsernameToWaitingT().get(pm.getUsername()).add(notification);
                connections.send(connectionId,new ErrorMsg((short)6));
            }
        }
    }

    //---------------------------------------------------------------USER LIST---------------------------------------------------------------------
    private void processUserlist(Userlist userlist) {
        if(dataBase.onlineUsers().get(user)){
            Vector<String> vectorOfAllUsers = new Vector<>();
            short numOfCurrentUsers = (short)dataBase.getUsernameToPassword().size();
            dataBase.getUsernameToPassword().forEach((name,pass)->{
                vectorOfAllUsers.add(name);
            });
            AckFollowUserlist ackFollowUserlist = new AckFollowUserlist((short)7,numOfCurrentUsers,vectorOfAllUsers);
            connections.send(connectionId,ackFollowUserlist);
        }else connections.send(connectionId,new ErrorMsg((short)7));
    }

    //---------------------------------------------------------------STAT---------------------------------------------------------------------
    private void processStat(Stat stat) {
        int idToSend = dataBase.getUsernameToId().get(stat.getUsername());
        if(user == null){//the user that send is Logout
            connections.send(connectionId, new ErrorMsg((short)8));
        }else{
            //bring from the database the information.
            short numPosts = (short)dataBase.getPostsOfUser().size();
            short numFollowers = (short)dataBase.getWhoFollowsMe().size();
            short numFollowing = (short)dataBase.getWhoIfollow().size();
            connections.send(connectionId,new AckStat(numPosts,numFollowers,numFollowing));
        }
    }
    //------------------------------------------------------------------------------------------------------------------------------------------


    @Override
    public void process(Message message) {
        //many private functions. each of them tells what to do
        //for each message.
        switch (message.getOpcode()) {
            case 1: {
                processRegister((Register) message);
                break;
            }
            case 2: {
                processLogin((Login) message);
                break;
            }
            case 3: {
                processLogout((Logout) message);
                break;
            }
            case 4: {
                processFollow((Follow) message);
                break;
            }
            case 5: {
                processPost((Post) message);
                break;
            }
            case 6: {
                processPM((PM) message);
                break;
            }
            case 7: {
                processUserlist((Userlist) message);
                break;
            }
            case 8: {
                processStat((Stat) message);
                break;
            }
        }
    }
}