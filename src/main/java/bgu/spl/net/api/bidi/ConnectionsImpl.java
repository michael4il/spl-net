package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Messages.Message;
import bgu.spl.net.api.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {
    private DataBase dataBase;

    @Override
    public boolean send(int connectionId, T msg) {
        ConnectionHandler handler = (ConnectionHandler)(dataBase.getIdToHandler().get(connectionId));
        handler.send(msg);
        return false;
    }

    @Override
    public void broadcast(T msg) {
        dataBase.getIdToHandler().forEach((i,handler)->{
            ConnectionHandler handler1 = (ConnectionHandler)handler;
            handler1.send(msg);
        });
    }

    @Override
    public void disconnect(int connectionId) {

    }

    //TODO I Added.
    //Impl later
    //TODO the connectionImpl class will manage the id's
    public void add(int connectionId, DataBase dataBase){

    }

}
