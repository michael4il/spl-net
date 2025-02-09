package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.ConnectionHandler;
import bgu.spl.net.api.bidi.ConnectionsImpl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, java.io.Closeable, ConnectionHandler<T> {

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private int connectionId;
    private ConnectionsImpl<T> connections;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol,ConnectionsImpl connections) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        this.connections = connections;
    }

    @Override
    public void send(T msg) {
        try {
            out.write(encdec.encode(msg));
            out.flush();
        }catch (IOException e){
            System.out.println("Some IO exception occurred");
        }
    }
    @SuppressWarnings("Duplicates")
    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());
            synchronized (connections) {
                protocol.start(connections.add(this), connections);
            }
            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    protocol.process(nextMessage);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

}
