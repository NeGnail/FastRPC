package main.java.com.meelody.rpc;

import main.java.com.meelody.rpc.exception.NetException;
import main.java.com.meelody.rpc.protocol.Protocol;
import main.java.com.meelody.rpc.util.Config;


public class RpcServer {
    private static Protocol protocol;
    public static void startServer(){
        Protocol protocol= null;
        try {
            protocol = (Protocol) Class.forName(Config.getPROTOCOL()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void publish(int port){
        try {
            protocol.publish(port);
        } catch (NetException e) {
            e.printStackTrace();
        }
    }
}
