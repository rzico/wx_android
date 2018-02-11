package com.rzico.weex.model;

import java.io.Serializable;

/**
 * Created by Jinlesoft on 2018/2/1.
 */

public class LivePlayerBean implements Serializable {

    private String url;
    private String video;
    private String method;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
