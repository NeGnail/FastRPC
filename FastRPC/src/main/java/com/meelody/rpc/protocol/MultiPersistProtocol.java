package main.java.com.meelody.rpc.protocol;


import main.java.com.meelody.rpc.client.cache.Cache;
import main.java.com.meelody.rpc.exception.ConfigurationException;
import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.net.NetWork;
import main.java.com.meelody.rpc.net.nio.handler.NioClientConnectorHandler;
import main.java.com.meelody.rpc.net.nio.handler.NioClientReaderCompletionHandler;
import main.java.com.meelody.rpc.net.nio.handler.NioClientWriterCompetionHandler;
import main.java.com.meelody.rpc.net.nio.io.NioReader;
import main.java.com.meelody.rpc.net.nio.io.NioUnDelayConnector;
import main.java.com.meelody.rpc.net.nio.io.NioUnDelayWriter;
import main.java.com.meelody.rpc.net.nio.selector.NetSelector;
import main.java.com.meelody.rpc.net.nio.selector.PerformSelector;
import main.java.com.meelody.rpc.info.Call;
import main.java.com.meelody.rpc.waitStrategy.WaitStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;


public class MultiPersistProtocol extends BothBsyProtocol implements NetWork{
    protected final Log log = LogFactory.getLog(getClass());

    private NioUnDelayWriter writer;
    private NioReader reader;
    private NetSelector selector;
    private Cache cache;
    private AtomicBoolean running=new AtomicBoolean(false);   //1为开启
    private WaitStrategy waitStrategy;
    private NioUnDelayConnector connector;






    public MultiPersistProtocol(Cache cache, WaitStrategy waitStrategy) throws NetException, ConfigurationException, ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        this.waitStrategy = waitStrategy;
        this.cache = cache;
        init();
        prepare();
    }



    @Override
    protected void doInit() {

    }

    @Override
    protected void doPrepare(ExecutorService ioExecutor, ExecutorService recExecutor) throws NetException {
        NioClientWriterCompetionHandler nioWriterCompletionHandler=new NioClientWriterCompetionHandler(selector);

        writer = new NioUnDelayWriter(ioExecutor, recExecutor, (int) (RW_COUNT / (RW_RATIO + 1)),nioWriterCompletionHandler );
        connector=new NioUnDelayConnector(new NioClientConnectorHandler(writer));
        reader = new NioReader(ioExecutor, recExecutor, (int) (RW_COUNT *RW_RATIO / (RW_RATIO + 1)), new NioClientReaderCompletionHandler(cache, waitStrategy));

        try {
            selector = new PerformSelector(reader);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NetException("The selector open error");
        }
        nioWriterCompletionHandler.setSelector(selector);
    }


    @Override
    public void publish(int port) {

    }

    @Override
    public void call(Call call, InetSocketAddress address) throws NetException, IOException, InterruptedException {
        if(running.compareAndSet(false,true)){
            System.out.println(running.get());
            start();
        }
        connector.connect(address,false,call);


    }




    @Override
    public void start() throws NetException {
        log.info("service Initializing");
        doStart(selector,writer,reader);
        log.info("service opened");


    }


    @Override
    public void stop(){
        log.info("service stop  process");
        if(running.compareAndSet(false,true)){


            try {
                writer.stop();
            } catch (IOException e) {
                e.printStackTrace();
                log.warn("writer stop error");
            }
            try {
                selector.stop();
            } catch (IOException e) {
                e.printStackTrace();
                log.warn("selector stop error");
            }
            try {
                reader.stop();
            } catch (IOException e) {
                e.printStackTrace();
                log.warn("reader stop error");
            }
            log.info("service stop has been completed");
        }
    }

}
