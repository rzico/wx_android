package com.rzico.weex.model;

import com.rzico.weex.model.info.BaseEntity;

import java.io.Serializable;

/**
 * Created by Jinlesoft on 2018/2/1.
 */

public class LivePlayer2 extends BaseEntity {

    private data data;

    public LivePlayer2.data getData() {
        return data;
    }

    public void setData(LivePlayer2.data data) {
        this.data = data;
    }

    public class data{
        private String url;
        private String video;

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

    }

}
