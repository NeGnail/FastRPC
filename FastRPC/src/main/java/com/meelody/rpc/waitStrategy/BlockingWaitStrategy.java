package main.java.com.meelody.rpc.waitStrategy;

import main.java.com.meelody.rpc.client.cache.Cache;
import main.java.com.meelody.rpc.info.Call;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class BlockingWaitStrategy implements WaitStrategy{
    private ReentrantLock lock=new ReentrantLock();
    private Condition condition=lock.newCondition();
    @Override
    public Call waitFor(Cache cache, long id) throws InterruptedException {
        Call call=null;
        try {
            lock.lock();

            while((call= (Call) cache.getObject(id))==null){
                condition.await();
            }
            return call;
        }finally {
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
