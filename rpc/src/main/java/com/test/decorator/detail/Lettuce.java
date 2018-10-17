package com.test.decorator.detail;


import com.test.decorator.Condiment;
import com.test.decorator.Pancake;

import java.math.BigDecimal;

public class Lettuce extends Condiment {
    private Pancake pancake;
    public Lettuce(Pancake pancake)
    {
        this.pancake = pancake;
    }
    @Override
    public String getName()
    {
        return this.pancake.getName()+",加生菜";
    }

    @Override
    public BigDecimal getPrice()
    {
        return pancake.getPrice().add(new BigDecimal(1));
    }
}