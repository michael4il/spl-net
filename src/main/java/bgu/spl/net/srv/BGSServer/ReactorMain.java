package bgu.spl.net.srv.BGSServer;

import bgu.spl.net.api.EncDecServer;
import bgu.spl.net.api.Messages.Message;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.DataBase;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args){
        DataBase dataBase = new DataBase();
        int numOfThread = 10;
        Server.reactor(numOfThread,7777,()->new BidiMessagingProtocolImpl(dataBase),()->new EncDecServer()).serve();
    }
}
