package bgu.spl.net.api.bidi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataBase<T> {
    private Connections<T> connections;
    private ConcurrentHashMap<Integer, ConnectionHandler> idToHandler;
    private ConcurrentHashMap<String, Integer> nameToId;
    private ConcurrentHashMap<String, String> usernameToPassword;
    private ConcurrentHashMap<String, Boolean> usernameToLogin;

    public DataBase(){
        connections = new ConnectionsImpl();
        idToHandler = new ConcurrentHashMap<>();
        nameToId = new ConcurrentHashMap<>();
        usernameToPassword = new ConcurrentHashMap<>();
        usernameToLogin = new ConcurrentHashMap<>();
    }

    public Connections<T> getConnections() {
        return connections;
    }

    public void add(ConnectionHandler handlerToAdd, int id) {
        idToHandler.put(id, handlerToAdd);
    }

    public ConcurrentHashMap<Integer, ConnectionHandler> getIdToHandler() {
        return idToHandler;
    }

    public ConcurrentHashMap<String, Integer> getNameToId() {
        return nameToId;
    }

    public ConcurrentHashMap<String, String> getUsernameToPassword() {
        return usernameToPassword;
    }

    public ConcurrentHashMap<String, Boolean> getUsernameToLogin() {
        return usernameToLogin;
    }
}
