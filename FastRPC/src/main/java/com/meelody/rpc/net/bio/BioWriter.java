package main.java.com.meelody.rpc.net.bio;

import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.net.io.NetWorkBase;
import main.java.com.meelody.rpc.util.RpcUtil;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;


public class BioWriter extends NetWorkBase{
    private BlockingQueue<BioWrition> pool=new LinkedBlockingQueue<>();
    private int count;
    private BioWriterCompletionHandler handler;
    public BioWriter(ExecutorService executor, ExecutorService receiver, int count,BioWriterCompletionHandler handler) {
        super(executor, receiver);
        this.count=count;
        this.handler=handler;
    }

    @Override
    protected int getCount() {
        return count;
    }

    @Override
    protected void receive(Object task) throws InterruptedException {
        pool.put((BioWrition) task);
    }

    @Override
    protected int getRemainingCapacity() {
        return pool.remainingCapacity();
    }

    @Override
    protected void process() throws InterruptedException, IOException {
        BioWrition writion=pool.take();
        RpcUtil.writeCall(writion.getCall(),writion.getSocket());
        handler.handle(writion.getSocket());
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
