package bgu.spl.net.api;

import bgu.spl.net.api.Messages.*;
import bgu.spl.net.api.Messages.ServerToClient.Ack.Ack;
import bgu.spl.net.api.Messages.ServerToClient.Ack.AckFollowUserlist;
import bgu.spl.net.api.Messages.ServerToClient.Ack.AckStat;
import bgu.spl.net.api.Messages.ServerToClient.ErrorMsg;
import bgu.spl.net.api.Messages.ServerToClient.Notification;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Vector;

public class EncDecServer implements MessageEncoderDecoder<Message> {
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private short opcode = 0;
    private Message msg;
    private int numOfZero = 0;
    private String s1;
    private String s2;
    private boolean follow = false;
    private short numOfUsers = 0;
    private short userWeSaw = 0;
    private int timesInCase = 0;
    private int readingOpcode = 0;
    private String[] listOfUsers = new String[1 << 10];
    private int usersIndex = 0;

    private void init(){
        len = 0;
        numOfZero = 0;
        follow = false;
        numOfUsers = 0;
        userWeSaw = 0;
        timesInCase = 0;
        usersIndex = 0;
    }

    @Override
    public Message decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison

        if (readingOpcode < 2) {
            pushByte(nextByte);
            if (readingOpcode == 1 ) {
                opcode = bytesToShort(Arrays.copyOfRange(bytes, 0, 2));//Read the first 2 bytes - they are the opcode.
                init();
                if(opcode == 3){
                    readingOpcode = 0;
                    return new Logout();
                }
                if (opcode == 7) {
                    readingOpcode = 0;
                    return new Userlist();
                }
            }
        }
        if(readingOpcode >= 2){
            switch (opcode) {
                case 1: {
                    if (nextByte == '\0') {
                        if (numOfZero == 0) {
                            s1 = popString();//s1 is the username
                            numOfZero++;
                            break;
                        }
                        if (numOfZero == 1) {
                            s2 = popString();//s2 is the password
                            msg = new Register(s1, s2);
                            readingOpcode = 0;
                            return msg;
                        }
                    }else {
                        pushByte(nextByte);
                        break;
                    }
                }case 2: {
                    if (nextByte == '\0') {
                        if (numOfZero == 0) {
                            s1 = popString();//s1 is the username
                            numOfZero++;
                            break;
                        }
                        if (numOfZero == 1) {
                            s2 = popString();//s2 is the password
                            msg = new Login(s1, s2);
                            readingOpcode = 0;
                            return msg;
                        }
                    } else {
                        pushByte(nextByte);
                        break;
                    }
                }
                case 3: {
                    readingOpcode = 0;
                    return new Logout();
                }
                case 4: {
                    if (timesInCase == 0) {
                        if (nextByte == '\0') {
                            follow = true;
                        }
                    }
                    if (timesInCase == 1) { //we read the *first Byte* number of user we want to follow/unfollow
                        pushByte(nextByte);
                    }
                    if (timesInCase == 2) {//we read the number of user we want to follow/unfollow
                        pushByte(nextByte);
                        numOfUsers = bytesToShort(Arrays.copyOfRange(bytes, 0, 2));//The Second and the third byte.
                        //Init the length.
                        len = 0;
                    }
                    if(timesInCase > 2) {
                        if (nextByte == '\0') {
                            listOfUsers[userWeSaw] = popString();
                            userWeSaw++;
                            if (userWeSaw == numOfUsers) {
                                Vector<String> listOfUsersToSend = new Vector<>();
                                //String[] listOfUsersToSend = new String[numOfUsers];
                                for(int i = 0 ; i < numOfUsers; i++){
                                    listOfUsersToSend.add(listOfUsers[i]);
                                }
                                readingOpcode = 0;
                                return new Follow(listOfUsersToSend,numOfUsers,follow);
                            }
                        }else {
                            //In this implementation we do decode the first part and any \n, if we want not to decode it the code should
                            //be inside an "else" statement.
                            pushByte(nextByte);
                        }
                    }
                    timesInCase++;
                    break;
                }
                case 5: {
                    if (nextByte == '\0') {
                        readingOpcode = 0;
                        return new Post(popString());
                    }
                    pushByte(nextByte);
                    break;
                }
                case 6: {
                    if (nextByte == '\0') {
                        if (numOfZero == 0) {
                            s1 = popString();//username for the private message
                            numOfZero++;
                            break;
                        }
                        if (numOfZero == 1) {
                            s2 = popString();//the content of the message
                            readingOpcode = 0;
                            return new PM(s1, s2);
                        }
                    }else {
                        pushByte(nextByte);
                        break;
                    }
                }
                case 7: {
                    len = 0;
                    readingOpcode = 0;
                    return new Userlist();
                }
                case 8: {
                    if (nextByte == '\0') {
                        readingOpcode = 0;
                        return new Stat(popString());
                    }else
                    {
                        pushByte(nextByte);
                    }
                    break;
                }
                /*
                //The Notification -ACK -Error not need to be decoded.
                */
            }
        }
        readingOpcode++;
        //null means that the protocol needs to wait with the response.
        return null;
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Function from SPL Website~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }

    //~~~~~~~~~~Ack~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private byte[] encodeFollowUserlist(AckFollowUserlist ackFollowUserlist){
        byte[] encodedBytes = new byte[1<<10];
        int i = 0;
        byte[] encodedAck = encodeAck(ackFollowUserlist);
        String oneStringOfUsers = new String();
        System.arraycopy(encodedAck,0,encodedBytes,i,encodedAck.length);
        if(encodedAck.length!=4)
            System.out.println("WR: encodedAck.length!=4");
        i += 4;// Starts from 4 because the first 4 bytes used by Ack
        while (!ackFollowUserlist.getUserNameList().isEmpty()){
            oneStringOfUsers = oneStringOfUsers + ackFollowUserlist.getUserNameList().remove(0) +'\0';
        }
        System.arraycopy(shortToBytes(ackFollowUserlist.getNumOfUsers()),0,encodedBytes,i,2);//The next 2 bytes are numOfUsers.
        i += 2;
        System.arraycopy(oneStringOfUsers.getBytes(),0,encodedBytes,i,oneStringOfUsers.getBytes().length);//The rest of the message
        i += oneStringOfUsers.getBytes().length;
        byte[] encodedToSend = new byte[i];
        System.arraycopy(encodedBytes,0,encodedToSend,0,i);
        return encodedToSend;
    }

    private byte[] encodeStat(AckStat ackStat){
        byte[] encodedBytes = new byte[1 << 10];
        int i =0 ;
        System.arraycopy(shortToBytes(ackStat.getOpcode()),0,encodedBytes,i,2);
        i += 2;// Starts from 4 because the first 4 bytes used by Ack
        System.arraycopy(shortToBytes(ackStat.getOpcodeRespose()),0,encodedBytes,i,2);
        i += 2;
        System.arraycopy(shortToBytes(ackStat.getNumOfPosts()),0,encodedBytes,i,2);//The next 2 bytes are numOfUsers.
        i += 2;
        System.arraycopy(shortToBytes(ackStat.getNumOfFollowers()),0,encodedBytes,i,2);
        i += 2;
        System.arraycopy(shortToBytes(ackStat.getNumOfFollowing()),0,encodedBytes,i,2);
        i += 2;
        byte[] encodedToSend = new byte[i];
        System.arraycopy(encodedBytes,0,encodedToSend,0,i);
        return encodedToSend;
    }
    @SuppressWarnings("Duplicates")
    private byte[] encodeAck(Ack ack){
        byte[] encodedBytes = new byte[1 << 10];
        int i = 0;
        System.arraycopy(shortToBytes(ack.getOpcode()),0,encodedBytes,i,2);
        i += 2;
        System.arraycopy(shortToBytes(ack.getOpcodeRespose()),0,encodedBytes,i,2);
        i +=2;
        byte[] encodedToSend = new byte[i];
        System.arraycopy(encodedBytes,0,encodedToSend,0,i);
        return encodedToSend;
    }

    //~~~~~~~~~Notification~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private byte[] encodeNoti(Notification notification){
        byte[] encodedBytes = new byte[1 << 10];
        int i = 0;
        System.arraycopy(shortToBytes(notification.getOpcode()),0,encodedBytes,i,2);//The first 2 bytes of encodedBytes are the encoding of the opcode.
        //PM of Public
        i = i+2;
        encodedBytes[i] = (byte)notification.getPMorPost();
        i++;
        //PostingUser
        byte[] postingUserBytes = notification.getSendingUser().getBytes();
        System.arraycopy(postingUserBytes,0,encodedBytes,i,postingUserBytes.length);
        i += postingUserBytes.length;
        char c = '\0';
        encodedBytes[i] = (byte)c;
        i++;
        //Content
        byte[] contentInByets = notification.getContent().getBytes();
        System.arraycopy(contentInByets,0,encodedBytes,i,contentInByets.length);
        i += contentInByets.length;
        encodedBytes[i] = (byte)c;
        i++;
        byte[] encodedToSend = new byte[i];
        System.arraycopy(encodedBytes,0,encodedToSend,0,i);
        return encodedToSend;
    }

    @SuppressWarnings("Duplicates")
    //~~~~~~~~~~~~~~~~Error~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private byte[] encodeError(ErrorMsg errorMsg){
        byte[] encodedBytes = new byte[1 << 10];
        int i =0;
        System.arraycopy(shortToBytes(errorMsg.getOpcode()),0,encodedBytes,i,2);
        i += 2;
        System.arraycopy(shortToBytes(errorMsg.getOpcodeRespose()),0,encodedBytes,i,2);
        i +=2;
        byte[] encodedToSend = new byte[i];
        System.arraycopy(encodedBytes,0,encodedToSend,0,i);
        return encodedToSend;

    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Encode~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    //Server to Client Messages:
    //Notification -ACK -Error only
    public byte[] encode(Message message) {
        if(message instanceof Ack){
            if(message instanceof AckFollowUserlist){
                return encodeFollowUserlist((AckFollowUserlist)message);
            }
            if(message instanceof AckStat){
                return encodeStat((AckStat)message);
            }
            //None of them. regular ack
            return encodeAck((Ack)message);
        }
        if(message instanceof Notification){
            return encodeNoti((Notification) message);
        }
        if(message instanceof ErrorMsg){
            return encodeError((ErrorMsg)message);
        }
        System.out.println("WR: end of encode message in encdecServer");
        return (message + "\n").getBytes(); //uses utf8 by default
    }

}