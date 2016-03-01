package main.java.com.meelody.rpc.net.nio.io;

import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.net.io.NetWorkBase;
import main.java.com.meelody.rpc.net.nio.NioWrition;
import main.java.com.meelody.rpc.net.nio.handler.NioWriterCompletionHandler;
import main.java.com.meelody.rpc.util.RpcUtil;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;


public class NioUnDelayWriter extends NetWorkBase {
    private BlockingQueue<NioWrition> pool=new LinkedBlockingQueue<>();
    private NioWriterCompletionHandler handler;
    private int count;
    public NioUnDelayWriter(ExecutorService executor, ExecutorService receiver, int count, NioWriterCompletionHandler handler) {
        super(executor, receiver);
        this.handler=handler;
        this.count=count;
    }

    @Override
    protected int getCount() {
        return count;
    }

    @Override
    protected void receive(Object task) throws InterruptedException {
        pool.put((NioWrition) task);
    }

    @Override
    protected int getRemainingCapacity() {
        return pool.remainingCapacity();
    }

    @Override
    protected void process() throws InterruptedException, IOException {
        NioWrition writion=pool.take();
        RpcUtil.writeCall(writion.getCall(),writion.getSocketChannel());
        handler.handle(writion.getSocketChannel());
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


}
