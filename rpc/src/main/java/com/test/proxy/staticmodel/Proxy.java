package com.test.proxy.staticmodel;

public class Proxy implements Subject {
    private Subject subject = null;

    @Override
    public void operate(){
        if(subject == null)
            subject = new RealSubject();
        System.out.print("I'm Proxy, I'm invoking...");
        this.subject.operate();
    }

    public static void main(String[] args) {
        Subject subject = new Proxy();
        subject.operate();
    }

}