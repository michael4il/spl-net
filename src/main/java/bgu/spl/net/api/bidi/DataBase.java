package bgu.spl.net.api.bidi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataBase<T> {

    private ConcurrentHashMap<String, Integer> usernameToId;
    private ConcurrentHashMap<String, String> usernameToPassword;
    private ConcurrentHashMap<String, Boolean> usernameToLogin;

    private ConcurrentHashMap<String, ConcurrentLinkedQueue<T>> usernameToWaitingT;

    public DataBase(){
        usernameToId = new ConcurrentHashMap<>();
        usernameToPassword = new ConcurrentHashMap<>();
        usernameToLogin = new ConcurrentHashMap<>();
        usernameToWaitingT = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, Integer> getUsernameToId() {
        return usernameToId;
    }

    public ConcurrentHashMap<String, String> getUsernameToPassword() {
        return usernameToPassword;
    }

    public ConcurrentHashMap<String, Boolean> onlineUsers() {
        return usernameToLogin;
    }

    public ConcurrentHashMap<String, ConcurrentLinkedQueue<T>> getUsernameToWaitingT() {
        return usernameToWaitingT;
    }
}
