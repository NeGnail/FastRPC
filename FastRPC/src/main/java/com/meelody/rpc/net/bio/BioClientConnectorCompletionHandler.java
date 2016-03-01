package main.java.com.meelody.rpc.net.bio;

import main.java.com.meelody.rpc.info.Call;

import java.net.Socket;


public class BioClientConnectorCompletionHandler implements BioConnectorCompletionHandler{
    private BioWriter writer;

    public BioClientConnectorCompletionHandler(BioWriter writer) {
        this.writer = writer;
    }

    @Override
    public void handle(Socket socket, Call call) {
        writer.addTask(new BioWrition(socket,call));
    }
}
