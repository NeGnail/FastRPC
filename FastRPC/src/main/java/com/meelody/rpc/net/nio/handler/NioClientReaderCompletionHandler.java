package main.java.com.meelody.rpc.net.nio.handler;

import main.java.com.meelody.rpc.client.cache.Cache;
import main.java.com.meelody.rpc.net.Handler.AbstractReaderCompletionHandler;
import main.java.com.meelody.rpc.info.Call;
import main.java.com.meelody.rpc.waitStrategy.WaitStrategy;

import java.io.IOException;
import java.nio.channels.SocketChannel;


public class NioClientReaderCompletionHandler extends AbstractReaderCompletionHandler implements NioReaderCompletionHandler {


    public NioClientReaderCompletionHandler(Cache cache, WaitStrategy waitStrategy) {
        super(cache, waitStrategy);
    }

    @Override
    public void handle(Call call, SocketChannel socketChannel) throws IOException {
        socketChannel.close();
        cache.put(call.getId(),call);
        waitStrategy.signalAllWhenBlocking();

    }
}
