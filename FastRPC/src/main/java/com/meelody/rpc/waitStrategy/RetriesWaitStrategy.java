package main.java.com.meelody.rpc.waitStrategy;

import main.java.com.meelody.rpc.client.cache.Cache;
import main.java.com.meelody.rpc.info.Call;

import java.util.concurrent.locks.LockSupport;


public class RetriesWaitStrategy implements WaitStrategy {
    private static final int DEF_COUNT = 100;
    private static final long TIME=1L;
    private int count;

    public RetriesWaitStrategy() {
        count = DEF_COUNT;
    }

    public RetriesWaitStrategy(int count) {
        this.count = count;
    }

    @Override
    public Call waitFor(Cache cache, long id) throws InterruptedException {
        Call call = null;
        while ((call = (Call) cache.getObject(id)) == null) {
            if (count > 100) {
                count--;
            } else if (count > 0) {
                count--;
                Thread.yield();
            } else {
                LockSupport.parkNanos(TIME);
            }
        }
        return call;
    }


    @Override
    public void signalAllWhenBlocking() {

    }
}
