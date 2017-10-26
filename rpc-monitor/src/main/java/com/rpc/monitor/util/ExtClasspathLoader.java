package com.rpc.monitor.util;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public final class ExtClasspathLoader {

    private static Method addURL = initAddMethod();

    private static URLClassLoader classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();

    /**
     * 初始化addUrl 方法.
     * @return 可访问addUrl方法的Method对象
     */
    private static Method initAddMethod() {
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
            add.setAccessible(true);
            return add;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 加载jar classpath。
     */
    public static void loadClasspath() {
        List<String> files = getJarFiles();
        for (String f : files) {
            loadClasspath(f);
        }

        List<String> resFiles = getResFiles();

        for (String r : resFiles) {
            loadResourceDir(r);
        }
    }

    public static void loadClasspath(String filepath) {
        File file = new File(filepath);
        loopFiles(file);
    }

    public static void loadClassurl(String fileUrl) throws MalformedURLException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        URL url = new URL(fileUrl);
        addURL.invoke(classloader, new Object[] { url });
        Class clazz = classloader.loadClass("com.netease.corp.it.workflow.common.service.WorkflowManageService");
        System.out.println(JSONObject.toJSONString(clazz.getFields()));
    }


    private static void loadResourceDir(String filepath) {
        File file = new File(filepath);
        loopDirs(file);
    }

    /** *//**
     * 循环遍历目录，找出所有的资源路径。
     * @param file 当前遍历文件
     */
    private static void loopDirs(File file) {
        // 资源文件只加载路径
        if (file.isDirectory()) {
            addURL(file);
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopDirs(tmp);
            }
        }
    }

    /**
     * 循环遍历目录，找出所有的jar包。
     * @param file 当前遍历文件
     */
    private static void loopFiles(File file) {
        if (file.isDirectory()) {
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopFiles(tmp);
            }
        }
        else {
            if (file.getAbsolutePath().endsWith(".jar") || file.getAbsolutePath().endsWith(".zip")) {
                addURL(file);
            }
        }
    }

    /**
     * 通过filepath加载文件到classpath。
     * @return URL
     * @throws Exception 异常
     */
    private static void addURL(File file) {
        try {
            addURL.invoke(classloader, new Object[] { file.toURI().toURL() });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }



    /***
     * 从配置文件中得到配置的需要加载到classpath里的路径集合。
     * @return
     */
    private static List<String> getJarFiles() {
        // TODO 从properties文件中读取配置信息  如果不想配置 可以自己new 一个List<String> 然后把 jar的路径加进去 然后返回
        return null;
    }

    /**
     * 从配置文件中得到配置的需要加载classpath里的资源路径集合
     * @return
     */
    private static List<String> getResFiles() {
        //TODO 从properties文件中读取配置信息略  如果不想配置 可以自己new 一个List<String> 然后把 jar的路径加进去 然后返回  额 如果没有资源路径为空就可以了
        return null;
    }

    public static void main(String[] args) {
        ExtClasspathLoader.loadClasspath();
    }
}