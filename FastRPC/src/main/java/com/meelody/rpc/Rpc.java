package main.java.com.meelody.rpc;

import main.java.com.meelody.rpc.client.Callback;
import main.java.com.meelody.rpc.client.SimpleClient;

import main.java.com.meelody.rpc.protocol.Protocol;
import main.java.com.meelody.rpc.util.Config;

import main.java.com.meelody.rpc.waitStrategy.WaitStrategy;




public class Rpc {
    private static SimpleClient client=new SimpleClient();
    public static Object getObject(Class clazz){
        return client.getProxy(clazz);
    }
    public static Object getObject(Class clazz, Callback callback){
        return client.getProxy(clazz,callback);
    }
    public static Object getObject(Class clazz, Callback callback, WaitStrategy waitStrategy){
        client.setWaitStrategy(waitStrategy);
        return client.getProxy(clazz,callback);

    }
    public static Object getObject(Class clazz,WaitStrategy waitStrategy){
        client.setWaitStrategy(waitStrategy);
        return client.getProxy(clazz);
    }

}
