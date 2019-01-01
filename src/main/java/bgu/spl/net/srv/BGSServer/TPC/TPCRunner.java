package bgu.spl.net.srv.BGSServer.TPC;

import bgu.spl.net.api.EncDecServer;
import bgu.spl.net.api.Messages.Message;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.DataBase;
import bgu.spl.net.srv.Server;

import java.io.IOException;

import static bgu.spl.net.srv.Server.threadPerClient;

public class TPCRunner {

    public static void main(String[] args) throws IOException {
        DataBase dataBase = new DataBase();


        Server<Message> threadPerClient =new TPCServer<>(7777,()-> new BidiMessagingProtocolImpl(dataBase),()->new EncDecServer());
                //Server.threadPerClient(7777,()-> new BidiMessagingProtocolImpl(dataBase),()->new EncDecServer());
        threadPerClient.serve();

/*
        TPCServer server = new TPCServer(7777,()-> new BidiMessagingProtocolImpl(),()->new EncDecServer());
        server.serve();*/


    }
}
