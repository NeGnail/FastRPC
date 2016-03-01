package main.java.com.meelody.rpc.net.bio;

import java.net.Socket;


public class BioClientWriterCompletionHandler implements BioWriterCompletionHandler{
    private BioReader reader;

    public BioClientWriterCompletionHandler(BioReader reader) {
        this.reader = reader;
    }

    @Override
    public void handle(Socket socket) {
        reader.addTask(socket);
    }
}
