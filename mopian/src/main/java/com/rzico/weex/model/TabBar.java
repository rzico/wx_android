package com.rzico.weex.model;

/**
 * Created by Jinlesoft on 2018/11/5.
 * 首页底部导航栏配置类
 */

public class TabBar {

    private String text;
    private String path;
    //该图片一定要在drawable中存在
    private String icon;
    private String tag;
    private boolean requireAuth;
    private String selectedIconPath;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSelectedIconPath() {
        return selectedIconPath;
    }

    public void setSelectedIconPath(String selectedIconPath) {
        this.selectedIconPath = selectedIconPath;
    }

    public boolean isRequireAuth() {
        return requireAuth;
    }

    public void setRequireAuth(boolean requireAuth) {
        this.requireAuth = requireAuth;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
