package com.rzico.weex.utils.task;

/**
 * Created by Jinlesoft on 2017/9/26.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 *  解压
 */
public class ZipExtractorTask extends AsyncTask<Void, Integer, Long> {
    private final String TAG = "ZipExtractorTask";
    private final File mInput;
    private final File mOutput;
    //  private final ProgressDialog mDialog;
    private int mProgress = 0;
    private final Context mContext;
    private boolean mReplaceAll;
    public static final int ZIPSUCCESS = 1;
    public static final int ZIPERROR = 0;
    private Handler mHandler;
    WebView mWebView;

    public ZipExtractorTask(String in, String out, Context context, boolean replaceAll, Handler handler){
        super();
        mInput = new File(in);
        mOutput = new File(out);
        this.mHandler = handler;
        if(!mOutput.exists()){
            if(!mOutput.mkdirs()){
                Log.e(TAG, "Failed to make directories:"+mOutput.getAbsolutePath());
            }
        }

        mContext = context;
        mReplaceAll = replaceAll;
    }
    @Override
    protected Long doInBackground(Void... params) {
        // TODO Auto-generated method stub
        return unzip();
    }

    @Override
    protected void onPostExecute(Long result) {
        // TODO Auto-generated method stub
        //super.onPostExecute(result);

        if(isCancelled())
            return;
        //这里表示解压完成  可以进行显示WebView 发送广播 并更新保存的 时间
//        Intent intent = new Intent();
//        intent.setAction("com.sl.unzip");
//        mContext.sendBroadcast(intent);
        mHandler.sendEmptyMessage(ZIPSUCCESS);
    }
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        //super.onPreExecute();
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        // TODO Auto-generated method stub
    }
    private long unzip(){
        long extractedSize = 0L;
        Enumeration<ZipEntry> entries;
        ZipFile zip = null;
        try {
            zip = new ZipFile(mInput);
            long uncompressedSize = getOriginalSize(zip);
            publishProgress(0, (int) uncompressedSize);

            entries = (Enumeration<ZipEntry>) zip.entries();
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(entry.isDirectory()){
                    continue;
                }
                File destination = new File(mOutput, entry.getName());
                if(!destination.getParentFile().exists()){
                    Log.e(TAG, "make="+destination.getParentFile().getAbsolutePath());
                    destination.getParentFile().mkdirs();
                }
                if(destination.exists()&&mContext!=null&&!mReplaceAll){

                }
                ProgressReportingOutputStream outStream = new ProgressReportingOutputStream(destination);
                extractedSize+=copy(zip.getInputStream(entry),outStream);
                outStream.close();
            }
        } catch (ZipException e) {
            // TODO Auto-generated catch block
            mHandler.sendEmptyMessage(ZIPERROR);
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            mHandler.sendEmptyMessage(ZIPERROR);
            e.printStackTrace();
        }finally{
            try {
                if(zip != null){
                    zip.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return extractedSize;
    }

    private long getOriginalSize(ZipFile file){
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) file.entries();
        long originalSize = 0l;
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            if(entry.getSize()>=0){
                originalSize+=entry.getSize();
            }
        }
        return originalSize;
    }

    private int copy(InputStream input, OutputStream output){
        byte[] buffer = new byte[1024*8];
        BufferedInputStream in = new BufferedInputStream(input, 1024*8);
        BufferedOutputStream out  = new BufferedOutputStream(output, 1024*8);
        int count =0,n=0;
        try {
            while((n=in.read(buffer, 0, 1024*8))!=-1){
                out.write(buffer, 0, n);
                count+=n;
            }
            out.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                out.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return count;
    }

    private final class ProgressReportingOutputStream extends FileOutputStream {

        public ProgressReportingOutputStream(File file)
                throws FileNotFoundException {
            super(file);
            // TODO Auto-generated constructor stub
        }
        @Override
        public void write(byte[] buffer, int byteOffset, int byteCount)
                throws IOException {
            // TODO Auto-generated method stub
            super.write(buffer, byteOffset, byteCount);
            mProgress += byteCount;
            publishProgress(mProgress);
        }

    }
}