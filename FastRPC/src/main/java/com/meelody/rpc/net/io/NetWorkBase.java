package main.java.com.meelody.rpc.net.io;

import main.java.com.meelody.rpc.net.NetWork;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;


public abstract class NetWorkBase implements NetWork{
    protected final Log log = LogFactory.getLog(getClass());
    protected int remain;
    protected ExecutorService executor;
    protected ExecutorService receiver;
    protected boolean running;

    public NetWorkBase(ExecutorService executor, ExecutorService receiver) {
        this.executor = executor;
        this.receiver = receiver;

    }

    protected void execute(){
        for (int i = 0; i < getCount(); i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    while (running) {
                        try {
                            process();
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.warn("A process to io an error occurs");
                        }
                    }
                }

            });
        }
    }

    protected abstract int getCount();


    public void addTask(final Object task){
        if(getRemainingCapacity()<remain){
            receiver.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        receive(task);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        log.warn("Missing a connection");
                    }
                }
            });
        }else{
            try {
                receive(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.warn("Missing a connection");
            }
        }
    }

    protected abstract void receive(Object task) throws InterruptedException;

    protected abstract int getRemainingCapacity();

    protected abstract void process() throws InterruptedException, IOException, ClassNotFoundException;
}
