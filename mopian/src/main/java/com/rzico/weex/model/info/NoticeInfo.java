package com.rzico.weex.model.info;

import java.io.Serializable;

/**
 * Created by Jinlesoft on 2018/4/28.
 * 这个是直播公告的数据
 */

public class NoticeInfo  implements Serializable {
    private String type;
    private String title;
    private String url;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
