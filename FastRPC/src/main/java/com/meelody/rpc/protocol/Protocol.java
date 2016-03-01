package main.java.com.meelody.rpc.protocol;

import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.info.Call;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;


public interface Protocol {
    public void publish(int port) throws NetException;
    public void call(Call call, InetSocketAddress address) throws NetException, IOException, InterruptedException, ExecutionException, ClassNotFoundException;

}
