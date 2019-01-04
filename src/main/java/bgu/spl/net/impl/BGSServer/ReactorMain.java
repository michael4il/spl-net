package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.EncDecServer;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.DataBase;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args){
        DataBase dataBase = new DataBase();
        Server.reactor(Integer.parseInt(args[1]),Integer.parseInt(args[0]),()->new BidiMessagingProtocolImpl(dataBase),()->new EncDecServer()).serve();
    }
}
