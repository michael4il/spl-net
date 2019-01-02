package bgu.spl.net.api.bidi;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {
    private int counter=0;//need to be atomic
    private ConcurrentHashMap<Integer, ConnectionHandler> idToHandler =new ConcurrentHashMap<>();


    public ConnectionsImpl() {}

    @Override
    public  boolean send(int connectionId, T msg) {
        idToHandler.get(connectionId).send(msg);
        return true;
    }

    @Override
    public void broadcast(T msg) {
        idToHandler.forEach((i,handler)->handler.send(msg));
    }

    @Override
    //Todo - check if we need to do logout in the Database.
    //Probably not. already done.
    //Sync
    public void disconnect(int connectionId) {
    }

    //TODO the connectionImpl class will manage the id's
    public int add(ConnectionHandler connectionHandler){
        counter++;
        idToHandler.put(counter,connectionHandler);
        return counter;
    }

}
