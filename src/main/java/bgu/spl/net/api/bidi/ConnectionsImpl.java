package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Messages.Message;
import bgu.spl.net.api.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl implements Connections<Message> {

    private ConcurrentHashMap<Integer, ConnectionHandlerTPC> idToHandler = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> nameToId = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, String > idToMessage = new ConcurrentHashMap<>();

    @Override
    public boolean send(int connectionId, Message msg) {
        (idToHandler.get(connectionId)).send(msg);
        return false;
    }

    @Override
    public void broadcast(Message msg) {
        idToHandler.forEach((i,handler)->handler.send(msg));
    }

    @Override
    public void disconnect(int connectionId) {

    }


    @Override
    public void add(ConnectionHandler handlerToAdd, int id) {
        idToHandler.put(id,(ConnectionHandlerTPC) handlerToAdd);
    }

    public ConcurrentHashMap<Integer, ConnectionHandlerTPC> getIdToHandler() {
        return idToHandler;
    }

    public ConcurrentHashMap<String, Integer> getNameToId() {
        return nameToId;
    }
}
