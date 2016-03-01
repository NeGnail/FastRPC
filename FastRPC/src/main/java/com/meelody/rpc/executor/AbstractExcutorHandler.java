package main.java.com.meelody.rpc.executor;

import main.java.com.meelody.rpc.client.LoadBalance;
import main.java.com.meelody.rpc.client.cache.Cache;
import main.java.com.meelody.rpc.exception.ConfigurationException;
import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.exception.RemoteException;
import main.java.com.meelody.rpc.protocol.Protocol;
import main.java.com.meelody.rpc.serializer.Serializer;
import main.java.com.meelody.rpc.util.Config;
import main.java.com.meelody.rpc.util.RpcUtil;
import main.java.com.meelody.rpc.info.Call;
import main.java.com.meelody.rpc.info.Execution;
import main.java.com.meelody.rpc.waitStrategy.BlockingWaitStrategy;
import main.java.com.meelody.rpc.waitStrategy.WaitStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;


public abstract class AbstractExcutorHandler implements InvocationHandler{

    protected final Log log = LogFactory.getLog(getClass());
    protected ConcurrentMap<String,Integer> versions=new ConcurrentHashMap<String, Integer>();
    protected Serializer serializer;
    protected Protocol protocol;
    protected LoadBalance loadBalance;
    protected final int defaultVersion = 0;
    protected Cache cache;
    protected WaitStrategy waitStrategy;
    protected ConcurrentHashMap<Class,Boolean> cacheObj=new ConcurrentHashMap();

    public AbstractExcutorHandler(WaitStrategy waitStrategy) throws NetException, IOException, ConfigurationException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        this.waitStrategy=waitStrategy;
        if(this.waitStrategy==null){
            this.waitStrategy=new BlockingWaitStrategy();
        }
        serializer= (Serializer) Class.forName(Config.getSERIALIZER()).newInstance();
        loadBalance= (LoadBalance) Class.forName(Config.getLOADBALANCE()).newInstance();
        cache = (Cache) Class.forName(Config.getCACHE()).newInstance();
        try {
            protocol= (Protocol) Class.forName(Config.getPROTOCOL()).getConstructor(Cache.class,WaitStrategy.class).newInstance(cache,this.waitStrategy);
        } catch (Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new NetException("Protocol init error");

        }

    }

    protected Object synProcess(Method method,Object[] args) throws IOException, InterruptedException, ExecutionException, ClassNotFoundException, NetException, RemoteException {
        Execution execution= RpcUtil.createExcution(method,args,versions,defaultVersion);
        Object cacheResult=cacheProceccs(method,execution);
        if(cacheResult!=null){
            return cacheResult;
        }
        InetSocketAddress address=loadBalance.findServiceAddress(execution);
        Call request=RpcUtil.createCall(execution);
        protocol.call(request,address);
        Call call= waitStrategy.waitFor(cache,request.getId());
        Object result=RpcUtil.readResult(call);
        if(result instanceof Exception){
            throw new RemoteException(((Exception) result).getMessage(),((Exception) result).getCause());
        }
        storeCache(method,execution,result);
        return result;
    }

    protected Object cacheProceccs(Method method, Execution execution) {
        Class clazz=method.getDeclaringClass();
        if(cacheObj.get(clazz)!=null&&cacheObj.get(clazz)==true){
                String key=RpcUtil.createCachekey(execution);
                return cache.getObject(key);
        }
        return null;
    }
    protected void storeCache(Method method,Execution execution,Object result){
        Class clazz=method.getDeclaringClass();

        if(cacheObj.get(clazz)!=null&&cacheObj.get(clazz)==true){
            String key=RpcUtil.createCachekey(execution);
            cache.put(key,result);
        }
    }


    protected Object singleRecall(long requestId) throws NetException, InterruptedException, IOException, ClassNotFoundException, RemoteException {
        Call call= waitStrategy.waitFor(cache,requestId);
        Object result=RpcUtil.readResult(call);
        if(result instanceof Exception){
            throw new RemoteException(((Exception) result).getMessage(),((Exception) result).getCause());
        }
        return result;
    }





    public void registerVisions(String serviceMethod,int version){
        this.versions.put(serviceMethod,version);
    }

    public void registerCache(Class clazz){
        cacheObj.put(clazz,true);
    }


}
