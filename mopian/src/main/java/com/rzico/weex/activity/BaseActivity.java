package com.rzico.weex.activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rzico.weex.Constant;
import com.rzico.weex.R;
import com.rzico.weex.WXApplication;
import com.rzico.weex.net.HttpRequest;
import com.rzico.weex.utils.BarTextColorUtils;
import com.rzico.weex.utils.NetUtil;
import com.rzico.weex.utils.NetWorkStateReceiver;
import com.rzico.weex.utils.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;


import java.lang.reflect.Field;


public class BaseActivity extends AppCompatActivity implements NetWorkStateReceiver.NetEvevt {
    public static final int REQUEST_CODE_LOGIN = 0xFFFF;

    public Context mContext;

    public static NetWorkStateReceiver.NetEvevt evevt;
    /**
     * 网络类型
     */
    private int netMobile;

    public int oldNetMobile;

    protected Toast toast;

    /**
     * 全透状态栏
     */
    protected void setStatusBarFullTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 半透明状态栏
     */
    protected void setHalfTransparent() {

        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 如果需要内容紧贴着StatusBar
     * 应该在对应的xml布局文件中，设置根布局fitsSystemWindows=true。
     */
    private View contentViewGroup;

    protected void setFitSystemWindow(boolean fitSystemWindow) {
        if (contentViewGroup == null) {
            contentViewGroup = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        }
        contentViewGroup.setFitsSystemWindows(fitSystemWindow);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobclickAgent.openActivityDurationTrack(false);

        // 设置为U-APP场景
        MobclickAgent.setScenarioType(mContext, MobclickAgent.EScenarioType.E_UM_NORMAL);
        mContext = BaseActivity.this;
        evevt = this;
        initView();
//        initSystemBar();

        setStatusBarFullTransparent();
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(R.color.b3a);
        }

        // SDK在统计Fragment时，需要关闭Activity自带的页面统计，
        // 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
        MobclickAgent.openActivityDurationTrack(false);

        // 设置为U-APP场景
        MobclickAgent.setScenarioType(mContext, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    /**
     * 这是更改baouu版本之前的顶部样式处理 现在要换成baouu的了
     *
     */
    public void initSystemBar(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);// 创建状态栏的管理实例
        tintManager.setStatusBarTintEnabled(true);// 激活状态栏设置
        tintManager.setNavigationBarTintEnabled(true);// 激活导航栏设置
        tintManager.setStatusBarTintColor(getResources().getColor(R.color.transparent));//设置状态栏颜色
        tintManager.setStatusBarDarkMode(false, this);//false 状态栏字体颜色是白色 true 颜色是黑色
    }

    private void initView() {
        WXApplication.getInstance().addActivity(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constant.isSetting = true;
//        HttpRequest.getInstance().unRegisterTasks(this);
    }


    /**
     * 判断是否拥有权限
     *
     * @param permission
     * @return
     */
    protected boolean hasAccessed(String permission) {
        boolean hasAccessed = ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED;

        return hasAccessed;
    }

    public void showToast() {
        showToast("", false);
    }


    public void showToast(String message) {
        showToast(message, false);
    }

    public void showToast(String message, boolean success) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_toast, (LinearLayout) findViewById(R.id.linear_toast));
        ImageView imgToast = (ImageView) layout.findViewById(R.id.img_toast);
        TextView txvToast = (TextView) layout.findViewById(R.id.txv_toast);

        String msg = "";
        if (success) {
            imgToast.setImageResource(R.mipmap.toast_success);
            msg = getResources().getString(R.string.toast_success);
        } else {
            imgToast.setImageResource(R.mipmap.toast_fail);
            msg = getResources().getString(R.string.toast_fail);
        }
        if (!TextUtils.isEmpty(message)) {
            msg = message;
        }
        txvToast.setText(msg);
        toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }


    /**
     * 跳转界面
     *
     * @param cls
     */
    protected void ChangeActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }



    public void bindOnClick(View view, int[] ids, View.OnClickListener listener) {
        for (int id : ids) {
            View v = view == null ? findViewById(id) : view.findViewById(id);
            v.setOnClickListener(listener);
        }
    }


    /**
     * 屏蔽系统字体大小设置
     *
     * @return
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }


    public <T> T findViewHolder(View view, Class<T> c) {
        try {
            String p = getPackageName();
            T obj = c.newInstance();
            Field[] fields = c.getDeclaredFields();
            for (Field f : fields) {
                if (View.class.isAssignableFrom(f.getType())) {
                    int id = getResources().getIdentifier(f.getName(), "id", p);
                    f.setAccessible(true);
                    f.set(obj, view == null ? findViewById(id) : view.findViewById(id));
                }
            }

            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用户不允许权限，向用户说明权限的重要性，并支持用户去设置中开启权限
     */
    public void showDeniedDialog(final String title) {
        new android.app.AlertDialog.Builder(BaseActivity.this).setTitle(title)
                .setMessage("请允许使用该权限,拒绝将无法使用此功能")
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(title.equals("需要存储权限,才能运行程序")){
                            WXApplication.exit();
                        }
                        //TiaohuoApplication.exit();
                    }
                })
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Constant.isSetting = true;
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.parse("package:" + BaseActivity.this.getPackageName()));
                        intent.putExtra("cmp", "com.android.settings/.applications.InstalledAppDetails");
                        intent.addCategory("android.intent.category.DEFAULT");
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .show();
    }
    /**
     * 初始化时判断有没有网络
     */

    public boolean inspectNet() {
        this.netMobile = NetUtil.getNetWorkState(BaseActivity.this);
        return isNetConnect();

        // if (netMobile == 1) {
        // System.out.println("inspectNet：连接wifi");
        // } else if (netMobile == 0) {
        // System.out.println("inspectNet:连接移动数据");
        // } else if (netMobile == -1) {
        // System.out.println("inspectNet:当前没有网络");
        //
        // }
    }
    /**
     * 判断有无网络 。
     *
     * @return true 有网, false 没有网络.
     */
    public boolean isNetConnect() {
        if (netMobile == 1) {
            return true;
        } else if (netMobile == 0) {
            return true;
        } else if (netMobile == -1) {
            return false;

        }
        return false;
    }

    @Override
    public void onNetChange(int netMobile) {
        // TODO Auto-generated method stub
        this.oldNetMobile = this.netMobile;//记录原先的网络
        this.netMobile = netMobile;

    }


    //需要申请GETTask权限
//    private boolean isApplicationBroughtToBackground() {
//        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
//        if (!tasks.isEmpty()) {
//            ComponentName topActivity = tasks.get(0).topActivity;
//            if (!topActivity.getPackageName().equals(getPackageName())) {
//                return true;
//            }
//        }
//        return false;
//    }

//    public boolean wasBackground = false;    //声明一个布尔变量,记录当前的活动背景
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (isApplicationBroughtToBackground())
//            wasBackground = true;
//    }
//
//    public void onResume() {
//        super.onResume();
//        if (wasBackground) {//
//            Log.e("aa", "从后台回到前台");
//            wasBackground = false;
//        }
//    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
