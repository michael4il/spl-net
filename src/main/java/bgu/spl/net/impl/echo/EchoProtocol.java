package bgu.spl.net.impl.echo;

import bgu.spl.net.api.MessagingProtocol;
import java.time.LocalDateTime;

public class EchoProtocol implements MessagingProtocol<String> {

    private boolean shouldTerminate = false;

    @Override
    public String process(String msg) {
        shouldTerminate = "bye".equals(msg);
        System.out.println("[" + LocalDateTime.now() + "]: " + msg);
        return createEcho(msg);
    }

    private String createEcho(String message) {
        return message ;
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
