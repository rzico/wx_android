package com.rzico.weex.model.zhibo;

import com.rzico.weex.model.info.BaseEntity;

/**
 * Created by Jinlesoft on 2018/4/18.
 */

public class NoticeBean extends BaseEntity {


    private data data;

    public NoticeBean.data getData() {
        return data;
    }

    public void setData(NoticeBean.data data) {
        this.data = data;
    }

    public class data{

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
}
