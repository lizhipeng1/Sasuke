package com.test.decorator.detail;


import com.test.decorator.Condiment;
import com.test.decorator.Pancake;

import java.math.BigDecimal;

public class Egg extends Condiment {
    private Pancake pancake;

    public Egg(Pancake pancake)
    {
        this.pancake = pancake;
    }

    @Override
    public String getName()
    {
        return pancake.getName()+",加鸡蛋";
    }

    @Override
    public BigDecimal getPrice()
    {
        return pancake.getPrice().add(new BigDecimal(1.5));
    }
}