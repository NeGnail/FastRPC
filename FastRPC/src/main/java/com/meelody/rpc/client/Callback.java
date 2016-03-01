package main.java.com.meelody.rpc.client;

import java.lang.reflect.Method;
import java.rmi.RemoteException;


public interface Callback {
    void handleResult(Object result, Method method,Object[] args);
    void handleExeption(RemoteException e);
}
