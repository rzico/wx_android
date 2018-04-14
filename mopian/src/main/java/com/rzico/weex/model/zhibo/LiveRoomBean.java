package com.rzico.weex.model.zhibo;

import com.rzico.weex.model.info.BaseEntity;
import com.rzico.weex.model.info.Launch;

/**
 * Created by Jinlesoft on 2018/4/9.
 */

public class LiveRoomBean extends BaseEntity {


    private data data;

    public data getData() {
        return data;
    }

    public void setData(data data) {
        this.data = data;
    }

    public class data {
        private Long id;

        private Long liveId;


        private Long liveMemberId;
        /**
         * 标题
         */
        private String title;
        /**
         * 昵称
         */
        private String nickname;
        /**
         * 封面
         */
        private String frontcover;
        /**
         * 头像
         */
        private String headpic;
        /**
         * 位置
         */
        private String location;
        /**
         * 推流地址
         */
        private String pushUrl;
        /**
         * 观看地址
         */
        private String playUrl;
        /**
         * 状态 0:上线 1:下线
         */
        private String online;
        /**
         * 状态
         */
        private String status;
        /**
         * 回放地址
         */
        private String hlsPlayUrl;
        /**
         * 在线数
         */
        private Long viewerCount;
        /**
         * 点赞数
         */
        private Long likeCount;
        /**
         * 礼物数
         */
        private Long gift;

        /** 是否关注 */
        private Boolean follow;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getLiveId() {
            return liveId;
        }

        public void setLiveId(Long liveId) {
            this.liveId = liveId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getFrontcover() {
            return frontcover;
        }

        public void setFrontcover(String frontcover) {
            this.frontcover = frontcover;
        }

        public String getHeadpic() {
            return headpic;
        }

        public void setHeadpic(String headpic) {
            this.headpic = headpic;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getPushUrl() {
            return pushUrl;
        }

        public void setPushUrl(String pushUrl) {
            this.pushUrl = pushUrl;
        }

        public String getPlayUrl() {
            return playUrl;
        }

        public void setPlayUrl(String playUrl) {
            this.playUrl = playUrl;
        }

        public String getOnline() {
            return online;
        }

        public void setOnline(String online) {
            this.online = online;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getHlsPlayUrl() {
            return hlsPlayUrl;
        }

        public void setHlsPlayUrl(String hlsPlayUrl) {
            this.hlsPlayUrl = hlsPlayUrl;
        }

        public Long getViewerCount() {
            return viewerCount;
        }

        public void setViewerCount(Long viewerCount) {
            this.viewerCount = viewerCount;
        }

        public Long getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(Long likeCount) {
            this.likeCount = likeCount;
        }

        public Long getGift() {
            return gift;
        }

        public void setGift(Long gift) {
            this.gift = gift;
        }

        public Boolean getFollow() {
            return follow;
        }

        public void setFollow(Boolean follow) {
            this.follow = follow;
        }

        public Long getLiveMemberId() {
            return liveMemberId;
        }

        public void setLiveMemberId(Long liveMemberId) {
            this.liveMemberId = liveMemberId;
        }
    }
}
