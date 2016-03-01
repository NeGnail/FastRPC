package main.java.com.meelody.rpc.net.io;

import main.java.com.meelody.rpc.info.Call;

import java.io.IOException;
import java.net.InetSocketAddress;


public interface NetConnector {
    public void connect(InetSocketAddress address, boolean block, Call call) throws IOException;
}
