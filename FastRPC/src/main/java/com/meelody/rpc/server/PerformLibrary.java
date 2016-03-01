package main.java.com.meelody.rpc.server;

import main.java.com.meelody.rpc.client.cache.Cache;
import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.net.NetWork;
import main.java.com.meelody.rpc.net.nio.handler.NioReaderCompletionHandler;
import main.java.com.meelody.rpc.net.nio.io.NioUnDelayWriter;
import main.java.com.meelody.rpc.net.nio.NioWrition;
import main.java.com.meelody.rpc.util.RpcUtil;
import main.java.com.meelody.rpc.info.Call;
import main.java.com.meelody.rpc.info.Execution;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;


public class PerformLibrary implements NioReaderCompletionHandler,NetWork{
    protected final Log log = LogFactory.getLog(getClass());
    private ExecutorService executor;
    private Cache cache;
    private boolean running;
    private NioUnDelayWriter writer;

    public PerformLibrary(ExecutorService executor, Cache cache, NioUnDelayWriter writer) {
        this.executor = executor;
        this.cache = cache;
        this.writer = writer;
    }

    public Object excute(Execution execution) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        String service=execution.getService();
        String method=execution.getMethod();
        Object[] args=execution.getArgs();
        int version=execution.getVersion();

        Object serviceObj=cache.getObject(service);
        Method methodObj= (Method) cache.getObject(method);
        if(serviceObj==null){
            serviceObj=RpcUtil.createObject(service);
            cache.put(service,serviceObj);
        }
        if(methodObj==null){
            methodObj=RpcUtil.findMethod(serviceObj,method,args);
            cache.put(method,methodObj);
        }

        return RpcUtil.execute(serviceObj,methodObj,args);

    }

    @Override
    public void handle(final Call call, final SocketChannel socketChannel) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Execution execution=RpcUtil.readExecution(call);
                    Object result=excute(execution);
                    RpcUtil.update(call, result);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.warn("Service error");
                    try {
                        RpcUtil.update(call, e);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                writer.addTask(new NioWrition(socketChannel,call));
            }
        });
    }

    @Override
    public void start() throws IOException, NetException, InterruptedException, ExecutionException {


    }

    @Override
    public void stop() throws IOException {

    }
}
