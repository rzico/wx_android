package com.rzico.weex.model.zhibo;

import com.rzico.weex.model.info.BaseEntity;

public class GameBean extends BaseEntity {

    private data data;

    public data getData() {
        return data;
    }

    public void setData(data data) {
        this.data = data;
    }

    public class data {
        private String video;
        private String url;

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
