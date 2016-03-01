package main.java.com.meelody.rpc.net.nio.io;

import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.net.io.NetWorkBase;
import main.java.com.meelody.rpc.net.nio.handler.NioReaderCompletionHandler;
import main.java.com.meelody.rpc.util.RpcUtil;
import main.java.com.meelody.rpc.info.Call;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;


public class NioReader extends NetWorkBase {
    private BlockingQueue<SocketChannel> pool=new LinkedBlockingQueue<>();
    private NioReaderCompletionHandler handler;
    private int count;

    public NioReader(ExecutorService executor, ExecutorService receiver, int count, NioReaderCompletionHandler handler) {
        super(executor, receiver);
        this.handler = handler;
        this.count=count;
    }



    @Override
    public void start() throws IOException, NetException, InterruptedException, ExecutionException {
        running=true;
        execute();
    }

    @Override
    public void stop() throws IOException {
        receiver.shutdown();
        while(!pool.isEmpty()){
        }
        running=false;
        executor.shutdown();
    }

    @Override
    protected int getCount() {
        return count;
    }

    @Override
    protected void receive(Object task) throws InterruptedException {
        pool.put((SocketChannel) task);
    }

    @Override
    protected int getRemainingCapacity() {
        return pool.remainingCapacity();
    }

    @Override
    protected void process() throws InterruptedException, IOException {
        SocketChannel socketChannel= pool.take();
        Call call=RpcUtil.readCall(socketChannel);
        handler.handle(call,socketChannel);
    }
}
