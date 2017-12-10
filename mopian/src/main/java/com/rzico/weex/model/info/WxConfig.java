package com.rzico.weex.model.info;

/**
 * Created by Jinlesoft on 2017/9/14.
 */

public class WxConfig {
    /*系统主色*/
    private String color;
    /*资源服务器*/
    private String resourcePath;
    /*后台服务器*/
    private String interfacePath;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getInterfacePath() {
        return interfacePath;
    }

    public void setInterfacePath(String interfacePath) {
        this.interfacePath = interfacePath;
    }
}
