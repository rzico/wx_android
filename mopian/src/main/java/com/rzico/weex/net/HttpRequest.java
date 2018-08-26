package com.rzico.weex.net;

import android.content.Intent;


import com.google.gson.Gson;
import com.rzico.weex.Constant;
import com.rzico.weex.WXApplication;
import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.activity.LoginActivity;
import com.rzico.weex.db.DbUtils;
import com.rzico.weex.model.RequestBean;
import com.rzico.weex.model.info.Message;
import com.rzico.weex.net.session.SessionOutManager;
import com.rzico.weex.net.session.TaskBean;
import com.rzico.weex.utils.LoginUtils;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.rzico.weex.Constant.isLoginAcitivity;
import static com.rzico.weex.Constant.key;

/**
 * Created by GuanYuCai on 16/3/9.
 */
public class HttpRequest {


    private long requestTime = 0;
    private HttpRequest() {

        tasks = new ArrayList<>();
    }

    public static interface OnRequestListener {
        public static int ERR_NO_NETWORK = 0;

        public void onSuccess(BaseActivity activity, String result, String type);

        public void onFail(BaseActivity activity,String cacheData, int code);
    }

    private static class HttpFetcherFactory {
        private static HttpRequest instance = instance();
    }

    private static HttpRequest instance() {
        return new HttpRequest();
    }

    public static HttpRequest getInstance() {
        return HttpFetcherFactory.instance;
    }

    private List<XRequest> tasks;
    public void unRegisterTasks(BaseActivity activity) {

        Iterator<XRequest> it = tasks.iterator();
        while (it.hasNext()) {
            XRequest req = it.next();
            if (req.activity!=null && activity !=null && req.activity == activity) {
                req.requestListener = null;
                req.activity = null;
            }
        }
    }
    public void execute(XRequest task) {

//        if (!Utils.checkConnection(task.activity) && !Utils.isApkDebugable(task.activity)) {
//            if (task.requestListener != null) {
//                task.requestListener.onFail(task.activity, task.cacheData,OnRequestListener.ERR_NO_NETWORK);
//            }
//            return;
//        }

        Callback.CommonCallback<String> callback = getCallback(task);

        RequestParams params = new RequestParams(task.url);
        if (task.params != null && !task.params.isEmpty()) {
            if (XRequest.FILE.equals(task.method)) {
                for (String key : task.params.keySet()) {
                    Object value = task.params.get(key);
                    if (value == null) {
                        continue;
                    }
                    params.addBodyParameter(key, value, "");
                }
                x.http().post(params, callback);
                tasks.add(task);
                return;
            }

            for (String key : task.params.keySet()) {
                Object value = task.params.get(key);
                if (value == null) {
                    continue;
                }

                // GET
                if (XRequest.GET.equals(task.method)) {
                    if (value instanceof String) {
                        params.addBodyParameter(key, (String) value);
                    } else if (value instanceof Integer || value instanceof Long
                            || value instanceof Float || value instanceof Double) {
                        params.addBodyParameter(key, value + "");
                    }
                    continue;
                }

                // POST
                if (value instanceof List) {
                    List<Object> list = (List<Object>) value;
                    for (Object v : list) {
                        if (v instanceof String) {
                            params.addListParameter(key, (String) v);
                        } else if (v instanceof Integer || v instanceof Long
                                || v instanceof Float || v instanceof Double) {
                            params.addListParameter(key, v + "");
                        }
                    }

                } else if (value instanceof String) {
                    params.addListParameter(key, (String) value);
                } else if (value instanceof Integer || value instanceof Long
                        || value instanceof Float || value instanceof Double) {
                    params.addListParameter(key, value + "");
                }
            }
        } else if (task.requestBean != null && !task.requestBean.isEmpty()) {
            for (RequestBean bean : task.requestBean) {
                params.addListParameter(bean.key, bean.value + "");
            }
        }
       String uid= WXApplication.getUid();
       String app= Constant.app;
       String tsp = String.valueOf(System.currentTimeMillis());

       String tkn = MD5.Md5(uid + app + tsp + key);

        params.addHeader("x-uid", uid);//设备号
        params.addHeader("x-app", app);//包名
        params.addHeader("x-tsp", tsp);//时间戳
        params.addHeader("x-tkn", tkn);//令牌

        if (XRequest.GET.equals(task.method)) {
            x.http().get(params, callback);
        } else if (XRequest.POST.equals(task.method)) {
            x.http().post(params, callback);
        }

        tasks.add(task);
        requestTime = System.currentTimeMillis();
    }

    private Callback.CommonCallback<String> getCallback(final XRequest task) {
        Callback.CommonCallback<String> callback = new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("======================" + task.url + "|success|请求耗时:" + (System.currentTimeMillis() - requestTime));
                try {
                    Message entity= new Gson().fromJson(result, Message.class);
                    String type= entity.getType();
                    String content=entity.getContent();
                    String md5 = entity.getMd5();

                    if ("error".equals(type) && "session.invaild".equals(content)) {
                        if (task.activity != null) {
                            //如果会话实效 讲当前的请求保存起来
                            TaskBean taskBean = new TaskBean();
                            taskBean.setUrl(task.url.replace(Constant.helperUrl, ""));//将helperUrl host 替换掉
                            taskBean.setActivity(task.activity);
                            taskBean.setMethod(task.method);
                            taskBean.setParams(task.params);
                            taskBean.setListener(task.requestListener);
                            SessionOutManager.enQueue(taskBean);
                            if(!isLoginAcitivity){
                                //跳转至登录页面
                                Intent intent = new Intent(task.activity, LoginActivity.class);
                                task.activity.startActivity(intent);
                                isLoginAcitivity = true;
                            }
                        }
                    } else {
                        if ("success".equals(type) && "login.success".equals(content)){
                            if(task.activity != null){
                                LoginUtils.checkLogin(task.activity, null, null);//不做处理 就传null
                                //这里拦截了 登录成功
                                while (!SessionOutManager.isEmpty()){//队列里如果有数据 就请求
                                    TaskBean taskBean = SessionOutManager.deQueue();
                                    //重新请求一遍
                                    new XRequest(taskBean.getActivity(), taskBean.getUrl(),taskBean.getMethod(), taskBean.getParams()).setOnRequestListener(taskBean.getListener()).execute();
                                }
                            }
                        }

                        if (task.requestListener != null) {
                            //如果是get并且请求成功就缓存起来
                            if(XRequest.GET.equals(task.method)){
                                if(type.equals("success")){
                                  task.requestListener.onSuccess(task.activity, result, "success");//200
                                  DbUtils.save(XRequest.HTTPCACHE, task.url, result, "N", md5);
                                }else if(type.equals("warn")){
                                    //如果是警告 就是缓存数据没变 不需要保存 并且把缓存告诉前端
                                    task.requestListener.onSuccess(task.activity, task.cacheData, "success");//200
                                }
                                else{//以上都不是 就是 error
                                 task.requestListener.onSuccess(task.activity, task.cacheData.equals("") ? result : task.cacheData, content);//304
                                }
                            }else{
                                task.requestListener.onSuccess(task.activity, result, "success");//200
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
//                    if(result != null && task.requestListener != null && (result.startsWith("{") && result.endsWith("}") || result.startsWith("[") && result.endsWith("]"))){
//                            task.requestListener.onSuccess(task.activity, result);
//                    }else
                    if (task.requestListener != null) {
                        task.requestListener.onFail(task.activity, task.cacheData, XRequest.GET.equals(task.params) ? 1 : 2);
                    }
//                    LogUtil.d(e.getMessage());
//                    //yinping后期添加的，如有不妥删除
//                    TiaohuoApplication.exit();
                }
                tasks.remove(task);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("======================" + task.url + "|error|请求耗时:" + (System.currentTimeMillis() - requestTime) + "：：：" + ex.toString());
                if (ex instanceof HttpException) { // 网络错误
                    if (task.requestListener != null) {
                        task.requestListener.onFail(task.activity, task.cacheData,  XRequest.GET.equals(task.params) ? 1 : 2);
                        //task.activity.showToast("网络错误");
                    }
                } else { // 其他错误
                    if (task.requestListener != null) {
                        task.requestListener.onFail(task.activity, task.cacheData,  XRequest.GET.equals(task.params) ? 1 : 2);
//                    task.activity.showToast("其他错误");
                    }
                }
                tasks.remove(task);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                tasks.remove(task);
            }

            @Override
            public void onFinished() {
                tasks.remove(task);
            }
        };

        return callback;
    }
}
