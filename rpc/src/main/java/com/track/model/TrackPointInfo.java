package com.track.model;

import java.io.Serializable;

/**
 * Created by hzlizhipeng on 2017/10/27.
 */
public class TrackPointInfo implements Serializable {
    private String param;
    private String method;
    private String response;
    private String exception;
    private String  clazz;
    private String userInfo;
    private Long runTime;

    public Long getRunTime() {
        return runTime;
    }

    public TrackPointInfo setRunTime(Long runTime) {
        this.runTime = runTime;
        return this;
    }

    public String getParam() {
        return param;
    }

    public TrackPointInfo setParam(String param) {
        this.param = param;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public TrackPointInfo setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getResponse() {
        return response;
    }

    public TrackPointInfo setResponse(String response) {
        this.response = response;
        return this;
    }

    public String getException() {
        return exception;
    }

    public TrackPointInfo setException(String exception) {
        this.exception = exception;
        return this;
    }

    public String getClazz() {
        return clazz;
    }

    public TrackPointInfo setClazz(String clazz) {
        this.clazz = clazz;
        return this;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public TrackPointInfo setUserInfo(String userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    public TrackPointInfo() {
    }

    public TrackPointInfo(String param, String method, String response, String exception, String clazz, String userInfo, Long runTime) {
        this.param = param;
        this.method = method;
        this.response = response;
        this.exception = exception;
        this.clazz = clazz;
        this.userInfo = userInfo;
        this.runTime = runTime;
    }
}
