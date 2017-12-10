package com.rzico.weex.net;



import com.rzico.weex.activity.BaseActivity;
import com.rzico.weex.json.ListResponse;
import com.rzico.weex.json.ResponseParser;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by shiweiwei on 2016/4/23.
 */
public abstract class BaseListRequestListener <T> implements HttpRequest.OnRequestListener {
    @Override
    public void onSuccess(BaseActivity activity, String result, String type) {

        Class cls = getActualTypeClass(getClass());
        ListResponse<T> response = ResponseParser.toListResponse(result, cls);
        if (response == null) {
            fail(-1);
        } else if ("success".equals(response.getMessage().getType())) {
            success(response.getData());
        } else if ("error".equals(response.getMessage().getType())
                && "session.invaild".equals(response.getMessage().getContent())) {
            if (activity != null) {
//                activity.showOffLineDialog();
            }
        } else if (activity != null) {
            activity.showToast(response.getMessage().getContent());
        }
    }

    @Override
    public void onFail(BaseActivity activity, String cacheData, int code) {
        fail(code);
    }

    public abstract void success(List<T> list);

    public abstract void fail(int code);

    private Class getActualTypeClass(Class entity) {
        ParameterizedType type = (ParameterizedType) entity.getGenericSuperclass();
        Class entityClass = (Class) type.getActualTypeArguments()[0];
        return entityClass;
    }
}