package main.java.com.meelody.rpc.util;

import main.java.com.meelody.rpc.rpc.RpcContext;
import main.java.com.meelody.rpc.serializer.JdkSerializer;
import main.java.com.meelody.rpc.serializer.Serializer;
import main.java.com.meelody.rpc.info.Call;
import main.java.com.meelody.rpc.info.Execution;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;


public class RpcUtil {
    private static Serializer serializer = new JdkSerializer();

    public static Call readCall(DataInputStream inputStream) throws IOException {
        Call call = new Call();
        call.setId(inputStream.readLong());
        int length = inputStream.readInt();
        byte[] data = new byte[length];
        inputStream.read(data);
        call.setLength(length);
        call.setData(data);
        return call;
    }

    public static void writeCall(Call call, DataOutputStream outputStream) throws IOException {
        outputStream.writeLong(call.getId());
        outputStream.writeInt(call.getLength());
        outputStream.write(call.getData());
    }

    public static void writeCall(Call call, SocketChannel socketChannel) throws IOException {
        int length = call.getLength();
        ByteBuffer byteBuffer = ByteBuffer.allocate(length + 12);
        byteBuffer.putLong(call.getId());
        byteBuffer.putInt(length);
        byteBuffer.put(call.getData());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
    }

    public static long createId() {

        return Long.valueOf(String.valueOf(Thread.currentThread().getId()) + String.valueOf(System.currentTimeMillis()));
    }

    public static Call readCall(SocketChannel socketChannel) throws IOException {
        Call call = new Call();
        ByteBuffer byteBuffer = ByteBuffer.allocate(12);

        int rl=socketChannel.read(byteBuffer);
        while(rl!=12){
            rl+=socketChannel.read(byteBuffer);
        }

        byteBuffer.flip();
        call.setId(byteBuffer.getLong());
        int length = byteBuffer.getInt();
        call.setLength(length);
        ByteBuffer byteBuffer2 = ByteBuffer.allocate(length);
        byte[] data = new byte[length];
        int rl2=socketChannel.read(byteBuffer2);
        while(rl2!=length){
            rl2+=socketChannel.read(byteBuffer2);
        }
        byteBuffer2.flip();
        byteBuffer2.get(data);
        call.setData(data);
        return call;
    }

    public static Call createCall(Method method, Object[] args, ConcurrentMap<String, Integer> versions, int defaultVersion) throws IOException {
        Execution excution = createExcution(method, args, versions, defaultVersion);
        return createCall(excution);
    }

    public static Call createCall(Execution excution) throws IOException {
        excution.setContext(new HashMap<String, Object>());
        byte[] data = serializer.serialize(excution);
        Call call = new Call();
        call.setData(data);
        call.setId(RpcUtil.createId());
        call.setLength(data.length);

        return call;
    }

    public static Execution createExcution(Method method, Object[] args, ConcurrentMap<String, Integer> versions, int defaultVersion) {
        Execution excution = new Execution();

        String service = method.getDeclaringClass().getName();
        String methodName = method.getName();
        excution.setService(service);
        excution.setMethod(methodName);
        excution.setArgs(args);
        Integer version = versions.get(service + "_" + methodName);
        if (version == null) {
            version = defaultVersion;
        }
        excution.setVersion(version);
        excution.setContext(RpcContext.getContent());

        return excution;
    }

    public static Object readResult(Call call) throws IOException, ClassNotFoundException {
        return serializer.deserialize(call.getData());
    }

    public static Execution readExecution(Call call) throws IOException, ClassNotFoundException {
        byte[] data = call.getData();
        return (Execution) serializer.deserialize(data);
    }

    public static byte[] getData(Object result) throws IOException {
        return serializer.serialize(result);
    }

    public static Class[] getArgsType(Object[] args) {
        Class[] argsType = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argsType[i] = args[i].getClass();
        }
        return argsType;
    }

    public static Object createObject(String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return Class.forName(name).newInstance();
    }

    public static Method findMethod(Object serviceObj, String method, Object[] args) throws NoSuchMethodException {
        return serviceObj.getClass().getMethod(method, getArgsType(args));

    }

    public static Object execute(Object serviceObj, Method methodObj, Object[] args) throws InvocationTargetException, IllegalAccessException {
        return methodObj.invoke(serviceObj, args);
    }

    public static Call update(Call call, Object result) throws IOException {
        byte[] data = getData(result);
        call.setData(data);
        call.setLength(data.length);
        return call;
    }

    public static void writeCall(Call call, Socket socket) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.writeLong(call.getId());
        outputStream.writeInt(call.getLength());
        outputStream.write(call.getData());
    }

    public static Call readCall(Socket socket) throws IOException, ClassNotFoundException {
        Call call=new Call();
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        long id = inputStream.readLong();
        int length=inputStream.readInt();
        byte[] data=new byte[length];
        inputStream.read(data);
        call.setId(id);
        call.setLength(length);
        call.setData(data);
        return call;
    }

    public static String createCachekey(Execution execution) {
        String key=null;
        String service=execution.getService();
        String method=execution.getMethod();
        String version=String.valueOf(execution.getVersion());
        key=service+method+version+Thread.currentThread().getId();
        Object[] args=execution.getArgs();
        for(int i=0;i<args.length;i++){
            key+=args[i].toString();
        }
        return key;
    }
}
