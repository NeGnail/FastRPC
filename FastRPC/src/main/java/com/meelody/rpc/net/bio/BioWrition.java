package main.java.com.meelody.rpc.net.bio;

import main.java.com.meelody.rpc.info.Call;

import java.net.Socket;


public class BioWrition {
    private Socket socket;
    protected Call call;

    public BioWrition(Socket socket, Call call) {
        this.socket = socket;
        this.call = call;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }
}
