package com.rzico.weex.model.info;

/**
 * Created by Jinlesoft on 2017/9/25.
 */

public class Launch extends BaseEntity {


    private data data;

    public Launch.data getData() {
        return data;
    }

    public void setData(Launch.data data) {
        this.data = data;
    }

        public class data{
        private String resVersion;
        private String resUrl;
        private String appVersion;
        private String minVersion;
        private String appUrl;
        private String key;

        public String getResVersion() {
            return resVersion;
        }

        public void setResVersion(String resVersion) {
            this.resVersion = resVersion;
        }

        public String getResUrl() {
            return resUrl;
        }

        public void setResUrl(String resUrl) {
            this.resUrl = resUrl;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }

        public String getAppUrl() {
            return appUrl;
        }

        public void setAppUrl(String appUrl) {
            this.appUrl = appUrl;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

            public String getMinVersion() {
                return minVersion;
            }

            public void setMinVersion(String minVersion) {
                this.minVersion = minVersion;
            }
        }

}
