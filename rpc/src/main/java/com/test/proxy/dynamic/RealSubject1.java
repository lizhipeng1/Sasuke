package com.test.proxy.dynamic;


public class RealSubject1 implements Subject {
    @Override
    public String operate() {
        return "RealSubject1-operate()";
    }
}
