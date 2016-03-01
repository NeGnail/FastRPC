package main.java.com.meelody.rpc.net.bio;

import main.java.com.meelody.rpc.info.Call;

import java.net.Socket;


public interface BioConnectorCompletionHandler {
    void handle(Socket socket,Call call);
}
