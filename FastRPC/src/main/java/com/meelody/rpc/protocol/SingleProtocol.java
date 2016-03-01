package main.java.com.meelody.rpc.protocol;

import main.java.com.meelody.rpc.client.cache.Cache;
import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.net.bio.BioClientReaderCompletionHandler;
import main.java.com.meelody.rpc.net.bio.BioReaderCompletionHandler;
import main.java.com.meelody.rpc.util.RpcUtil;
import main.java.com.meelody.rpc.info.Call;
import main.java.com.meelody.rpc.waitStrategy.WaitStrategy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutionException;


public class SingleProtocol implements Protocol{
    private Cache cache;
    private WaitStrategy waitStrategy;
    private BioReaderCompletionHandler bioReaderCompletionHandler;

    public SingleProtocol(Cache cache, WaitStrategy waitStrategy) {
        this.cache=cache;
        this.waitStrategy=waitStrategy;
        bioReaderCompletionHandler=new BioClientReaderCompletionHandler(cache,waitStrategy);
    }

    @Override
    public void publish(int port) {

    }

    @Override
    public void call(Call call, InetSocketAddress address) throws NetException, IOException, InterruptedException, ExecutionException, ClassNotFoundException {
        Socket socket=new Socket();
        socket.connect(address);
        RpcUtil.writeCall(call,socket);
        Call result=RpcUtil.readCall(socket);
        bioReaderCompletionHandler.handle(result,socket);
    }
}
