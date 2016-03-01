package main.java.com.meelody.rpc.net.nio.io;

import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.net.io.NetWorkBase;
import main.java.com.meelody.rpc.net.nio.handler.NioAcceptorCompletionHandler;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;


public class NioAcceptor extends NetWorkBase {
    private BlockingQueue<ServerSocketChannel> pool=new LinkedBlockingQueue<>();
    private int count;
    private NioAcceptorCompletionHandler handler;

    public NioAcceptor(ExecutorService executor, ExecutorService receiver, int count, NioAcceptorCompletionHandler handler) {
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
        pool.put((ServerSocketChannel) task);
    }

    @Override
    protected int getRemainingCapacity() {
        return pool.remainingCapacity();
    }

    @Override
    protected void process() throws InterruptedException, IOException {
        ServerSocketChannel serverSocketChannel= pool.take();
        SocketChannel socketChannel=serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        handler.handle(socketChannel,null);
    }
}
