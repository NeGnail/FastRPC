package main.java.com.meelody.rpc.protocol;

import main.java.com.meelody.rpc.client.cache.Cache;
import main.java.com.meelody.rpc.exception.ConfigurationException;
import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.net.NetWork;
import main.java.com.meelody.rpc.net.bio.*;
import main.java.com.meelody.rpc.info.Call;
import main.java.com.meelody.rpc.waitStrategy.WaitStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;


public class lFlowProtocol extends BothBsyProtocol implements NetWork {
    private final Log log = LogFactory.getLog(getClass());
    private BioWriter writer;
    private BioReader reader;
    private Cache cache;
    private boolean running;
    private WaitStrategy waitStrategy;
    private BioUnDelayConnector connector;

    public lFlowProtocol(Cache cache, WaitStrategy waitStrategy) throws NetException, ClassNotFoundException, IOException, InstantiationException, ConfigurationException, IllegalAccessException {
        this.cache = cache;
        this.waitStrategy = waitStrategy;
        init();
        prepare();
    }

    @Override
    protected void doInit() {

    }

    @Override
    protected void doPrepare(ExecutorService ioExecutor, ExecutorService recExecutor) throws NetException {
        reader = new BioReader(ioExecutor, recExecutor, (int) (RW_COUNT * RW_RATIO / (RW_RATIO + 1)), new BioClientReaderCompletionHandler(cache, waitStrategy));
        writer = new BioWriter(ioExecutor, recExecutor, (int) (RW_COUNT / (RW_RATIO + 1)), new BioClientWriterCompletionHandler(reader));
        connector = new BioUnDelayConnector(new BioClientConnectorCompletionHandler(writer));

    }

    @Override
    public void start() throws NetException {
        log.info("service Initializing");
        running=true;
        try {
            reader.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new NetException("reader start error");
        }
        try {
            writer.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new NetException("writer start error");
        }
        log.info("service opened");

    }

    @Override
    public void stop()  {
        log.info("service stop  process");
        running=false;
        try {
            writer.stop();
        } catch (IOException e) {
            e.printStackTrace();
            log.warn("writer stop error");
        }
        try {
            reader.stop();
        } catch (IOException e) {
            e.printStackTrace();
            log.warn("reader stop error");
        }
        log.info("service stop has been completed");
    }

    @Override
    public void publish(int port) {

    }

    @Override
    public void call(Call call, InetSocketAddress address) throws NetException, IOException, InterruptedException, ExecutionException {
        if (!running) {
            start();
        }
        connector.connect(address,false,call);
    }
}
