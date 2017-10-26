package com.rpc.monitor.service;

import java.net.MalformedURLException;

/**
 * Created by hzlizhipeng on 2017/10/25.
 */
public interface JarService {

    /**
     * 加载 jar包
     * @param url
     */
    void loadJarClass(String url) throws MalformedURLException, NoSuchMethodException;

}
