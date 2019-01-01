package bgu.spl.net.srv.BGSServer.TPC;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.Messages.Message;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.bidi.*;
import bgu.spl.net.srv.BaseServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

/* from the class material*/
public class TPCServer extends BaseServer {
    private Connections connections;
    private int clientId = 0;


    public TPCServer(
            int port,
            Supplier<BidiMessagingProtocol> protocolSupplier,
            Supplier<MessageEncoderDecoder<Message>> encoderDecoderSupplier){
        super(port, protocolSupplier, encoderDecoderSupplier);
        this.connections = new ConnectionsImpl();
    }


    @Override
    public void serve() {
        try (ServerSocket serverSock = new ServerSocket(getPort())) {
            System.out.println("Server started");

            setSock(serverSock); //just to be able to close

            //clientId represents the primary key for the users - we still don't know their username.
            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSock = serverSock.accept();
                ConnectionHandlerTPC handler = new ConnectionHandlerTPC(
                        (BidiMessagingProtocol<Message>)getProtocolFactory().get(),
                        (MessageEncoderDecoder<Message>)getEncdecFactory().get(),
                        clientSock);
                execute(handler);
            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");
    }


    @Override
    protected void execute(ConnectionHandlerTPC handler){
        connections.add(handler ,clientId);
        handler.getProtocol().start(clientId,connections);
        clientId++;
        new Thread(handler).start();
    }
}
















/*    @Override
    protected void execute(BlockingConnectionHandler handler) {

    }*/
/*/*With T
public class TPCServer<T> extends BaseServer<T> {
    private Connections<T> connections;

    public TPCServer(
            int port,
            Supplier<MessagingProtocol<T>> protocolSupplier,
            Supplier<MessageEncoderDecoder<T>> encoderDecoderSupplier,
            Connections<T> connections){
        super(port, protocolSupplier, encoderDecoderSupplier);
        this.connections = connections;
    }

    @Override
    protected void execute(BlockingConnectionHandler<T> blockingConnectionHandler){
        //connections.add(blockingConnectionHandler);
        new Thread(blockingConnectionHandler).start();
    }
}
 */
