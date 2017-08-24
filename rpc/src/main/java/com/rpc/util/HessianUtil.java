package com.rpc.util;

import com.caucho.hessian.client.HessianProxyFactory;

public class HessianUtil {

    public static HessianProxyFactory factory = new HessianProxyFactory();

    static {
        factory.setOverloadEnabled(true);
    }

}
