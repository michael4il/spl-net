package bgu.spl.net.api.bidi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections<T> {
/*
    private AtomicInteger counter=new AtomicInteger(0);//be atomic
*/
    private int counter =0;
    private ConcurrentHashMap<Integer, ConnectionHandler> idToHandler =new ConcurrentHashMap<>();


    public ConnectionsImpl() {}

    @Override
    public boolean send(int connectionId, T msg) {
        if(idToHandler.containsKey(connectionId)) {
            idToHandler.get(connectionId).send(msg);
            return true;
        }else return false;
    }

    @Override
    public void broadcast(T msg) {
        idToHandler.forEach((i,handler)->handler.send(msg));
    }

    @Override
    public void disconnect(int connectionId) {
        idToHandler.remove(connectionId);
    }

    //TODO the connectionImpl class will manage the id's
    public int add(ConnectionHandler connectionHandler){
        counter++;
        idToHandler.put(counter,connectionHandler);
        return counter;
    }

}
