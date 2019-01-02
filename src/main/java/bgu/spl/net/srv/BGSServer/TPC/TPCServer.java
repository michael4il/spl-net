package bgu.spl.net.srv.BGSServer.TPC;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.Messages.Message;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.bidi.*;
import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;


public class TPCServer<T>  implements Server<T>{ //we use modified blocking handler(with connections),
    // should've used baseServer but we override all methods any way and we need the fields
    //connections generic wise?how to add T like protocols?maybe summon it like the factorys
    //break the head later

    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    private Connections<T> connections;


    public TPCServer(
            int port, Supplier<BidiMessagingProtocol<T>> protocolFactory, Supplier<MessageEncoderDecoder<T>> encdecFactory) {
        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
        this.sock = null;
        this.connections=new ConnectionsImpl();
    }
    @SuppressWarnings("Duplicates")
    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {
            System.out.println("Server started");

            this.sock = serverSock; //just to be able to close

            while (!Thread.currentThread().isInterrupted()) {

                Socket clientSock = serverSock.accept();
                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<>(
                        clientSock,
                        encdecFactory.get(),
                        protocolFactory.get(),
                        connections);


                connections.add(handler);
                execute(handler);
            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");

    }

    private void execute(BlockingConnectionHandler<T> handler) {
        new Thread(handler).start();
    }


    @Override
    public void close() throws IOException {

    }
}


///* from the class material*/
//public class TPCServer extends BaseServer {
//    private Connections connections;
//    private int clientId = 0;
//
//
//    public TPCServer(
//            int port,
//            Supplier<BidiMessagingProtocol> protocolSupplier,
//            Supplier<MessageEncoderDecoder<Message>> encoderDecoderSupplier){
//        super(port, protocolSupplier, encoderDecoderSupplier);
//        this.connections = new ConnectionsImpl();
//    }
//
//
//    @Override
//    public void serve() {
//        try (ServerSocket serverSock = new ServerSocket(getPort())) {
//            System.out.println("Server started");
//
//            setSock(serverSock); //just to be able to close
//
//            //clientId represents the primary key for the users - we still don't know their username.
//            while (!Thread.currentThread().isInterrupted()) {
//                Socket clientSock = serverSock.accept();
//                ConnectionHandlerTPC handler = new ConnectionHandlerTPC(
//                        (BidiMessagingProtocol<Message>)getProtocolFactory().get(),
//                        (MessageEncoderDecoder<Message>)getEncdecFactory().get(),
//                        clientSock);
//                execute(handler);
//            }
//        } catch (IOException ex) {
//        }
//
//        System.out.println("server closed!!!");
//    }
//
//    @Override
//    protected void execute(ConnectionHandlerTPC handler){
//        connections.add(handler ,clientId);
//        handler.getProtocol().start(clientId,connections);
//        clientId++;
//        new Thread(handler).start();
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
///*    @Override
//    protected void execute(BlockingConnectionHandler handler) {
//
//    }*/
///*/*With T
//public class TPCServer<T> extends BaseServer<T> {
//    private Connections<T> connections;
//
//    public TPCServer(
//            int port,
//            Supplier<MessagingProtocol<T>> protocolSupplier,
//            Supplier<MessageEncoderDecoder<T>> encoderDecoderSupplier,
//            Connections<T> connections){
//        super(port, protocolSupplier, encoderDecoderSupplier);
//        this.connections = connections;
//    }
//
//    @Override
//    protected void execute(BlockingConnectionHandler<T> blockingConnectionHandler){
//        //connections.add(blockingConnectionHandler);
//        new Thread(blockingConnectionHandler).start();
//    }
//}
// */
