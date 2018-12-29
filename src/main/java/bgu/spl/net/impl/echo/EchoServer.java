package bgu.spl.net.impl.echo;
import bgu.spl.net.srv.*;
import java.io.IOException;

public class EchoServer {

    public static void main(String[] args) throws IOException {
        SingleThreadedServer server = new SingleThreadedServer(7777,()->new EchoProtocol(),()->new LineMessageEncoderDecoder());
        server.serve();



    }


}



