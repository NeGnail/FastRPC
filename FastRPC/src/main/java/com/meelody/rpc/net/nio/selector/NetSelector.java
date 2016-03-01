package main.java.com.meelody.rpc.net.nio.selector;

import main.java.com.meelody.rpc.net.NetWork;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


public interface NetSelector extends NetWork{
    public void regist(ServerSocketChannel serverSocketChannel, int event, Object attachment) throws ClosedChannelException, InterruptedException;

    public void regist(SocketChannel socketChannel, int event, Object attachment) throws ClosedChannelException, InterruptedException;
}
