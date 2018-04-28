package com.rzico.weex.model.info;

import java.util.List;

/**
 * Created by Jinlesoft on 2018/4/28.
 * 用于接受page数据
 *
 */

public class BasePage extends BaseEntity {

    private data data;

    public BasePage.data getData() {
        return data;
    }

    public void setData(BasePage.data data) {
        this.data = data;
    }

    public class data{
//        "draw": 0,
//        "start": 0,
//        "length": 20,
//        "recordsTotal": 0,
//        "recordsFiltered": 0,
//        "data": []
        private Long draw;
        private Long start;
        private Long length;
        private Long recordsTotal;
        private Long recordsFiltered;

        private List<NoticeInfo> data;

        public Long getDraw() {
            return draw;
        }

        public void setDraw(Long draw) {
            this.draw = draw;
        }

        public Long getStart() {
            return start;
        }

        public void setStart(Long start) {
            this.start = start;
        }

        public Long getLength() {
            return length;
        }

        public void setLength(Long length) {
            this.length = length;
        }

        public Long getRecordsTotal() {
            return recordsTotal;
        }

        public void setRecordsTotal(Long recordsTotal) {
            this.recordsTotal = recordsTotal;
        }

        public Long getRecordsFiltered() {
            return recordsFiltered;
        }

        public void setRecordsFiltered(Long recordsFiltered) {
            this.recordsFiltered = recordsFiltered;
        }

        public List<NoticeInfo> getData() {
            return data;
        }

        public void setData(List<NoticeInfo> data) {
            this.data = data;
        }
    }
}
