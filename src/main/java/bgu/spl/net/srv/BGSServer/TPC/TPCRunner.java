package bgu.spl.net.srv.BGSServer.TPC;

import bgu.spl.net.api.EncDecServer;
import bgu.spl.net.api.Messages.Message;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;

import java.io.IOException;

public class TPCRunner {
    public static void main(String[] args) throws IOException {
        TPCServer server = new TPCServer(7777,()-> new BidiMessagingProtocolImpl(),()->new EncDecServer());
        server.serve();


    }
}
