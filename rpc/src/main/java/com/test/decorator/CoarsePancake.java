package com.test.decorator;


import com.test.decorator.detail.Egg;
import com.test.decorator.detail.Ham;
import com.test.decorator.detail.Lettuce;

import java.math.BigDecimal;

public class CoarsePancake extends Pancake
{
    public CoarsePancake(){
        this.name = "杂粮煎饼";
    }

    @Override
    public BigDecimal getPrice()
    {
        return new BigDecimal(5);
    }

    public static void main(String[] args) {
        Pancake pancake = new CoarsePancake();
        Condiment egg = new Egg(pancake);
        Condiment ham = new Ham(egg);
        ham.sold();
        Condiment lettuce = new Lettuce(ham);
        lettuce.sold();
    }

}