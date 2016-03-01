package main.java.com.meelody.rpc.net.nio.handler;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;


public interface NioWriterCompletionHandler {
    void handle(SocketChannel socketChannel) throws IOException, InterruptedException;

}
