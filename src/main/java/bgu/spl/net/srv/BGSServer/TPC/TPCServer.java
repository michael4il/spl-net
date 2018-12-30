package bgu.spl.net.srv.BGSServer.TPC;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
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

    public TPCServer(
            int port,
            Supplier<MessagingProtocol> protocolSupplier,
            Supplier<MessageEncoderDecoder> encoderDecoderSupplier){
        super(port, protocolSupplier, encoderDecoderSupplier);
        this.connections = new ConnectionsImpl();
    }

    @Override
    protected void execute(BlockingConnectionHandler blockingConnectionHandler){
        //connections.add(blockingConnectionHandler);
        new Thread(blockingConnectionHandler).start();
    }
}
/*
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
