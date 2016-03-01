package main.java.com.meelody.rpc.test;


public class Test {
    public static void main(String[] args) throws NoSuchMethodException {
        System.out.println(Park.class.getMethod("say",new Class[]{}).getName());
        System.out.println(Person.class.getMethod("say",new Class[]{}).getDeclaringClass().getName());

    }
}
