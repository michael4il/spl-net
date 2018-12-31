package bgu.spl.net.api.bidi;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessageMarker;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.BlockingConnectionHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionHandlerTPC extends BlockingConnectionHandler implements ConnectionHandler {

    private String username;
    private String password;
    private int id;
    private boolean login = false;
    private ConcurrentLinkedQueue<MessageMarker> messagesWaitingToConnect;
    private ConcurrentLinkedQueue<String> whoFollowsMe;
    private ConcurrentLinkedQueue<String> whoIFollow;

    public ConnectionHandlerTPC(Socket sock, MessageEncoderDecoder reader, MessagingProtocol protocol, int id) {
        super(sock, reader, protocol);
        this.id = id;
    }


    @Override
    public void send(Object msg) {
        run();
    }

}
