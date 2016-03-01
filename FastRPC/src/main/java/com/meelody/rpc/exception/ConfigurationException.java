package main.java.com.meelody.rpc.exception;


public class ConfigurationException extends Exception{
    public ConfigurationException(Throwable cause) {
        super(cause);
    }

    public ConfigurationException() {
        super();
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
