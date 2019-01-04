package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Messages.*;
import bgu.spl.net.api.Messages.ServerToClient.Ack.Ack;
import bgu.spl.net.api.Messages.ServerToClient.Ack.AckFollowUserlist;
import bgu.spl.net.api.Messages.ServerToClient.Ack.AckStat;
import bgu.spl.net.api.Messages.ServerToClient.ErrorMsg;
import bgu.spl.net.api.Messages.ServerToClient.Notification;

import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

/*~~~~~~~The protocol of the server~~~~~~~~~~~*/
public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {
    private boolean shouldTerminate;
    private int connectionId;
    private Connections connections;
    private DataBase<Message> DB;
    private String user;


    public BidiMessagingProtocolImpl(DataBase DB) {
        this.DB = DB;
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


    @Override
    public void process(Message message) {
        //many private functions. each of them tells what to do
        //for each message.
        switch (message.getOpcode()) {
            case 1: { processRegister((Register) message);break; }
            case 2: { processLogin((Login) message);break; }
            case 3: { processLogout((Logout) message);break; }
            case 4: { processFollow((Follow) message);break; }
            case 5: { processPost((Post) message);break; }
            case 6: { processPM((PM) message);break; }
            case 7: { processUserlist((Userlist) message);break; }
            case 8: { processStat((Stat) message);break; } }
    }


    //---------------------------------------------------------------REGISTER-----------------------------------------------------------------------
    private void processRegister(Register register) {
        //Should be Sync on username
        System.out.println("We entered the Register method in the protocol");
        if (DB.nameToPassword().get(register.getUsername()) != null) {//means the name is already exist
            ErrorMsg errorMsg = new ErrorMsg((short) 1);
            connections.send(connectionId, errorMsg);
        } else {
            //The acts we do when a user Register
            //Maybe this line is unnecessary
            String newName = register.getUsername();
            //Necessary
            DB.nameToPassword().put(newName, register.getPassword());
            DB.onlineUsers().put(newName, false);
            DB.nameToWaitingT().put(newName, new ConcurrentLinkedQueue<>());
            DB.getPrivateMsgOfUser().put(newName,new ConcurrentLinkedQueue<>());
            DB.whoFollowsMe().put(newName, new ConcurrentLinkedQueue<>());
            DB.whoIfollow().put(newName,new ConcurrentLinkedQueue<>());
            DB.getPostsOfUser().put(newName,new ConcurrentLinkedQueue<>());
            DB.getUsers().add(newName);
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
            if ((DB.nameToPassword()).get(login.getUsername()) == null) {
                System.out.println(login.getUsername());
                System.out.println(DB.nameToPassword().get(login.getUsername()));
                ErrorMsg errorMsg = new ErrorMsg((short) 2);
                connections.send(connectionId, errorMsg);
            }//So the username exist.
            //if the password isn't match with the password that in the connection handler that match to of the id that match to the username from login.
            else {

                String password = DB.nameToPassword().get(login.getUsername());
                boolean isLogin = DB.onlineUsers().get(login.getUsername());

                if (!password.equals(login.getPassword()) || isLogin) {
                    ErrorMsg errorMsg = new ErrorMsg((short) 2);
                    connections.send(connectionId, errorMsg);
                } else {
                    //Good LOGIN~~~~~~~~~~~~~~~~~~~~~~~~~~
                    //TODO add getting the messages before
                    DB.nameToId().put(login.getUsername(),connectionId);
                    user = login.getUsername();
                    Ack ack = new Ack((short) 2);
                    connections.send(connectionId, ack);
                    while (!DB.nameToWaitingT().get(user).isEmpty()){
                        connections.send(connectionId, DB.nameToWaitingT().get(user).poll());
                    }
                    DB.onlineUsers().replace(login.getUsername(), true);
                }
            }
        }
    }

    //-------------------------------------------------------------------LOG OUT-------------------------------------------------------------------
    private void processLogout(Logout logout) {
        System.out.println("We entered the Logout method in the protocol");
        if(user==null){
            connections.send(connectionId,new ErrorMsg((short)3));
        }else {
            String lock = user;
            synchronized (lock) {
                if (DB.onlineUsers().get(user)) {
                    DB.nameToId().remove(user, connectionId);
                    DB.onlineUsers().replace(user, false);
                    Ack ack = new Ack((short) 3);
                    connections.send(connectionId, ack);
                    shouldTerminate = true;
                } else connections.send(connectionId, new ErrorMsg((short) 2));
            }
            user = null;
        }
    }

    //---------------------------------------------------------------FOLLOW---------------------------------------------------------------------
    private void processFollow(Follow follow) {
        System.out.println("We entered the Follow method in the protocol");
        for (String username : follow.getUserlist()) {
        }
        System.out.println(follow.isFollow());
        if(user==null){
            connections.send(connectionId,new ErrorMsg((short)4));
        }else {
            // short successfulUsers = 0;
            Vector<String> successfulUsers = new Vector<>();
            for (String followHim : follow.getUserlist()) {
                if (follow.isFollow()) {
                    //do follow, change twice
                    //check if followHim exist && check if we dont already follow him.
                    if(DB.nameToPassword().get(followHim) != null && !DB.whoIfollow().get(user).contains(followHim)) {
                        DB.whoIfollow().get(user).add(followHim);
                        DB.whoFollowsMe().get(followHim).add(user);
                        successfulUsers.add(followHim);
                    }
                } else {
                    //check if followHim exist && check if we already don't follow him.
                    if (DB.nameToPassword().get(followHim) != null && DB.whoIfollow().get(user).contains(followHim)) {
                        ConcurrentLinkedQueue<String> iFollowThey = DB.whoIfollow().get(user);
                        for(String iFollowHim : iFollowThey){
                            //Comparing between users i follow and users that asked to be unfollow.
                            if(iFollowHim.equals(followHim)){
                                iFollowThey.remove(iFollowHim);
                                DB.whoFollowsMe().get(iFollowHim).remove(user);
                                successfulUsers.add(iFollowHim);
                            }
                        }
                    }
                }
            }
            if (!successfulUsers.isEmpty()) {
                //send ack with op 4
                connections.send(connectionId,new AckFollowUserlist((short)4,(short)successfulUsers.size(),successfulUsers));
            } else {
                //send error
                connections.send(connectionId, new ErrorMsg((short)4));
            }
        }
    }

    //---------------------------------------------------------------POST---------------------------------------------------------------------

    private void sendPost(String userToSendTo, Notification postNotification) {
        synchronized (userToSendTo) {
            if(DB.nameToPassword().get(userToSendTo) != null) {
                if (DB.onlineUsers().get(userToSendTo)) {
                    int idTosend = DB.nameToId().get(userToSendTo);
                    connections.send(idTosend, postNotification);
                } else DB.nameToWaitingT().get(userToSendTo).add(postNotification);
            }
        }
    }

    private void processPost(Post post) {
        System.out.println("We entered the PM method in the protocol");
        if(user!=null) {
            char c = 1;
            Notification postNotification = new Notification(c, user, post.getContent());
            //Save to database the post.
            DB.getTimestampToT().put(0, post);
            DB.getPostsOfUser().get(user).add(post);
            Vector<String> taggedPeople = taggedPeople(post.getContent());
            for (String follower : DB.whoFollowsMe().get(user)) {
                if(taggedPeople.contains(follower))
                    taggedPeople.remove(follower);
                sendPost(follower, postNotification);
            }
            while (!taggedPeople.isEmpty()) {
                String tagged = taggedPeople.remove(0);
                sendPost(tagged, postNotification);
            }
            connections.send(connectionId,new Ack((short)5));
        }else {
            connections.send(connectionId,new ErrorMsg((short)5));
        }
    }

    //---------------------------------------------------------------PM---------------------------------------------------------------------
    private void processPM(PM pm) {
        System.out.println("We entered the PM method in the protocol");
        //TODO check if timestamp needed.
        if(user != null && DB.nameToPassword().containsKey(pm.getUsername())) {
            DB.getTimestampToT().put(0, pm);
            Notification notification = new Notification('\0', user, pm.getContent());
            if (DB.nameToId().get(pm.getUsername()) != null) {//other user exists
                int idTosend = DB.nameToId().get(pm.getUsername());
                //If the user logged in
                synchronized (pm.getUsername()) {
                    if (DB.onlineUsers().get(pm.getUsername())) {
                        connections.send(idTosend, notification);
                    } else {
                        //the other user was not logged in
                        connections.send(connectionId,new ErrorMsg((short)6));
                        //DB.nameToWaitingT().get(pm.getUsername()).add(notification);
                    }
                    //Add the pm to the database
                    DB.getPrivateMsgOfUser().get(user).add(notification);
                    connections.send(connectionId, new Ack((short) 6));
                }
            }
        }//we are not logged in.
        else connections.send(connectionId,new ErrorMsg((short)6));

    }

    //---------------------------------------------------------------USER LIST---------------------------------------------------------------------
    private void processUserlist(Userlist userlist) {
        if(user!=null) {
            if (DB.onlineUsers().get(user)) {
                Vector<String> vectorOfAllUsers = new Vector<>();
                short numOfCurrentUsers = (short) DB.nameToPassword().size();
                for(String user : DB.getUsers())
                {
                    vectorOfAllUsers.add(user);
                }

                AckFollowUserlist ackFollowUserlist = new AckFollowUserlist((short) 7, numOfCurrentUsers, vectorOfAllUsers);
                connections.send(connectionId, ackFollowUserlist);
            }else connections.send(connectionId,new ErrorMsg((short)7));
        }else connections.send(connectionId,new ErrorMsg((short)7));
    }

    //---------------------------------------------------------------STAT---------------------------------------------------------------------
    private void processStat(Stat stat) {
        if(user == null){//the user that send is Logout
            connections.send(connectionId, new ErrorMsg((short)8));
        }else{
            //bring from the database the information.
            ConcurrentLinkedQueue posts = DB.getPostsOfUser().get(stat.getUsername());
            ConcurrentLinkedQueue followers = DB.whoFollowsMe().get(stat.getUsername());
            ConcurrentLinkedQueue following = DB.whoIfollow().get(stat.getUsername());
            short numPosts;
            short numFollowers;
            short numFollowing;
            if(posts.isEmpty()) numPosts = (short)0;
            else numPosts = (short)posts.size();
            if(followers.isEmpty()) numFollowers = (short)0;
            else numFollowers = (short)followers.size();
            if(following.isEmpty()) numFollowing = (short)0;
            else numFollowing = (short)following.size();
            connections.send(connectionId,new AckStat(numPosts,numFollowers,numFollowing));
        }
    }
//------------------------------------------------------------------------------------------------------------------------------------------




    private Vector<String> taggedPeople(String postContent){
        Vector<String> names = new Vector<>();
        Scanner sc = new Scanner(postContent);
        while ( sc.findInLine("@")!=null) {
            names.add(sc.next());
        }
        sc.close();
        return names;
    }
}