package main.java.com.meelody.rpc.net.nio.io;

import main.java.com.meelody.rpc.info.Call;

import java.net.InetSocketAddress;


public class Connection {
    private InetSocketAddress address;
    private Call call;

    public Connection(InetSocketAddress address, Call call) {
        this.address = address;
        this.call = call;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }
}
