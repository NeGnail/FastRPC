package main.java.com.meelody.rpc.client;

import main.java.com.meelody.rpc.info.Execution;

import java.io.IOException;
import java.net.InetSocketAddress;


public interface LoadBalance {
    InetSocketAddress findServiceAddress(Execution execution) throws IOException;
}
