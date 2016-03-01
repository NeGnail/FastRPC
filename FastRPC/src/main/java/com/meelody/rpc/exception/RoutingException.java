package main.java.com.meelody.rpc.exception;


public class RoutingException extends Exception {
    public RoutingException() {
        super();
    }

    public RoutingException(String message) {
        super(message);
    }

    public RoutingException(String message, Throwable cause) {
        super(message, cause);
    }

    public RoutingException(Throwable cause) {
        super(cause);
    }
}
