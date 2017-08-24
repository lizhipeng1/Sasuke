package com.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by hzlizhipeng on 2017/8/19.
 */
@Component
@WebListener
public class ServletListener implements ServletContextListener {

    @Autowired
    private ThreadExecutor threadExecutor;

    public void contextInitialized(ServletContextEvent servletContextEvent) {
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        threadExecutor.shutdown();
    }
}
