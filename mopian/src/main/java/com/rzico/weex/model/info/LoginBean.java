package com.rzico.weex.model.info;

/**
 * Created by Jinlesoft on 2017/10/18.
 * 验证登录接口 接受的实体
 */

public class LoginBean {

    private data data;

    public LoginBean.data getData() {
        return data;
    }

    public void setData(LoginBean.data data) {
        this.data = data;
    }

    public class data{
        private long uid;
        private boolean loginStatus;
        private String userSig;
        private String userId;

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public boolean isLoginStatus() {
            return loginStatus;
        }

        public void setLoginStatus(boolean loginStatus) {
            this.loginStatus = loginStatus;
        }

        public String getUserSig() {
            return userSig;
        }

        public void setUserSig(String userSig) {
            this.userSig = userSig;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
