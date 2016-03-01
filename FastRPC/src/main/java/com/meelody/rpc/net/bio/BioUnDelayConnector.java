package main.java.com.meelody.rpc.net.bio;

import main.java.com.meelody.rpc.net.io.NetConnector;
import main.java.com.meelody.rpc.info.Call;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


public class BioUnDelayConnector implements NetConnector{
    private BioConnectorCompletionHandler handler;

    public BioUnDelayConnector(BioConnectorCompletionHandler handler) {
        this.handler = handler;
    }

    @Override
    public void connect(InetSocketAddress address, boolean block, Call call) throws IOException {
        Socket socket=new Socket();
        if(!block){
            socket.connect(address,2000);
        }else{
            socket.connect(address);

        }
        handler.handle(socket,call);
    }

}
