package bgu.spl.net.srv.BGSServer;

import bgu.spl.net.api.EncDecServer;
import bgu.spl.net.api.Messages.Message;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.DataBase;
import bgu.spl.net.srv.Server;

import java.io.IOException;
import java.util.Scanner;

import static bgu.spl.net.srv.Server.threadPerClient;

public class TPCMain {

    public static void main(String[] args) throws IOException {
        DataBase dataBase = new DataBase();
//        String s = "@abc aaaa @df asd @@@@ ";
//        Scanner sc = new Scanner(s);
//        while ( sc.findInLine("@")!=null) {
//            System.out.println(sc.next());
//        }
//        sc.close();


        //server is T=message ,bidi protocol and enc dec is already message
        Server threadPerClient = Server.threadPerClient(7777,()-> new BidiMessagingProtocolImpl(dataBase),()->new EncDecServer());
        threadPerClient.serve();

    }
}



