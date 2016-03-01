package main.java.com.meelody.rpc.net.bio;

import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.net.io.NetWorkBase;
import main.java.com.meelody.rpc.util.RpcUtil;
import main.java.com.meelody.rpc.info.Call;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;


public class BioReader extends NetWorkBase{
    private BlockingQueue<Socket> pool=new LinkedBlockingQueue<>();
    private int count;
    private BioReaderCompletionHandler handler;

    public BioReader(ExecutorService executor, ExecutorService receiver, int count, BioReaderCompletionHandler handler) {
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
        pool.put((Socket) task);
    }

    @Override
    protected int getRemainingCapacity() {
        return pool.remainingCapacity();
    }

    @Override
    protected void process() throws InterruptedException, IOException, ClassNotFoundException {
        Socket socket=pool.take();
        Call call=RpcUtil.readCall(socket);
        handler.handle(call,socket);
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
