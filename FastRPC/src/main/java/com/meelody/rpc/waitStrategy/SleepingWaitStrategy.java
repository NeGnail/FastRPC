package main.java.com.meelody.rpc.waitStrategy;

import main.java.com.meelody.rpc.client.cache.Cache;
import main.java.com.meelody.rpc.info.Call;

import java.util.concurrent.locks.LockSupport;


public class SleepingWaitStrategy implements WaitStrategy {
    private static final long TIME=1L;

    @Override
    public Call waitFor(Cache cache, long id) throws InterruptedException {
        Call call=null;
        while ((call = (Call) cache.getObject(id))==null) {
            LockSupport.park(TIME);
        }
        return call;
    }

    @Override
    public void signalAllWhenBlocking() {

    }
}
