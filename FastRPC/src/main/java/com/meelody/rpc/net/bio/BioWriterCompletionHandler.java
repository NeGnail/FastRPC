package main.java.com.meelody.rpc.net.bio;

import java.net.Socket;
import java.nio.channels.SocketChannel;


public interface BioWriterCompletionHandler {
    void handle(Socket socket);
}
