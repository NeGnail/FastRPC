package main.java.com.meelody.rpc.net.nio;

import main.java.com.meelody.rpc.info.Call;

import java.nio.channels.SocketChannel;


public class NioWrition {
    private SocketChannel socketChannel;
    private Call call;

    public NioWrition(SocketChannel socketChannel, Call call) {
        this.socketChannel = socketChannel;
        this.call = call;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }
}
