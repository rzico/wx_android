package com.rzico.weex.db;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.rzico.weex.Constant;
import com.rzico.weex.WXApplication;
import com.rzico.weex.activity.LoginActivity;
import com.rzico.weex.db.bean.Redis;
import com.rzico.weex.db.notidmanager.DbCacheBean;
import com.rzico.weex.db.notidmanager.NotIdManager;
import com.rzico.weex.model.chat.Message;
import com.rzico.weex.net.XRequest;
import com.rzico.weex.utils.SharedUtils;
import com.taobao.weex.bridge.JSCallback;

import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jinlesoft on 2017/9/20.
 */

public class DbUtils {
    public static boolean save(String type, String key, String value, String sort, String keyword) throws DbException {
        if(WXApplication.getDb() != null){
            if(type.equals(XRequest.HTTPCACHE)){//如果是保存缓存 则因前端要求 要去掉前缀
                key = key.replace(Constant.helperUrl,"");
            }
            Redis redis = find(type, key);
            if( redis == null){
            //现在是默认用userid = 1
            redis = new Redis();
            if (!"systemInfo".equals(key)) {
                redis.setUserId(SharedUtils.readLoginId());//这里要去从服务器获取
            } else {
                redis.setUserId(0);
            }
            redis.setType(type);
            redis.setKey(key);
            redis.setValue(value);
            redis.setSort(sort);
            redis.setKeyword(keyword);
            WXApplication.getDb().save(redis);
            }else{
                if (!"systemInfo".equals(key)) {
                    redis.setUserId(SharedUtils.readLoginId());//这里要去从服务器获取
                } else {
                    redis.setUserId(0);
                }
                redis.setType(type);
                redis.setKey(key);
                redis.setValue(value);
                redis.setSort(sort);
                redis.setKeyword(keyword);
                WXApplication.getDb().update(redis, WhereBuilder.b("key", "=", key).and("type", "=", type).and("userId", "=", redis.getUserId()), "value", "sort", "keyword");
            }
            return true;//保存成功
        }else{
            return false;
        }
    }

    /**
     *
     * @param type
     * @param key
     * @return
     */
    public static Redis find(String type, String key) throws DbException {
            if(WXApplication.getDb() != null){
                long uid = 0;
                if (!"systemInfo".equals(key)) {
                    uid = SharedUtils.readLoginId();
                }
//            List<Redis> data = WXApplication.getDb().selector(Redis.class).findAll();
                if(type.equals(XRequest.HTTPCACHE)){//如果是保存缓存 则因前端要求 要去掉前缀
                    key = key.replace(Constant.helperUrl,"");
                }

                return WXApplication.getDb().selector(Redis.class).where("userId","=", uid).and("type", "=", type).and("key", "=", key).findFirst();
            }else{
                return null;
            }

    }
    public static List<Redis> findList(String type,  String keyword, String orderBy, int pageSize) throws DbException {
        if(WXApplication.getDb() != null){
//            List<Redis> data = WXApplication.getDb().selector(Redis.class).findAll();
            Selector<Redis> selector = null;
            if(!keyword.equals("")){
                selector = WXApplication.getDb().selector(Redis.class).where("userId","=", SharedUtils.readLoginId()).and("keyword", "like",  "%" + keyword + "%").orderBy("type", orderBy.equals("desc")).orderBy("sort", orderBy.equals("desc"));
            }else {
                selector = WXApplication.getDb().selector(Redis.class).where("userId","=", SharedUtils.readLoginId()).orderBy("type", orderBy.equals("desc")).orderBy("sort", orderBy.equals("desc"));
            }
            if(!type.equals("")){
                selector = selector.and("type", "=", type);
            }
            if(pageSize == 0){
                return selector.findAll();
            }else{
                return selector.limit(pageSize).findAll();
            }
        }else{
            return null;
        }
    }

    public static boolean delete(String type, String key) throws DbException{
        if(WXApplication.getDb() != null){
            Redis redis = find(type, key);
            if(redis != null){
                WXApplication.getDb().delete(redis);
                return true;
            }
        }
        return false;
    }
    public static boolean deleteAll(String type) throws DbException{
        if(WXApplication.getDb() != null){
            List<Redis> redis =  WXApplication.getDb().selector(Redis.class).where("userId","=", SharedUtils.readLoginId()).and("type","=", type).findAll();
            if(redis != null){
                WXApplication.getDb().delete(redis);
                return true;
            }
        }
        return false;
    }

    public static boolean checkLogin(){
        return true;
//        if(SharedUtils.readLoginId() == 0){
//            return false;
//        }else {
//            return true;
//        }
    }
    public static void handleNotLogin(DbCacheBean dbCacheBean){
        NotIdManager.enQueue(dbCacheBean);
        Intent intent = new Intent();
        intent.setClass(WXApplication.getActivity(), LoginActivity.class);
        WXApplication.getActivity().startActivity(intent);
    }
    public static void doSqlite(@NonNull DbCacheBean dbCacheBean) throws DbException {
        com.rzico.weex.model.info.Message message = new com.rzico.weex.model.info.Message();
        Redis redis = null;
        List<Redis> rediss = null;

        if(WXApplication.getDb() == null) {
            initDb();
        }
            switch (dbCacheBean.getDoType()) {
//                保存
                case SAVE:
                    redis = DbUtils.find(dbCacheBean.getType(), dbCacheBean.getKey());

                    DbUtils.save(dbCacheBean.getType(), dbCacheBean.getKey(), dbCacheBean.getValue(), dbCacheBean.getSort(), dbCacheBean.getKeyword());
                    message.setType("success");
                    if (redis == null) {
                        message.setContent("保存成功");
                    } else {
                        message.setContent("更新成功");
                    }
                    message.setData(null);
                    break;

//                查找单条
                case FIND:
                    redis = DbUtils.find(dbCacheBean.getType(), dbCacheBean.getKey());
                    if (redis != null) {
                        message.setType("success");
                        message.setContent("读取成功");
                        message.setData(redis);
                    } else {
                        message.setType("success");
                        message.setContent("没有数据");
                        message.setData("");
                    }
                    break;
//                查找多条
                case FINDLIST:
                    rediss = DbUtils.findList(dbCacheBean.getType(), dbCacheBean.getKeyword(), dbCacheBean.getOrderBy(), dbCacheBean.getPageSize());
                    int i;
                    List<Redis> nowRedis = new ArrayList<>();
                    if (rediss != null && rediss.size() > 0) {
                        for (i = dbCacheBean.getCurrent(); i < rediss.size(); i++) {
                            nowRedis.add(rediss.get(i));
                        }
                        message.setSuccess(nowRedis, "读取成功");
                    } else {
                        message.setType("success");
                        message.setContent("没有数据");
                        message.setData("");
                    }
                    break;
//                删除单条
                case DELETE:
                    if(DbUtils.delete(dbCacheBean.getType(), dbCacheBean.getKey())){
                        message.setType("success");
                        message.setContent("删除成功");
                        message.setData(null);
                    }else {
                        message.setType("error");
                        message.setContent("删除失败");
                        message.setData(null);
                    }
                    break;
//                  删除多条
                case DELETEALL:

                    if(DbUtils.deleteAll(dbCacheBean.getType())){
                        message.setType("success");
                        message.setContent("删除成功");
                        message.setData(null);
                    }else {
                        message.setType("error");
                        message.setContent("删除失败");
                        message.setData(null);
                    }
                    break;
            }
        dbCacheBean.getJsCallback().invoke(message);
    }
    public static void reDoSql(){
        while (!NotIdManager.isEmpty()){
            try {
                DbCacheBean dbCacheBean = NotIdManager.deQueue();
                if(dbCacheBean != null && dbCacheBean.getJsCallback() != null){
                    DbUtils.doSqlite(dbCacheBean);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    private static void initDb() {
        //初始化数据库
        org.xutils.DbManager.DaoConfig daoConfig = XDB.getDaoConfig();
        WXApplication.setDb(x.getDb(daoConfig));
    }
}
