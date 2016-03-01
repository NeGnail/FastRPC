package main.java.com.meelody.rpc.client.proxy;

import main.java.com.meelody.rpc.executor.AbstractExcutorHandler;
import main.java.com.meelody.rpc.executor.ExcutorHandler;


public interface Proxy {
   Object  getProxy(Class clazz,AbstractExcutorHandler excutor);

}
