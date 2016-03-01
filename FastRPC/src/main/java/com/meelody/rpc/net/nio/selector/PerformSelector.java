package main.java.com.meelody.rpc.net.nio.selector;

import main.java.com.meelody.rpc.exception.NetException;

import main.java.com.meelody.rpc.net.nio.handler.NioAcceptorCompletionHandler;
import main.java.com.meelody.rpc.net.nio.io.NioAcceptor;
import main.java.com.meelody.rpc.net.nio.io.NioReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;
import java.io.IOException;
import java.nio.channels.*;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;


public class PerformSelector implements NetSelector {
    protected final Log log = LogFactory.getLog(getClass());
    private boolean running;
    private Selector selector;
    private Set<SelectionKey> selectionKeys;
    Iterator<SelectionKey> iterator;
    NioAcceptor acceptor;
    NioReader reader;
    private BlockingQueue<Task> tasks=new LinkedBlockingQueue<>();
    private NioAcceptorCompletionHandler acceptorCompletionHandler;


    public PerformSelector(NioAcceptor acceptor, NioReader reader) throws IOException {
        selector=Selector.open();
        this.acceptor = acceptor;
        this.reader = reader;
    }

    public PerformSelector(NioReader reader,NioAcceptorCompletionHandler acceptorCompletionHandler) throws IOException {
        selector=Selector.open();
        this.reader=reader;
        this.acceptorCompletionHandler=acceptorCompletionHandler;
    }

    public PerformSelector(NioAcceptorCompletionHandler nioAcceptorCompletionHandler) throws IOException {
        selector=Selector.open();
        this.acceptorCompletionHandler=nioAcceptorCompletionHandler;
    }
    public PerformSelector(NioAcceptor acceptor) throws IOException {
        selector=Selector.open();
        this.acceptor = acceptor;
    }

    public PerformSelector(NioReader reader) throws IOException {
        selector=Selector.open();
        this.reader=reader;
    }

    @Override
    public void start() throws IOException, NetException, InterruptedException, ExecutionException {
        running=true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        Task task=tasks.poll();
                        if(task!=null){
                            doRegist(task);
                        }
                        selector.selectNow();
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.warn("select error");
                    }
                    selectionKeys = selector.selectedKeys();
                    iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        if (selectionKey.isAcceptable()) {
                            ServerSocketChannel serverSocketChannel= (ServerSocketChannel) selectionKey.channel();
                            try {
                                SocketChannel socketChannel=serverSocketChannel.accept();
                                acceptorCompletionHandler.handle(socketChannel,selectionKey.attachment());
                                selectionKeys.remove(selectionKey);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (selectionKey.isConnectable()) {
                        } else if (selectionKey.isReadable()) {
                            SocketChannel socketChannel= (SocketChannel) selectionKey.channel();
                            selectionKey.cancel();
                            reader.addTask(socketChannel);
                        } else if(selectionKey.isWritable()){
                        }
                    }
                }
            }


        }).start();

    }
    private void doRegist(Task task) throws ClosedChannelException {
        task.getChannel().register(selector,task.event,task.attachment);
    }
    public void regist(ServerSocketChannel serverSocketChannel, int event, Object attachment) throws ClosedChannelException, InterruptedException {
        tasks.put(new Task(serverSocketChannel,event,attachment));
    }

    public void regist(SocketChannel socketChannel, int event, Object attachment) throws ClosedChannelException, InterruptedException {
        tasks.put(new Task(socketChannel,event,attachment));
    }

    @Override
    public void stop() throws IOException {

    }

    private class Task{
        private AbstractSelectableChannel channel;
        private int event;
        private Object attachment;

        public Task(AbstractSelectableChannel channel, int event, Object attachment) {
            this.channel = channel;
            this.event = event;
            this.attachment = attachment;
        }

        public AbstractSelectableChannel getChannel() {
            return channel;
        }

        public void setChannel(AbstractSelectableChannel channel) {
            this.channel = channel;
        }

        public int getEvent() {
            return event;
        }

        public void setEvent(int event) {
            this.event = event;
        }

        public Object getAttachment() {
            return attachment;
        }

        public void setAttachment(Object attachment) {
            this.attachment = attachment;
        }
    }
}
