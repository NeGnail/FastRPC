package main.java.com.meelody.rpc.net.nio.handler;

import main.java.com.meelody.rpc.info.Call;

import java.io.IOException;
import java.nio.channels.SocketChannel;


public interface NioReaderCompletionHandler {
    void handle(Call call, SocketChannel socketChannel) throws IOException;
}
