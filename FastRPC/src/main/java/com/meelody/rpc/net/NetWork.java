package main.java.com.meelody.rpc.net;

import main.java.com.meelody.rpc.exception.NetException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public interface NetWork {
     void start() throws IOException, NetException, InterruptedException, ExecutionException;
     void stop() throws IOException;
}
