package com.test.decorator.detail;


import com.test.decorator.Condiment;
import com.test.decorator.Pancake;

import java.math.BigDecimal;

public class Ham extends Condiment {
    private Pancake pancake;
    public Ham(Pancake pancake)
    {
        this.pancake = pancake;
    }
    @Override
    public String getName()
    {
        return this.pancake.getName()+",加火腿";
    }

    @Override
    public BigDecimal getPrice()
    {
        return pancake.getPrice().add(new BigDecimal(2));
    }
}