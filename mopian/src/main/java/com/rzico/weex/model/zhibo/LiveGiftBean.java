package com.rzico.weex.model.zhibo;

import com.rzico.weex.model.info.BaseEntity;

import java.util.List;

/**
 * Created by Jinlesoft on 2018/4/12.
 */

public class LiveGiftBean extends BaseEntity {


    private data data;

    public data getData() {
        return data;
    }

    public void setData(data data) {
        this.data = data;
    }

    public class data {
//	"draw": 0,
//            "start": 0,
//            "length": 20,
//            "recordsTotal": 8,
//            "recordsFiltered": 8,

        private int draw;
        private int start;
        private int length;
        private int recordsTotal;
        private int recordsFiltered;
    private List<datagif> data;

    public List<datagif> getData() {
        return data;
    }

    public void setData(List<datagif> data) {
        this.data = data;
    }

        public int getDraw() {
            return draw;
        }

        public void setDraw(int draw) {
            this.draw = draw;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getRecordsTotal() {
            return recordsTotal;
        }

        public void setRecordsTotal(int recordsTotal) {
            this.recordsTotal = recordsTotal;
        }

        public int getRecordsFiltered() {
            return recordsFiltered;
        }

        public void setRecordsFiltered(int recordsFiltered) {
            this.recordsFiltered = recordsFiltered;
        }

        //    礼物对象
    public class datagif {
        private Long id;
        /**
         * 名称
         */
        private String name;
        /**
         * 缩例图
         */
        private String thumbnail;
        /**
         * 特效
         */
        private String animation;
        /**
         * 价格
         */
        private Long price;

        private Boolean isPlay;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getAnimation() {
            return animation;
        }

        public void setAnimation(String animation) {
            this.animation = animation;
        }

        public Long getPrice() {
            return price;
        }

        public void setPrice(Long price) {
            this.price = price;
        }

            public Boolean getPlay() {
                return isPlay;
            }

            public void setPlay(Boolean play) {
                isPlay = play;
            }
        }
    }
}
