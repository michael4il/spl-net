package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandlerTPC<T> implements Runnable, java.io.Closeable {

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private int connectionId;

    public BlockingConnectionHandlerTPC(BidiMessagingProtocol<T> protocol, MessageEncoderDecoder<T> encdec, Socket sock, int connectionId) {
        this.protocol = protocol;
        this.encdec = encdec;
        this.sock = sock;
        this.connectionId = connectionId;
    }


    @SuppressWarnings("Duplicates")
    @Override
    public void run() {
/*        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    //This section should change.
                    protocol.start();
                    protocol.process(nextMessage);
                    if (response != null) {
                        out.write(encdec.encode(response));
                        out.flush();
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }*/

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }
}
