package com.rzico.weex.model.info;

/**
 * Created by Jinlesoft on 2017/9/14.
 */

public class Message {
    /**
     * type
     * "success" 成功
     * "error"   失败
     *
     * content 说明
     */
    private String type;
    private Object data;
    private String content;
    private String md5;

    public void setSuccess(Object data,String content){
        this.type = "success";
        this.content = content;
        this.data = data;
    }
    public void setSuccess(Object data){
        setSuccess(data, "success");
    }
    public Message success(Object data,String content){
        this.type = "success";
        this.content = content;
        this.data = data;
        return this;
    }
    public Message success(Object data){
        return success(data, "success");
    }
    public void setError(){
        setError("error");
    }
    public void setError(String content){
        this.type = "error";
        this.content = content;
        this.data = "";
    }
    public Message error(){
        return error("error");
    }
    public Message error(String content){
        this.type = "error";
        this.content = content;
        this.data = "";
        return this;
    }



    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
