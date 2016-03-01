package main.java.com.meelody.rpc.net.io;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;


public abstract class NioBsyProcessor extends NetWorkBase {
    protected BlockingQueue<SelectionKey> pool= new LinkedBlockingQueue<SelectionKey>();

    public NioBsyProcessor(ExecutorService executor, ExecutorService receiver) {
        super(executor, receiver);
    }

    @Override
    protected void process() throws InterruptedException, IOException {
        SelectionKey selectionKey=pool.take();
        process(selectionKey);
        selectionKey.cancel();
    }

    @Override
    protected void receive(Object task) throws InterruptedException {
        pool.put((SelectionKey) task);
    }

    protected abstract void process(SelectionKey selectionKey) throws IOException;

    @Override
    protected int getRemainingCapacity() {
        return pool.remainingCapacity();
    }
}
