package main.java.com.meelody.rpc.net.bio;

import main.java.com.meelody.rpc.client.cache.Cache;
import main.java.com.meelody.rpc.net.Handler.AbstractReaderCompletionHandler;
import main.java.com.meelody.rpc.info.Call;
import main.java.com.meelody.rpc.waitStrategy.WaitStrategy;

import java.io.IOException;
import java.net.Socket;


public class BioClientReaderCompletionHandler extends AbstractReaderCompletionHandler implements BioReaderCompletionHandler{


    public BioClientReaderCompletionHandler(Cache cache, WaitStrategy waitStrategy) {
        super(cache, waitStrategy);
    }

    @Override
    public void handle(Call call, Socket socket) throws IOException {
        socket.close();
        cache.put(call.getId(),call);
        waitStrategy.signalAllWhenBlocking();
    }
}
