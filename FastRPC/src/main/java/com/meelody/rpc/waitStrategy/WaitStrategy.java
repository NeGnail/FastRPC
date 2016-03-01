package main.java.com.meelody.rpc.waitStrategy;

import main.java.com.meelody.rpc.client.cache.Cache;
import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.info.Call;


public interface WaitStrategy {
    public Call waitFor(Cache cache, long id) throws InterruptedException, NetException;
    public void signalAllWhenBlocking();
}
