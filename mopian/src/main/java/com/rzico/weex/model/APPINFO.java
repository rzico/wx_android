package com.rzico.weex.model;

import java.util.List;

/**
 * Created by Jinlesoft on 2018/11/5.
 * APP基本信息配置类
 */

public class APPINFO {

//    public static enum  Style{
//        both,/*导航有图也有文字*/
//        image,/*只有图片*/
//        text/*只有文字*/
//    }

    private String color;
    private String selectedColor;
    private String borderStyle;
    private String backgroundColor;
    private String style;
    private String index;
    private int dot;

    private List<TabBar> tabBar;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }

    public String getBorderStyle() {
        return borderStyle;
    }

    public void setBorderStyle(String borderStyle) {
        this.borderStyle = borderStyle;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getDot() {
        return dot;
    }

    public void setDot(int dot) {
        this.dot = dot;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public List<TabBar> getTabBar() {
        return tabBar;
    }

    public void setTabBar(List<TabBar> tabBar) {
        this.tabBar = tabBar;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}
