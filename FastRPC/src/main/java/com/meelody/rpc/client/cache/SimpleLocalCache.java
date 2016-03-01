package main.java.com.meelody.rpc.client.cache;


import main.java.com.meelody.rpc.info.Execution;

import java.util.concurrent.ConcurrentHashMap;


public class SimpleLocalCache implements Cache{
    private  ConcurrentHashMap<Long,Object> longCache=new ConcurrentHashMap<>();
    private  ConcurrentHashMap<String,Object> stringCache= new ConcurrentHashMap<>();
    private  ConcurrentHashMap<Execution,Object> resultCache= new ConcurrentHashMap<>();
    public void put(long key, Object value) {
        longCache.put(key,value);
    }


    public void put(String key, Object value) {
        stringCache.put(key,value);
    }

    public Object getObject(long key){
        return longCache.get(key);
    }
    public Object getObject(String key){
        return stringCache.get(key);
    }


}


