package com.yalantis.ucrop.model;

import java.util.List;

/**
 * Created by dd on 16/1/13.
 * 相同图片张数的不同排列实体
 */
public class Type {

    public Type(List<ImageItem> pic){
        this.pic = pic;
    }
    private List<ImageItem> pic;

    public List<ImageItem> getPic() {
        return pic;
    }

    public void setPic(List<ImageItem> pic) {
        this.pic = pic;
    }
}
