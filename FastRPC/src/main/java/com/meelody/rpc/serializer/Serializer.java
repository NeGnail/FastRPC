package main.java.com.meelody.rpc.serializer;

import java.io.IOException;


public interface Serializer {
    byte[] serialize(Object object) throws IOException;
    Object deserialize(byte[] data) throws IOException, ClassNotFoundException;
}
