package com.rzico.weex.model.event;

import java.util.Map;

/**
 * Created by Jinlesoft on 2018/3/22.
 */

public class MessageBus {

    public enum Type{
        GLOBAL,//全局事件
        LOGINSUCCESS,//登录成功
        LOGOUT,//注销登录
        FORCEOFFLINE,//登录账号被顶登录了
        RECEIVEMSG,//刷新未读数
        LOGINERROR,//登录失败
        ZHIBOCHAT,//直播消息
        ZHIBOCLOSE,//直播房间被关闭
        SENDGAG,//禁言发送消息
        SENDKICK,//踢人发送消息
        LIVEPLAYGAME//直播间开始游戏
    }

    //向weex推送全局消息
    public MessageBus(Type type, String eventKey, Map<String, Object> params){
        this.messageType = type;
        this.eventKey = eventKey;
        this.params = params;
    }
    public MessageBus(Type type, Object message){
        this.messageType = type;
        this.message=message;
    }
    public MessageBus(Type type){
        this.message = null;
        this.messageType = type;
    }


    private Object message;
    private Type messageType;

    private Map<String, Object> params;//这个是 向weex全局推送的参数

    private String eventKey;//这个是 向weex全局推送的 key

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }


    public Type getMessageType() {
        return messageType;
    }

    public void setMessageType(Type messageType) {
        this.messageType = messageType;
    }

}
