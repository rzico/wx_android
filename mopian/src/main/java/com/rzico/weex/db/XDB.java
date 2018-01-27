package com.rzico.weex.db;

import android.os.Environment;

import com.rzico.weex.Constant;
import com.rzico.weex.WXApplication;
import com.yixiang.mopian.constant.AllConstant;

import org.xutils.DbManager;

import java.io.File;

/**
 * Created by Jinlesoft on 2017/9/19.
 * xutils 数据库管理工具
 */

public class XDB {
    static DbManager.DaoConfig daoConfig;
    public static DbManager.DaoConfig getDaoConfig(){
            File file=new File(AllConstant.getDiskCachePath(WXApplication.getActivity()) + "/db");
            if(daoConfig==null){
                daoConfig=new DbManager.DaoConfig()
                        .setDbName("wx1.db")
                        .setDbDir(file)
                        .setDbVersion(1)
                        .setAllowTransaction(true)
                        .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                            @Override
                            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {

                            }
                        });
            }
        return daoConfig;
    }
}
