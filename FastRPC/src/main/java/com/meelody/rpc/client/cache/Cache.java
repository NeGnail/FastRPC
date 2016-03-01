package main.java.com.meelody.rpc.client.cache;


public interface Cache {
    void put(long key,Object value);
    Object getObject(long key);

    void put(String key,Object value);
    Object getObject(String key);



}
