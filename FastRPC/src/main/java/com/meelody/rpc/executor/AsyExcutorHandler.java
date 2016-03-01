package main.java.com.meelody.rpc.executor;

import main.java.com.meelody.rpc.client.Callback;
import main.java.com.meelody.rpc.exception.ConfigurationException;
import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.exception.RemoteException;
import main.java.com.meelody.rpc.util.Config;
import main.java.com.meelody.rpc.util.RpcUtil;
import main.java.com.meelody.rpc.info.Call;
import main.java.com.meelody.rpc.info.Execution;
import main.java.com.meelody.rpc.waitStrategy.WaitStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AsyExcutorHandler extends AbstractExcutorHandler{
    private final Log log = LogFactory.getLog(getClass());
    private int COUNT;
    private final int DEF_COUNT=3;
    private ConcurrentHashMap<Class,Callback> callBacks=new ConcurrentHashMap();
    private ExecutorService executorService= Executors.newFixedThreadPool(DEF_COUNT);
    public AsyExcutorHandler(WaitStrategy waitStrategy) throws NetException, ClassNotFoundException, InstantiationException, IllegalAccessException, ConfigurationException, IOException {
        super(waitStrategy);
        COUNT= Config.getHANDLE_COUNT()!=0 ? Config.getHANDLE_COUNT():DEF_COUNT;
    }



    public void registCallback(Class clazz,Callback callback){
        callBacks.put(clazz,callback);
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        Class clazz = method.getDeclaringClass();
        final Callback callback = callBacks.get(clazz);
        if (callback == null) {
            return synProcess(method, args);
        } else {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Execution execution = RpcUtil.createExcution(method, args, versions, defaultVersion);
                    Object cacheResult = cacheProceccs(method, execution);
                    if (cacheResult != null) {
                        callback.handleResult(cacheResult, method, args);
                    }
                    InetSocketAddress address = null;
                    try {
                        address = loadBalance.findServiceAddress(execution);
                        Call request = RpcUtil.createCall(execution);
                        protocol.call(request, address);
                        Call call = waitStrategy.waitFor(cache, request.getId());
                        Object result = RpcUtil.readResult(call);
                        if (result instanceof RemoteException) {
                            callback.handleExeption((java.rmi.RemoteException) result);
                        } else {
                            callback.handleResult(result, method, args);
                            storeCache(method, execution, result);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            return null;
        }
    }
}
