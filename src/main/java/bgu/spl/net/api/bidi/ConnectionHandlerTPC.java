package bgu.spl.net.api.bidi;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessageMarker;
import bgu.spl.net.api.Messages.Message;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.BlockingConnectionHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionHandlerTPC<Message> implements ConnectionHandler<Message>, Runnable{

    private final BidiMessagingProtocol<Message> protocol;
    private final MessageEncoderDecoder<Message> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;

    //Belong to the implementation
    private int connectionId;
    private String username;
    private String password;
    private boolean login = false;
    private ConcurrentLinkedQueue<MessageMarker> messagesWaitingToConnect;
    private ConcurrentLinkedQueue<String> whoFollowsMe;
    private ConcurrentLinkedQueue<String> whoIFollow;

    public ConnectionHandlerTPC(BidiMessagingProtocol<Message> protocol, MessageEncoderDecoder<Message> encdec, Socket sock) {
        this.protocol = protocol;
        this.encdec = encdec;
        this.sock = sock;
    }

    @Override
    public void send(Message msg) {
        try {
            out.write(encdec.encode(msg));
            out.flush();
        }catch (IOException e){
            System.out.println("Some exception occurred");
        }

    }

    @SuppressWarnings("Duplicates")
    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                Message nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    //This section should change.
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

    public BidiMessagingProtocol<Message> getProtocol() {
        return protocol;
    }
}
