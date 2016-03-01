package main.java.com.meelody.rpc.net.nio.handler;

import main.java.com.meelody.rpc.net.nio.NioWrition;
import main.java.com.meelody.rpc.net.nio.io.NioUnDelayWriter;
import main.java.com.meelody.rpc.info.Call;

import java.io.IOException;
import java.nio.channels.SocketChannel;


public class NioClientConnectorHandler implements NioConnectorCompletionHandler{
    private NioUnDelayWriter writer;

    public NioClientConnectorHandler(NioUnDelayWriter writer) {
        this.writer = writer;
    }

    @Override
    public void handle(SocketChannel socketChannel,Call call) throws IOException {
        while(!socketChannel.finishConnect()){
        }
        writer.addTask(new NioWrition(socketChannel, call));
    }
}
