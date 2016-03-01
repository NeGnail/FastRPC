package main.java.com.meelody.rpc.executor;

import main.java.com.meelody.rpc.exception.ConfigurationException;
import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.exception.RemoteException;
import main.java.com.meelody.rpc.waitStrategy.WaitStrategy;

import java.io.IOException;
import java.lang.reflect.Method;


public class ExcutorHandler extends AbstractExcutorHandler{


    public ExcutorHandler(WaitStrategy waitStrategy) throws IllegalAccessException, IOException, ConfigurationException, InstantiationException, NetException, ClassNotFoundException {
        super(waitStrategy);
    }



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return synProcess(method,args);
    }
}
