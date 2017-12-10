package com.rzico.weex.json;


import com.google.gson.Gson;
import com.rzico.weex.model.monitor.ResultBean;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ResponseParser {
    public static Gson g = new Gson();

    public static BaseResponse toBaseResponse(String jsonString) {
        try {
            return g.fromJson(jsonString, BaseResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static IntResponse toIntResponse(String jsonString) {
        try {
            return g.fromJson(jsonString, IntResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static StringResponse toStringResponse(String jsonString) {
        try {
            return g.fromJson(jsonString, StringResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LongResponse toLongResponse(String jsonString) {
        try {
            return g.fromJson(jsonString, LongResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DoubleResponse toDoubleResponse(String jsonString) {
        try {
            return g.fromJson(jsonString, DoubleResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static IntsResponse toIntsResponse(String jsonString) {
        try {
            return g.fromJson(jsonString, IntsResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static StringsResponse toStringsResponse(String jsonString) {
        try {
            return g.fromJson(jsonString, StringsResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MapResponse toMapResponse(String jsonString) {
        try {
            return g.fromJson(jsonString, MapResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 通过json得到一条数据
    public static <T> Object getObjectByJson(String jsonString, Class<T> clazz) {
        try {
            return g.fromJson(jsonString, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> ObjectResponse<T> toObjectResponse(String jsonString, Class<T> clazz) {
        try {
            Type type = type(ObjectResponse.class, clazz);
//            java.lang.reflect.Type type = new TypeToken<ObjectResponse<T>>(){}.getType();
            return g.fromJson(jsonString, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> ListResponse<T> toListResponse(String jsonString, Class<T> clazz) {
        try {
            Type type = type(ListResponse.class, clazz);

//            java.lang.reflect.Type type = new TypeToken<ListResponse<T>>() {}.getType();
            return g.fromJson(jsonString, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> toSimpleListResponse(String jsonString, Class<T> clazz) {
        try {
            Type type = type(List.class, clazz);
            return g.fromJson(jsonString, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }


    public static ResultBean getResult(String jsonString) {
        ResultBean resultBean = new ResultBean();
        try {
            JSONObject obj = new JSONObject(jsonString);
            resultBean.code = obj.getString("code");
            resultBean.msg = obj.getString("msg");
            resultBean.data = obj.getString("data");
        } catch (Exception e) {
//            Logger.e("====================== Paras LYResult ======================");
//            Logger.e(e.toString());
        }
        return resultBean;
    }

}
