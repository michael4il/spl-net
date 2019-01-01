package bgu.spl.net.api.bidi;

import bgu.spl.net.api.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {

    ConcurrentHashMap<Integer, ConnectionHandler> idToHandler = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Integer> nameToId = new ConcurrentHashMap<>();

    @Override
    public boolean send(int connectionId, T msg) {
        idToHandler.get(connectionId).send(msg);
        return false;
    }

    @Override
    public void broadcast(T msg) {
        idToHandler.forEach((i,handler)->handler.send(msg));
    }

    @Override
    public void disconnect(int connectionId) {

    }


    @Override
    public void add(ConnectionHandler handlerToAdd, int id) {
        idToHandler.put(id, handlerToAdd);
    }

    public ConcurrentHashMap<Integer, ConnectionHandler> getIdToHandler() {
        return idToHandler;
    }

    public ConcurrentHashMap<String, Integer> getNameToId() {
        return nameToId;
    }
}
