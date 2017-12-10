package com.rzico.weex.db.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Jinlesoft on 2017/9/19.
 */

@Table(name="redis")
public class Redis {

    @Column(name="id",isId=true,autoGen=true)
    private int id;

    @Column(name="userId")
    private long userId;

    //类型
    @Column(name="type")
    private String type;

    //键
    @Column(name="key")
    private String key;


    //值 json
    @Column(name="value")
    private String value;


    //排序
    @Column(name="sort")
    private String sort;

    //搜索字段
    @Column(name="keyword")
    private String keyword;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
