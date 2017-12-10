package com.rzico.weex.net;


import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.json.ObjectResponse;
import com.rzico.weex.json.ResponseParser;


import java.lang.reflect.ParameterizedType;

/**
 * Created by GuanYuCai on 16/3/9.
 */
public abstract class BaseRequestListener<T> implements HttpRequest.OnRequestListener {
    @Override
    public void onSuccess(BaseActivity activity, String result, String type) {
        Class cls = getActualTypeClass(getClass());
        ObjectResponse<T> response = (ObjectResponse<T>) ResponseParser.toObjectResponse(result, cls);
        if (response == null) {
            activity.showToast("检查网络");
            fail(-1);
        } else if ("success".equals(response.getMessage().getType())) {
            success(response.getData());
        } else if ("error".equals(response.getMessage().getType())
                || "session.invaild".equals(response.getMessage().getContent())) {
            if (activity != null) {
                activity.showToast(response.getMessage().getContent());
            }
        } else if (activity != null) {
            activity.showToast(response.getMessage().getContent());
        }
    }

    @Override
    public void onFail(BaseActivity activity, String cacheData, int code) {
        fail(code);
    }

    public abstract void success(T t);

    public abstract void fail(int code);

    private Class getActualTypeClass(Class entity) {
        ParameterizedType type = (ParameterizedType) entity.getGenericSuperclass();
        Class entityClass = (Class) type.getActualTypeArguments()[0];
        return entityClass;
    }
}
