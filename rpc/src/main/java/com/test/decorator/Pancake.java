package com.test.decorator;

import java.math.BigDecimal;

public abstract class Pancake {
    protected String name;
    public String getName()
    {
        return this.name;
    }
    public abstract BigDecimal getPrice();
}