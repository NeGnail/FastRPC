package main.java.com.meelody.rpc.net.nio.handler;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;


public interface NioAcceptorCompletionHandler {

    void handle(SocketChannel socketChannel,Object attachment) throws IOException, InterruptedException;
}
