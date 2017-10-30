package com.rpc.monitor.util;

import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * SSH工具类
 *
 */
public class SSHUtil {

    private  static final Logger logger= LoggerFactory.getLogger(SSHUtil.class);

    private static  Session getShellConnection(String host, String user, String psw, int port ,String keyFile) throws Exception {
        JSch jsch = new JSch();
        jsch.addIdentity(keyFile);
        // 采用指定的端口连接服务器
        Session session = jsch.getSession(user, host, port);
        // 如果服务器连接不上，则抛出异常
        if (session == null) {
            throw new Exception("session is null");
        }
        // 设置登陆主机的密码
        session.setPassword(psw);// 设置密码
        // 设置第一次登陆的时候提示，可选值：(ask | yes | no)
        session.setConfig("StrictHostKeyChecking", "no");
        // 设置登陆超时时间
        session.connect(30000);
        return  session;
    }

    /**
     * 远程 执行命令并返回结果调用过程 是同步的（执行完才会返回）
     *
     * @param host 主机名
     * @param user 用户名
     * @param psw  密码
     * @param port 端口
     * @param shellCommand 命令
     * @return
     */
    public static void sshShell( String host, String user, String psw, int port , String shellCommand ,String keyFile) {

        Session session = null;
        Channel channel = null;
        BufferedReader input=null;
        try {
            session = getShellConnection(host , user , psw ,port ,keyFile);
            // 创建sftp通信通道shell
            channel =   session.openChannel("exec");

            ((ChannelExec) channel).setCommand(shellCommand);
            input = new BufferedReader(new InputStreamReader(channel
                    .getInputStream()));
            channel.connect();

            String result = IOUtils.toString( input );

            System.out.println(result);

            String[] st = result.split("\\n");
            System.out.println(JSONObject.toJSONString(st));


        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if(input!=null){
                    input.close();
                }
            } catch (IOException e) {
                logger.error("io is close fail:"+e.getMessage());
            }
        }
        session.disconnect();
        channel.disconnect();
    }


    public static void main(String[] args) {
        SSHUtil.sshShell("192.168.130.79","peng","",8210,"ps -aux | grep workflow" ,"C:\\Users\\hzlizhipeng\\.ssh\\id_rsa");
    }

}

