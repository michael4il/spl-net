package bgu.spl.net.api;

import bgu.spl.net.api.Messages.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class EncDecServer implements MessageEncoderDecoder<Message> {
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private short opcode = 0;
    private Message msg;
    int numOfZero = 0;
    private String s1;
    private String s2;
    boolean follow = false;
    short numOfUsers;
    short userWeSaw = 0;
    @Override
    public Message decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison

        switch (opcode){
            case 0: {//The default
                pushByte(nextByte);
                if (nextByte == '\n') {
                    opcode = bytesToShort(Arrays.copyOfRange(bytes,0,1));//Read the first 2 bytes - they are the opcode.
                }
                break;
            }case 1:{
                if(nextByte == '\n' ){
                    if(numOfZero == 0) {
                        s1 = popString();//s1 is the username
                        numOfZero++;
                    }else {
                        if (numOfZero == 1) {
                            s2 = popString();//s2 is the password
                            msg = new Register(opcode,s1,s2);
                            numOfZero = 0;
                            return msg;
                        }
                    }
                }
                pushByte(nextByte);
                break;
            }case 2:{
                if(nextByte == '\n' ){
                    if(numOfZero == 0) {
                        s1 = popString();//s1 is the username
                        numOfZero++;
                    }else {
                        if (numOfZero == 1) {
                            s2 = popString();//s2 is the password
                            msg = new Login(opcode,s1,s2);
                            numOfZero = 0;
                            return msg;
                        }
                    }
                }
                pushByte(nextByte);
                break;
            }case 3:{
                return new Logout(opcode);
                break;
            }case 4: {
                if (nextByte == '\n') {
                    if (numOfZero == 0) {//we want to follow
                        follow = true;
                        numOfZero++;
                    } else {
                        if (numOfZero == 1) {
                            numOfUsers = bytesToShort(Arrays.copyOfRange(bytes, 2, 2));//The third byte.
                            numOfZero++;
                        } else {//numOfZero > 1
                            if (userWeSaw == numOfZero) {
                                //return new Follow(opcode,)
                            }
                        }
                    }
                }
                userWeSaw++;
                pushByte(nextByte);
                break;
            }case 5:{
                break;
            }case 6:{
                break;
            }case 7:{
                break;
            }case 8:{
                break;
            }case 9:{
                break;
            }case 10:{
                break;
            }case 11:{
                break;
            }

        }

        if (nextByte == '\n') {
            return popString();
        }

        pushByte(nextByte);
        return null; //not a line yet
    }

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
    @Override
    public byte[] encode(Message message) {
        return (message + "\n").getBytes(); //uses utf8 by default
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
}
