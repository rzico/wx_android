package com.rzico.weex.db.notidmanager;

import com.taobao.weex.bridge.JSCallback;

/**
 * Created by Jinlesoft on 2017/12/3.
 * 用来保存请求数据库缓存
 */


public class DbCacheBean {
    //处理类型
    public static enum Type{
        SAVE,
        FIND,
        FINDLIST,
        DELETE,
        DELETEALL
    }

    private Type doType;
    private String type;
    private String key;
    private String value;
    private String sort;
    private String keyword;
    private String orderBy;
    private int current;
    private int pageSize;
    private JSCallback jsCallback;

    public Type getDoType() {
        return doType;
    }

    public void setDoType(Type doType) {
        this.doType = doType;
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

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public JSCallback getJsCallback() {
        return jsCallback;
    }

    public void setJsCallback(JSCallback jsCallback) {
        this.jsCallback = jsCallback;
    }
}
