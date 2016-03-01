package main.java.com.meelody.rpc.net.nio.handler;

import main.java.com.meelody.rpc.net.nio.selector.NetSelector;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;


public class NioServerAcceptorCompletionHandler implements NioAcceptorCompletionHandler {
    private NetSelector selector;

    public NetSelector getSelector() {
        return selector;
    }

    public void setSelector(NetSelector selector) {
        this.selector = selector;
    }

    public NioServerAcceptorCompletionHandler() {
    }

    public NioServerAcceptorCompletionHandler(NetSelector selector) {
        this.selector = selector;
    }

    @Override
    public void handle(SocketChannel socketChannel, Object attachment) throws IOException, InterruptedException {
        socketChannel.configureBlocking(false);
        selector.regist(socketChannel, SelectionKey.OP_READ, null);
    }
}
