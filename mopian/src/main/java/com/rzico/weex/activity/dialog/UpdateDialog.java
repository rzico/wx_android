package com.rzico.weex.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rzico.weex.R;
import com.rzico.weex.model.info.Launch;
import com.rzico.weex.utils.FileSizeUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class UpdateDialog extends Dialog {
    private Context context;
    private Launch.data res;
    CloseListener listener;

    public UpdateDialog(Context context, Launch.data res, CloseListener listener) {
        super(context, R.style.Dialog);
        this.context = context;
        this.res = res;
        this.listener = listener;
    }
    public interface CloseListener {
        void close();
    }
    private TextView info;
    private TextView size;
    private TextView update;
    private TextView version;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update);
        init();
        setWidows();
        downLoadApk(res.getAppUrl());
        bar.setVisibility(View.VISIBLE);
        update.setVisibility(View.GONE);
    }

    private void init() {
        info = (TextView) findViewById(R.id.info);
        info.setText("有新的版本更新");
        update = (TextView) findViewById(R.id.update);
        version = (TextView) findViewById(R.id.version);
        version.setText("版本号：" + res.getAppVersion());
        size = (TextView) findViewById(R.id.size);
        bar = (ProgressBar) findViewById(R.id.bar);
        update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isShowing()) {

                }
            }
        });

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                listener.close();
            }
        });
    }


    @SuppressWarnings("deprecation")
    private void setWidows() {
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (display.getWidth() * 4 / 5); //
        getWindow().setAttributes(lp);
        this.setCanceledOnTouchOutside(false);
        // 设置点击Dialog外部任意区域关闭Dialog
    }

    /*
   * 从服务器中下载APK
   */
    protected void downLoadApk(final String URL) {
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = getFileFromServer(URL, bar);
                    if(file!=null){
//                        size.setText("版本大小："+ FileSizeUtil.FormetFileSize(FileSizeUtil.getFileSize(file)));
                    }
                    sleep(1000);
                    installApk(file);
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }


    public static File getFileFromServer(String path, ProgressBar pd)
            throws Exception {
        // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept-Encoding", "identity"); //加上这句话解决问题
            conn.setConnectTimeout(5000);
            conn.connect();
            // 获取到文件的大小
            pd.setMax(conn.getContentLength() / 1024);
            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory(), "helper-release.apk");//http://rzico.oss-cn-shenzhen.aliyuncs.com/update/helper/helper-release.apk
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                // 获取当前下载量
                pd.setProgress(total / 1024);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }

    // 安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 执行动作
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 执行的数据类型
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

}
