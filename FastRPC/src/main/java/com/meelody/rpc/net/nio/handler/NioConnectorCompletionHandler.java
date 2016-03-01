package main.java.com.meelody.rpc.net.nio.handler;

import main.java.com.meelody.rpc.info.Call;

import java.io.IOException;
import java.nio.channels.SocketChannel;


public interface NioConnectorCompletionHandler {
    void handle(SocketChannel socketChannel,Call call) throws IOException;
}
