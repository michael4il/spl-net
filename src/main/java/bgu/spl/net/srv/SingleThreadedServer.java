//package bgu.spl.net.srv;
//
//import bgu.spl.net.api.MessageEncoderDecoder;
//import bgu.spl.net.api.MessagingProtocol;
//import bgu.spl.net.api.bidi.ConnectionHandler;
//import bgu.spl.net.api.bidi.ConnectionHandlerTPC;
//
//import java.util.function.Supplier;
//
//public class SingleThreadedServer extends BaseServer {
//
//    public SingleThreadedServer(
//            int port,
//            Supplier<MessagingProtocol> protocolFactory,
//            Supplier<MessageEncoderDecoder> encoderDecoderFactory) {
//
//        super(port,protocolFactory,encoderDecoderFactory);
//    }
//
// /*   @Override
//    protected void execute(BlockingConnectionHandler handler) {
//        handler.run();
//
//    }*/
//
//
//    @Override
//    protected void execute(ConnectionHandlerTPC handler) {
//
//    }
//}