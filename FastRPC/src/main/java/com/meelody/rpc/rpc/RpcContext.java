package main.java.com.meelody.rpc.rpc;

import java.util.HashMap;
import java.util.Map;


public class RpcContext {
    private static ThreadLocal<Map<String,Object>> mapThreadLocal=new ThreadLocal<Map<String,Object>>();

    public static void addObject(String key,Object value){
        mapThreadLocal.get().put(key,value);
    }
    public static Object getObject(String key){
       return mapThreadLocal.get().get(key);
    }
    public static Map<String,Object> getContent(){
        return mapThreadLocal.get();
    }
}
