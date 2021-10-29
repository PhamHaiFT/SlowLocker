package com.superslow.locker.task;

import android.os.Looper;

import java.io.Serializable;
import java.util.Map;


public abstract class ExecuteTask implements Runnable, Serializable {

    public static final int EXCUTE_TASK_ERROR = -1001;
    public static final int EXCUTE_TASK_RESPONSE_JSON = 10001;
    public static final int EXCUTE_TASK_RESPONSE_OBJECT = 10002;

    protected int uniqueID;

    @SuppressWarnings("rawtypes")
    protected Map taskParam;

    protected int status;

    protected String json;

    protected Object result;

    protected String md5Id;

    private boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();

    public String getMd5Id() {
        return md5Id;
    }

    public void setMd5Id(String md5Id) {
        this.md5Id = md5Id;
    }

    public ExecuteTask() {
    }

    public int getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(int uniqueID) {
        this.uniqueID = uniqueID;
    }

    @SuppressWarnings("rawtypes")
    public Map getTaskParam() {
        return taskParam;
    }

    @SuppressWarnings("rawtypes")
    public void setTaskParam(Map taskParam) {
        this.taskParam = taskParam;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }


    public boolean isMainThread() {
        /*return Looper.myLooper() == Looper.getMainLooper() ;  //this is wrong */
        return isMainThread;
    }


    @Override
    public void run() {
        doTask();
    }

    public abstract ExecuteTask doTask();
}

