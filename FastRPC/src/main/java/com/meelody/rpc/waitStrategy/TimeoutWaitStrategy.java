package main.java.com.meelody.rpc.waitStrategy;

import main.java.com.meelody.rpc.client.cache.Cache;
import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.info.Call;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class TimeoutWaitStrategy implements WaitStrategy{
    private ReentrantLock lock=new ReentrantLock();
    private Condition condition=lock.newCondition();
    private static final long DEF_TIME=15<<9;
    private long time;

    public TimeoutWaitStrategy(long time) {
        this.time = time;
    }

    public TimeoutWaitStrategy() {
        time=DEF_TIME;
    }

    @Override
    public Call waitFor(Cache cache, long id) throws InterruptedException, NetException {
        Call call=null;
        try {
            lock.lock();
            long remai=time;
            while((call= (Call) cache.getObject(id))==null){
                if(remai<=0L){
                    throw new NetException("Delay error");
                }
                remai=condition.awaitNanos(remai);

            }
            return call;
        } catch (NetException e) {
            e.printStackTrace();
            throw e;
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void signalAllWhenBlocking() {
        lock.lock();
        try {
            condition.signalAll();
        }
        finally {
            lock.unlock();
        }
    }
}
