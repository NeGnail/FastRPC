package main.java.com.meelody.rpc.net.nio.io;

import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.net.io.NetWorkBase;
import main.java.com.meelody.rpc.net.nio.handler.NioClientConnectorHandler;
import main.java.com.meelody.rpc.net.nio.selector.NetSelector;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;


public class NioConnector extends NetWorkBase {
    private BlockingQueue<Connection> pool=new LinkedBlockingQueue<>();
    private int count;
    private NioUnDelayConnector connector;


    public NioConnector(ExecutorService executor, ExecutorService receiver, int count, NetSelector selector,NioUnDelayWriter writer) {
        super(executor, receiver);
        this.count=count;
        connector= new NioUnDelayConnector(new NioClientConnectorHandler(writer));
    }



    @Override
    protected int getCount() {
        return count;
    }

    @Override
    protected void receive(Object task) throws InterruptedException {
        pool.put((Connection) task);
    }

    @Override
    protected int getRemainingCapacity() {
        return pool.remainingCapacity();
    }

    @Override
    protected void process() throws InterruptedException, IOException {
        Connection connection=pool.take();
        connector.connect(connection.getAddress(),true,connection.getCall());
    }



    @Override
    public void start() throws IOException, NetException, InterruptedException, ExecutionException {
        running=false;
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
