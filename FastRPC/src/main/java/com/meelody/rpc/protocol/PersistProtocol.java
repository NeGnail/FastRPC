package main.java.com.meelody.rpc.protocol;


import main.java.com.meelody.rpc.client.cache.Cache;
import main.java.com.meelody.rpc.exception.ConfigurationException;
import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.net.NetWork;
import main.java.com.meelody.rpc.net.nio.handler.NioServerAcceptorCompletionHandler;
import main.java.com.meelody.rpc.net.nio.handler.NioServerWriterCompletionHandler;
import main.java.com.meelody.rpc.net.nio.io.NioAcceptor;
import main.java.com.meelody.rpc.net.nio.io.NioReader;
import main.java.com.meelody.rpc.net.nio.io.NioUnDelayWriter;
import main.java.com.meelody.rpc.net.nio.selector.NetSelector;
import main.java.com.meelody.rpc.net.nio.selector.PerformSelector;
import main.java.com.meelody.rpc.server.PerformLibrary;
import main.java.com.meelody.rpc.util.Config;
import main.java.com.meelody.rpc.info.Call;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PersistProtocol extends BothBsyProtocol implements NetWork{
    protected final Log log = LogFactory.getLog(getClass());

    private NioAcceptor acceptor;
    private NioUnDelayWriter writer;
    private NioReader reader;
    public NetSelector accepSelector;
    public NetSelector readSelector;
    private Cache cache;

    private int CON_COUNT;
    private int HAND_COUNT;

    boolean running;

    private final int DEF_CON_COUNT = 1;
    private final int DEF_HAND_COUNT = Runtime.getRuntime().availableProcessors() / 2 ;

    public PersistProtocol() throws NetException, ConfigurationException, ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        init();
        prepare();
    }

    @Override
    protected void doPrepare(ExecutorService ioExecutor, ExecutorService recExecutor) throws NetException {

        ExecutorService connectorExecutor = CON_COUNT == 1 ? Executors.newSingleThreadExecutor() : Executors.newFixedThreadPool(CON_COUNT);
        ExecutorService handleExecutor = Executors.newFixedThreadPool(HAND_COUNT);

        NioServerAcceptorCompletionHandler nioServerAcceptorCompletionHandler=new NioServerAcceptorCompletionHandler();

        acceptor = new NioAcceptor(connectorExecutor, recExecutor, CON_COUNT,nioServerAcceptorCompletionHandler);
        writer = new NioUnDelayWriter(ioExecutor, recExecutor, (int) (RW_COUNT / (RW_RATIO + 1)), new NioServerWriterCompletionHandler());
        reader = new NioReader(ioExecutor, recExecutor, (int) (RW_COUNT * RW_RATIO / RW_RATIO + 1), new PerformLibrary(handleExecutor, cache, writer));

        try {
            accepSelector=new PerformSelector(nioServerAcceptorCompletionHandler);
            readSelector = new PerformSelector(reader);
        } catch (IOException e) {
            e.printStackTrace();
            throw new NetException("The selector open error");
        }
        nioServerAcceptorCompletionHandler.setSelector(readSelector);
    }

    @Override
    protected void doInit() throws IOException, ConfigurationException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String cacheString=Config.getCACHE();
        this.cache = (Cache) Class.forName(cacheString).newInstance();


        CON_COUNT = Config.getCONNECT_COUNT()!=0?Config.getCONNECT_COUNT():DEF_CON_COUNT;
        HAND_COUNT = Config.getHANDLE_COUNT()!=0? DEF_HAND_COUNT:DEF_HAND_COUNT;

    }







    @Override
    public void start() throws NetException {
        log.info("service Initializing");
        running=true;
        doStart(readSelector,writer,reader);

        try {
            accepSelector.start();

        } catch (Exception e) {
            e.printStackTrace();
            throw new NetException("accept start error");
        }

        log.info("service opened");

    }
    @Override
    public void publish(int port) throws NetException {
        if(running==false){
            start();
            running=true;
        }
        ServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress((port)));
            accepSelector.regist(serverSocketChannel, SelectionKey.OP_ACCEPT, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void call(Call call, InetSocketAddress address) throws NetException, IOException, InterruptedException {

    }


    @Override
    public void stop() throws IOException {
        log.info("stop  process");
        running=false;
        accepSelector.stop();
        readSelector.stop();
        reader.stop();
        writer.stop();
        log.info("stop has been completed");
    }


    public static void main(String[] args) throws NetException, IOException, ConfigurationException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        PersistProtocol protocol = new PersistProtocol();
        protocol.start();
        protocol.publish(8080);
    }
}
