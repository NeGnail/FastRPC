package main.java.com.meelody.rpc.net.bio;

import main.java.com.meelody.rpc.info.Call;

import java.io.IOException;
import java.net.Socket;

public interface BioReaderCompletionHandler{
    void handle(Call call,Socket socket) throws IOException;
}
