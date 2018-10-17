package com.test.decorator;

public abstract class Condiment extends Pancake {
    public abstract String getName();

    public void sold()
    {
        System.out.println(getName()+"："+getPrice());
    }
}