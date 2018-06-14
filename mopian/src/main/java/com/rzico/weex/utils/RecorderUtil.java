package com.rzico.weex.utils;

import android.media.MediaRecorder;
import android.util.Log;


import com.rzico.weex.WXApplication;
import com.rzico.weex.constant.AllConstant;
import com.rzico.weex.model.info.Message;
import com.taobao.weex.bridge.JSCallback;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 录音工具
 */
public class RecorderUtil {

    private static final String TAG = "RecorderUtil";

    private  String mFileName = null;
    private MediaRecorder mRecorder = null;
    private long startTime;
    private long timeInterval;
    private boolean isRecording;
    public static RecorderUtil recorderUtil;

    public static RecorderUtil getInstance() {
        if (recorderUtil == null) {
            recorderUtil = new RecorderUtil();
        }
        return recorderUtil;
    }
    public RecorderUtil(){
    }

    /**
     * 开始录音
     */
    public void startRecording(JSCallback jsCallback) {
        mFileName = AllConstant.getDiskCachePath(WXApplication.getActivity()) + "/recorderUtils_" + System.currentTimeMillis() + ".mp4";
        if (mFileName == null) {
            jsCallback.invoke(new Message().error("录音失败"));
            return;
        }
        if (isRecording){
            mRecorder.release();
            mRecorder = null;
        }
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        startTime = System.currentTimeMillis();
        try {
            mRecorder.prepare();
            mRecorder.start();
            isRecording = true;
            jsCallback.invoke(new Message().success("录音成功"));
        } catch (Exception e){
            Log.e(TAG, "prepare() failed");
            jsCallback.invoke(new Message().error("录音失败"));
        }
    }


    /**
     * 停止录音
     */
    public void stopRecording(JSCallback callback) {
        if (mFileName == null) return;
        timeInterval = System.currentTimeMillis() - startTime;
        try{
            if (timeInterval > 1000){
                mRecorder.stop();
            }
            mRecorder.release();
            mRecorder = null;
            isRecording =false;
        }catch (Exception e){
            Log.e(TAG, "release() failed");
            callback.invoke(new Message().error("录音失败"));
        }

    }


    /**
     * 获取录音文件
     */
    public byte[] getDate() {
        if (mFileName == null) return null;
        try{
            return readFile(new File(mFileName));
        }catch (IOException e){
            Log.e(TAG, "read file error" + e);
            return null;
        }
    }

    /**
     * 获取录音文件地址
     */
    public String getFilePath(){
        return mFileName;
    }


    /**
     * 获取录音时长,单位秒
     */
    public long getTimeInterval() {
        return timeInterval/1000;
    }


    /**
     * 将文件转化为byte[]
     *
     * @param file 输入文件
     */
    private static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }



}
