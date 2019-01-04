package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.EncDecServer;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.DataBase;
import bgu.spl.net.srv.Server;

import java.io.IOException;

public class TPCMain {

    public static void main(String[] args) throws IOException {
        DataBase dataBase = new DataBase();
        Server threadPerClient = Server.threadPerClient(Integer.parseInt(args[0]),()-> new BidiMessagingProtocolImpl(dataBase),()->new EncDecServer());
        threadPerClient.serve();
    }
}



