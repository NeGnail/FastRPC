package main.java.com.meelody.rpc.client;

import main.java.com.meelody.rpc.serviceInterface.Person;
import main.java.com.meelody.rpc.client.proxy.JdkProxy;
import main.java.com.meelody.rpc.client.proxy.Proxy;
import main.java.com.meelody.rpc.exception.ConfigurationException;
import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.executor.AsyExcutorHandler;
import main.java.com.meelody.rpc.waitStrategy.WaitStrategy;

import java.io.IOException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;


public class SimpleClient{
    private Proxy proxy;
    private AsyExcutorHandler excutor;

    public WaitStrategy getWaitStrategy() {
        return waitStrategy;
    }

    public void setWaitStrategy(WaitStrategy waitStrategy) {
        this.waitStrategy = waitStrategy;
    }

    private WaitStrategy waitStrategy;  //如果不设置默认为BlockingWaitStrategy
    public SimpleClient() {
        this.proxy = new JdkProxy();
        try {
            this.excutor = new AsyExcutorHandler(waitStrategy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getProxy(Class clazz){
        return proxy.getProxy(clazz,excutor);
    }
    public Object getProxy(Class clazz,Callback callback){
        excutor.registCallback(clazz,callback);
        return proxy.getProxy(clazz,excutor);
    }
    public Object getProxy(Class clazz,boolean cache){
        if(cache){
            excutor.registerCache(clazz);
        }
        return proxy.getProxy(clazz,excutor);
    }
    public static void main(String[] args) throws IOException, NetException, ClassNotFoundException, InstantiationException, ConfigurationException, IllegalAccessException {
        SimpleClient c=new SimpleClient();
//        final Person p= (Person) c.getProxy(Person.class, new Callback() {
//            @Override
//            public void handleResult(Object result, Method method, Object[] args) {
//                System.out.println(result);
//            }
//
//            @Override
//            public void handleExeption(RemoteException e) {
//
//            }
//        });

        final Person p= (Person) c.getProxy(Person.class,false);
//        System.out.println("<<<<<<<<<");
//        i.sys(3);
//        System.out.println(">>>>>>>>>>");

        for(int i=0;i<50;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int j=0;j<100;j++){
                        System.out.println(p.sys(6));
                        System.out.println("---->"+Thread.currentThread().getId());
                    }
                }
            }).start();
        }

//        for(int j=0;j<50;j++){
//            System.out.println(p.sys(6));
//            System.out.println("seed");
//        }

    }
}
