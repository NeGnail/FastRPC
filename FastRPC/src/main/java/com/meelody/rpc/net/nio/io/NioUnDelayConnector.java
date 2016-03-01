package main.java.com.meelody.rpc.net.nio.io;

import main.java.com.meelody.rpc.net.io.NetConnector;
import main.java.com.meelody.rpc.net.nio.handler.NioConnectorCompletionHandler;
import main.java.com.meelody.rpc.info.Call;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;


public class NioUnDelayConnector implements NetConnector {
    private NioConnectorCompletionHandler handler;

    public NioUnDelayConnector(NioConnectorCompletionHandler handler) {
        this.handler = handler;
    }

    @Override
    public void connect(InetSocketAddress address, boolean block, Call call) throws IOException {
        SocketChannel socketChannel=SocketChannel.open();
        socketChannel.configureBlocking(block);
        socketChannel.connect(address);
        handler.handle(socketChannel,call);
    }
}
