package com.rzico.weex.model.chat;

import java.io.Serializable;

/**
 * Created by Jinlesoft on 2017/11/17.
 * 聊天需要的昵称和头像
 */

public class ChatInfo implements Serializable{
    private String userNikeName;//对方的昵称
    private String myHeadImg;
    private String userHeadImg;

    public String getUserNikeName() {
        return userNikeName;
    }

    public void setUserNikeName(String userNikeName) {
        this.userNikeName = userNikeName;
    }

    public String getMyHeadImg() {
        return myHeadImg;
    }

    public void setMyHeadImg(String myHeadImg) {
        this.myHeadImg = myHeadImg;
    }

    public String getUserHeadImg() {
        return userHeadImg;
    }

    public void setUserHeadImg(String userHeadImg) {
        this.userHeadImg = userHeadImg;
    }


}
