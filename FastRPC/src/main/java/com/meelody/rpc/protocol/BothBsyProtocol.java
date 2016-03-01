package main.java.com.meelody.rpc.protocol;

import main.java.com.meelody.rpc.exception.ConfigurationException;
import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.net.nio.io.NioReader;
import main.java.com.meelody.rpc.net.nio.io.NioUnDelayWriter;
import main.java.com.meelody.rpc.net.nio.selector.NetSelector;
import main.java.com.meelody.rpc.util.Config;
import sun.misc.Unsafe;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public abstract class BothBsyProtocol implements Protocol{
    protected int RW_COUNT;
    protected int REC_COUNT;
    protected double RW_RATIO;

    protected final int DEF_RW_COUNT = Runtime.getRuntime().availableProcessors() / 2;
    protected final int DEF_REC_COUNT = 1;
    protected final double DEF_RW_RATIO = 1;

    public void init() throws ConfigurationException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        try {
            RW_COUNT =  Config.getRW_COUNT()!=0 ? Config.getRW_COUNT():DEF_RW_COUNT;
            REC_COUNT = DEF_REC_COUNT;
            RW_RATIO = Config.getRW_RADIO()!=0 ? Config.getRW_RADIO():DEF_RW_RATIO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConfigurationException("error configuration");
        }

        doInit();
    }


    public void prepare() throws NetException {
        ExecutorService ioExecutor = Executors.newFixedThreadPool(RW_COUNT);
        ExecutorService recExecutor = REC_COUNT == 1 ? Executors.newSingleThreadExecutor() : Executors.newFixedThreadPool(REC_COUNT);
        doPrepare(ioExecutor,recExecutor);
    }

    protected abstract void doInit() throws IOException, ConfigurationException, ClassNotFoundException, IllegalAccessException, InstantiationException;
    protected abstract void doPrepare(ExecutorService ioExecutor, ExecutorService recExecutor) throws NetException;

    protected void doStart(NetSelector selector, NioUnDelayWriter writer, NioReader reader) throws NetException {

        try {
            selector.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new NetException("Selector start error");
        }
        try {
            writer.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new NetException("Reader start error");
        }

        try {
            reader.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new NetException("Writer start error");
        }
    }
}
