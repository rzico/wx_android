package com.rzico.weex.model.info;

/**
 * Created by Administrator on 2017/3/29 0029.
 */

public class BaseEntity {
//    private message message;
//
//    public BaseEntity.message getMessage() {
//        return message;
//    }
//
//    public void setMessage(BaseEntity.message message) {
//        this.message = message;
//    }
//
//    public class message {
//        private String type;
//        private String content;
//
//        public String getType() {
//            return type;
//        }
//
//        public void setType(String type) {
//            this.type = type;
//        }
//        public String getContent() {
//            return content;
//        }
//
//        public void setContent(String content) {
//            this.content = content;
//        }
//
//
//    }

    private String type;
    private String content;
    private String md5;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
