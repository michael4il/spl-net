package bgu.spl.net.api;

import bgu.spl.net.api.Messages.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class EncDecServer implements MessageEncoderDecoder<Message> {
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private short opcode = 0;
    private Message msg;
    private int numOfZero = 0;
    private String s1;
    private String s2;
    private boolean follow = false;
    private boolean pmTruePostFalse = false;
    private short numOfUsers;
    private short userWeSaw = 0;
    private int timesInCase = 0;
    private boolean readingOpcode = true;
    @Override
    public Message decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison

        if(readingOpcode){
            pushByte(nextByte);
            if (nextByte == '\n') {
                opcode = bytesToShort(Arrays.copyOfRange(bytes, 0, 1));//Read the first 2 bytes - they are the opcode.
                numOfZero = 0;
            }
        }else {
            switch (opcode) {
                case 1: {
                    if (nextByte == '\n') {
                        if (numOfZero == 0) {
                            s1 = popString();//s1 is the username
                            numOfZero++;
                            break;
                        }
                        if (numOfZero == 1) {
                            s2 = popString();//s2 is the password
                            msg = new Register(opcode, s1, s2);
                            numOfZero = 0;
                            readingOpcode = true;
                            return msg;
                        }
                    }
                }
                pushByte(nextByte);
                break;
                case 2: {
                    if (nextByte == '\n') {
                        if (numOfZero == 0) {
                            s1 = popString();//s1 is the username
                            numOfZero++;
                        }
                        if (numOfZero == 1) {
                            s2 = popString();//s2 is the password
                            msg = new Login(opcode, s1, s2);
                            numOfZero = 0;
                            readingOpcode = true;
                            return msg;
                        }
                    } else {
                        pushByte(nextByte);
                    }
                    break;
                }
                case 3: {
                    readingOpcode = true;
                    return new Logout(opcode);
                    break;
                }
                case 4: {
                    if (timesInCase == 0) {
                        if (nextByte == '\n') {
                            follow = true;
                        }
                        timesInCase++;
                    }
                    if (timesInCase == 1) {//we read the number of user we want to follow/unfollow
                        numOfUsers = bytesToShort(Arrays.copyOfRange(bytes, 2, 2));//The third byte.
                        timesInCase++;
                    }
                    if (nextByte == '\n') {
                        userWeSaw++;
                        if (userWeSaw == numOfUsers) {
                            readingOpcode = true;
                            timesInCase = 0;
                            return new Follow(opcode, new String(bytes, 3, len, StandardCharsets.UTF_8), numOfUsers, follow);
                        }
                    } else {
                        //In this implementation we do not decode the first part and any \n, if we want to decode it the code should
                        //be outside the "else" statement.
                        pushByte(nextByte);
                    }
                    break;
                }
                case 5: {
                    if (nextByte == '\n') {
                        readingOpcode = true;
                        return new Post(opcode, popString());
                    }
                    pushByte(nextByte);
                    break;
                }
                case 6: {
                    if (nextByte == '\n') {
                        if (numOfZero == 0) {
                            s1 = popString();//username for the private message
                            break;
                        }
                        if (numOfZero == 1) {
                            s2 = popString();//the content of the message
                            readingOpcode = true;
                            return new PM(opcode, s1, s2);
                        }
                    }
                    break;
                }
                case 7: {
                    readingOpcode = true;
                    return new Userlist(opcode);
                    break;
                }
                case 8: {
                    if (nextByte == '\n') {
                        readingOpcode = true;
                        return new Stat(opcode,popString());
                    }
                    break;
                }
                case 9: {
                    if(timesInCase == 0){
                        if(nextByte == '\n'){
                            pmTruePostFalse = true;
                        }
                        timesInCase++;
                    }
                    if(nextByte == '\n'){
                        if(numOfZero == 0){
                            s1 = popString();
                        }
                        if(numOfZero == 1){
                            s2 = popString();
                            timesInCase = 0;
                            readingOpcode = true;
                            return new Notification();
                        }
                    }
                    break;
                }
                case 10: {
                    break;
                }
                case 11: {
                    break;
                }

            }
        }
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
