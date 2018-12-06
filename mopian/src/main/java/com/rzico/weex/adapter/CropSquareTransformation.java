package com.rzico.weex.adapter;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

public class CropSquareTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {

            int targetWidth = source.getWidth();

            int targetHeight = source.getHeight();

            int newWidth = 720;

            int newHeight = 1280;

            if (source.getWidth() == 0 || source.getHeight() == 0) {
                return source;
            }

            if (source.getWidth() > source.getHeight()) {//横向长图
                if (source.getHeight() < newHeight && source.getWidth() <= newWidth) {
                    return source;
                } else {
                    //如果图片大小大于等于设置的高度，则按照设置的高度比例来缩放
                    double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                    int width = (int) (targetHeight * aspectRatio);
                    if (width > newWidth) { //对横向长图的宽度 进行二次限制
                        width = newWidth;
                        targetHeight = (int) (width / aspectRatio);// 根据二次限制的宽度，计算最终高度
                    }
                    if (width != 0 && targetHeight != 0) {
                        Bitmap result = Bitmap.createScaledBitmap(source, width, targetHeight, false);
                        if (result != source) {
                            // Same bitmap is returned if sizes are the same
                            source.recycle();
                        }
                        return result;
                    } else {
                        return source;
                    }
                }
            } else {//竖向长图
                //如果图片小于设置的宽度，则返回原图
                if (source.getWidth() < newWidth && source.getHeight() <= newHeight) {
                    return source;
                } else {
                    //如果图片大小大于等于设置的宽度，则按照设置的宽度比例来缩放
                    double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                    int height = (int) (targetWidth * aspectRatio);
                    if (height > newHeight) {//对横向长图的高度进行二次限制
                        height = newHeight;
                        targetWidth = (int) (height / aspectRatio);//根据二次限制的高度，计算最终宽度
                    }
                    if (height != 0 && targetWidth != 0) {
                        Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, height, false);
                        if (result != source) {
                            // Same bitmap is returned if sizes are the same
                            source.recycle();
                        }
                        return result;
                    } else {
                        return source;
                    }
                }
            }
        }

        @Override
        public String key() {
            return "desiredWidth" + " desiredHeight";
        }
    }