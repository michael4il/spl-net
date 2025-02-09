package bgu.spl.net.api.bidi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class DataBase<T> {

    private ConcurrentHashMap<String, Integer> usernameToId;
    private ConcurrentHashMap<String, String> usernameToPassword;
    private ConcurrentHashMap<String, Object> usernameToLock;
    private ConcurrentHashMap<String, Boolean> usernameToLogin;
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<T>> usernameToWaitingT;
    private AtomicInteger timeStamp;
    private ConcurrentHashMap<Integer, T> timestampToT;
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> whoIfollow;
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> whoFollowsMe;
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<T>> postsOfUser;
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<T>> privateMsgOfUser;
    private ConcurrentLinkedQueue<String> users;

    public DataBase(){
        usernameToId = new ConcurrentHashMap<>();
        usernameToPassword = new ConcurrentHashMap<>();
        usernameToLogin = new ConcurrentHashMap<>();
        usernameToWaitingT = new ConcurrentHashMap<>();
        timestampToT = new ConcurrentHashMap<>();
        timeStamp = new AtomicInteger();
        whoIfollow = new ConcurrentHashMap<>();
        whoFollowsMe = new ConcurrentHashMap<>();
        postsOfUser = new ConcurrentHashMap<>();
        privateMsgOfUser = new ConcurrentHashMap<>();
        users = new ConcurrentLinkedQueue<>();
        usernameToLock = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<Integer, T> getTimestampToT() {
        return timestampToT;
    }

    public ConcurrentHashMap<String, Integer> nameToId() {
        return usernameToId;
    }

    public ConcurrentHashMap<String, String> nameToPassword() {
        return usernameToPassword;
    }

    public ConcurrentHashMap<String, Boolean> onlineUsers() {
        return usernameToLogin;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<T>> nameToWaitingT() {
        return usernameToWaitingT;
    }

    public AtomicInteger getTimeStamp() {
        return timeStamp;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> whoIfollow() {
        return whoIfollow;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> whoFollowsMe() {
        return whoFollowsMe;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<T>> getPostsOfUser() {
        return postsOfUser;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<T>> getPrivateMsgOfUser() {
        return privateMsgOfUser;
    }

    public ConcurrentLinkedQueue<String> getUsers() {
        return users;
    }

    public ConcurrentHashMap<String, Object> getUsernameToLock() {
        return usernameToLock;
    }
}
