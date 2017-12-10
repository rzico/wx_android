package com.rzico.weex.utils.photo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.rzico.weex.BuildConfig;
import com.rzico.weex.activity.PhotoHandleActivity;

/**
 * Created by Jinlesoft on 2017/11/7.
 * PhotoHandleActivity 帮助方法
 */

public class PhotoHandle {

    public static final int REQUEST_PHOTOHANDLER = 99;
    private static final String EXTRA_PREFIX = BuildConfig.APPLICATION_ID;

    public static final String EXTRA_INPUT_URI = EXTRA_PREFIX + ".InputUri";
    public static final String EXTRA_OUTPUT_URI = EXTRA_PREFIX + ".OutputUri";

    private Intent mPhotoHandlerIntent;
    private Bundle mPhotoHandlerBundle;

    /**
     * This method creates new Intent builder and sets both source and destination image URIs.
     *
     * @param source      Uri for image to crop
     * @param destination Uri for saving the cropped image
     */
    public static PhotoHandle of(@NonNull Uri source, @NonNull Uri destination) {
        return new PhotoHandle(source, destination);
    }

    private PhotoHandle(@NonNull Uri source, @NonNull Uri destination) {
        mPhotoHandlerIntent = new Intent();
        mPhotoHandlerBundle = new Bundle();
        mPhotoHandlerBundle.putParcelable(EXTRA_INPUT_URI, source);
        mPhotoHandlerBundle.putParcelable(EXTRA_OUTPUT_URI, destination);
    }

    /**
     * Send the crop Intent from an Activity
     *
     * @param activity Activity to receive result
     */
    public void start(@NonNull Activity activity) {
        start(activity, REQUEST_PHOTOHANDLER);
    }

    /**
     * Send the crop Intent from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public void start(@NonNull Activity activity, int requestCode) {
        activity.startActivityForResult(getIntent(activity), requestCode);
    }

    /**
     * Get Intent to start {@link PhotoHandleActivity}
     *
     * @return Intent for {@link PhotoHandleActivity}
     */
    public Intent getIntent(@NonNull Context context) {
        mPhotoHandlerIntent.setClass(context, PhotoHandleActivity.class);
        mPhotoHandlerIntent.putExtras(mPhotoHandlerBundle);
        return mPhotoHandlerIntent;
    }
}
