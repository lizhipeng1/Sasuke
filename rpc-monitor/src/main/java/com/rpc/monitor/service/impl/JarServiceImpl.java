package com.rpc.monitor.service.impl;

import com.rpc.monitor.service.JarService;
import com.rpc.monitor.util.ExtClasspathLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by hzlizhipeng on 2017/10/25.
 */
@Service
public class JarServiceImpl implements JarService {

    private  static final Logger logger= LoggerFactory.getLogger(JarServiceImpl.class);


    @Override
    public void loadJarClass(String jarUrl) throws MalformedURLException, NoSuchMethodException {
        try {
            ExtClasspathLoader.loadClassurl(jarUrl);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
