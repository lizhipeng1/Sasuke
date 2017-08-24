package com.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Environment {

    private static final Logger logger = LoggerFactory.getLogger(Environment.class);

    public static String environment = "dev";

    static {
        environment = System.getProperty("spring.profiles.active");
        logger.info("当前系统环境为：" + environment );
    }

    private Environment() {
    }

    public static String getEnvironment() {
        return environment;
    }
}