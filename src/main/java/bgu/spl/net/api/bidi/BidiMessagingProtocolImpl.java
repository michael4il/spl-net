package bgu.spl.net.api.bidi;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol {
    private boolean shouldTerminate;
    @Override
    public void start(int connectionId, Connections connections) {

    }

    @Override
    public void process(Object message) {
        //many private functions. each of them tells what to do
        //for each message.
        //I don't understand why start is different, should be part of register.
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
