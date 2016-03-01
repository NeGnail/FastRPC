package main.java.com.meelody.rpc.client.proxy;

import main.java.com.meelody.rpc.executor.AbstractExcutorHandler;
import main.java.com.meelody.rpc.executor.ExcutorHandler;


public class JdkProxy implements Proxy{

    @Override
    public Object getProxy(Class clazz, AbstractExcutorHandler excutor) {
      return java.lang.reflect.Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},excutor);
    }
}
