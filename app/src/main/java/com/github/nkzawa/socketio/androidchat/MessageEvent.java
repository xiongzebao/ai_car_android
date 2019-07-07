package com.github.nkzawa.socketio.androidchat;

import com.blankj.utilcode.util.CacheDiskStaticUtils;
import com.blankj.utilcode.util.GsonUtils;

import java.io.Serializable;





public class MessageEvent implements Serializable {

    private String role= CacheDiskStaticUtils.getString(Constants.SP_USERNAME);
    private boolean success=true;
    private String code;
    private String msg;
    private String action;
    private String recv="pi";

    public MessageEvent( String action) {

        this.action = action;
    }

    public MessageEvent(String msg, String action) {
        this.msg = msg;
        this.action = action;
    }



    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String toJson(){
       return GsonUtils.toJson(this);
    }

}
