package main.java.com.meelody.rpc.exception;


public class NetException extends Exception{
    public NetException() {
        super();
    }

    public NetException(String message) {
        super(message);
    }

    public NetException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetException(Throwable cause) {
        super(cause);
    }
}
