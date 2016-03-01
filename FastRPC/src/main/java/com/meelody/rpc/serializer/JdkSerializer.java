package main.java.com.meelody.rpc.serializer;

import java.io.*;


public class JdkSerializer implements Serializer{
    @Override
    public byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream=new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(data);
        ObjectInputStream objectInputStream=new ObjectInputStream(byteArrayInputStream);
        return objectInputStream.readObject();
    }
}
