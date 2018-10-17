package com.test.proxy.dynamic;

public class RealSubject2 implements Subject{

    @Override
    public String operate() {
        return "RealSubject2-operate()";
    }
}
