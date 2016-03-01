package main.java.com.meelody.rpc.net.Handler;

import main.java.com.meelody.rpc.client.cache.Cache;
import main.java.com.meelody.rpc.waitStrategy.WaitStrategy;


public class AbstractReaderCompletionHandler {
    protected Cache cache;
    protected WaitStrategy waitStrategy;

    public AbstractReaderCompletionHandler(Cache cache, WaitStrategy waitStrategy) {
        this.cache = cache;
        this.waitStrategy = waitStrategy;
    }
}
