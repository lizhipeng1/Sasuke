package com.test.proxy.dynamic;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyHandler implements InvocationHandler {
    Object obj = null;

    public Object newProxyInstance(Object realObj){
        this.obj = realObj;
        Class<?> classType = this.obj.getClass();
        return Proxy.newProxyInstance(classType.getClassLoader(), classType.getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        System.out.print("I'm Proxy, I'm invoking...");
        Object object = method.invoke(obj, args);
        System.out.println(object);
        return object;
    }

    public static void main(String[] args) {
        Subject subject = (Subject) new ProxyHandler().newProxyInstance(new RealSubject1());
        Subject subject2 = (Subject) new ProxyHandler().newProxyInstance(new RealSubject2());
        subject.operate();
        subject2.operate();
    }
}
