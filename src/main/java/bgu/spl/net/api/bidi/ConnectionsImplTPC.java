package bgu.spl.net.api.bidi;

public class ConnectionsImplTPC implements Connections {


    @Override
    public boolean send(int connectionId, Object msg) {
        return false;
    }

    @Override
    public void broadcast(Object msg) {

    }

    @Override
    public void disconnect(int connectionId) {

    }

    @Override
    public void add(ConnectionHandler handlerToAdd, int id) {

    }
}
