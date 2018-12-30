package bgu.spl.net.srv.BGSServer.TPC;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.bidi.ConnectionHandler;
import bgu.spl.net.api.bidi.ConnectionHandlerTPC;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.BlockingConnectionHandler;
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
            Supplier<MessagingProtocol> protocolSupplier,
            Supplier<MessageEncoderDecoder> encoderDecoderSupplier){
        super(port, protocolSupplier, encoderDecoderSupplier);
        this.connections = new ConnectionsImpl();
    }

    @Override
    public void serve() {
        try (ServerSocket serverSock = new ServerSocket(getPort())) {
            System.out.println("Server started");

            setSock(serverSock); //just to be able to close

            int i = 0;
            while (!Thread.currentThread().isInterrupted()) {

                Socket clientSock = serverSock.accept();


                ConnectionHandlerTPC handler = new ConnectionHandlerTPC(
                        "LALA" + i,
                        "lala" +i,
                        clientSock,
                        (MessageEncoderDecoder) getEncdecFactory().get(),
                        (MessagingProtocol)getProtocolFactory().get());

                executeByInterface(handler);
            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");
    }

    @Override
    protected void execute(BlockingConnectionHandler handler) {

    }

    @Override
    protected void executeByInterface(ConnectionHandler handler){
        connections.add( handler ,clientId);
        clientId++;
        new Thread().start();
    }
}
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
