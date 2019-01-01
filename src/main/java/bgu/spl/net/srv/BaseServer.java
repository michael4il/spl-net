package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.Messages.Message;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.ConnectionHandler;
import bgu.spl.net.api.bidi.ConnectionHandlerTPC;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;

    public BaseServer(
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {

        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
    }

    @Override
    public void serve() {
        try (ServerSocket serverSock = new ServerSocket(port)) {
			System.out.println("Server started");

            this.sock = serverSock; //just to be able to close

            while (!Thread.currentThread().isInterrupted()) {

                Socket clientSock = serverSock.accept();

                ConnectionHandlerTPC handler = new ConnectionHandlerTPC(
                        protocolFactory.get(),
                        encdecFactory.get(),
                        clientSock);

                execute(handler);
            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }

    protected abstract void execute(ConnectionHandlerTPC<T>  handler);

    /*We Add*/
/*
    protected void executeByInterface(ConnectionHandler<T> handler){
        System.out.println("ExecuteByInterface came from the BaseServer");
    };
*/

    public int getPort() {
        return port;
    }

    public void setSock(ServerSocket sock) {
        this.sock = sock;
    }

    public Supplier<MessageEncoderDecoder<T>> getEncdecFactory() {
        return encdecFactory;
    }

    public Supplier<BidiMessagingProtocol<T>> getProtocolFactory() {
        return protocolFactory;
    }
}
