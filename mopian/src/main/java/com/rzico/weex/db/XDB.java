package com.rzico.weex.db;

import android.os.Environment;

import com.rzico.weex.Constant;

import org.xutils.DbManager;

import java.io.File;

/**
 * Created by Jinlesoft on 2017/9/19.
 * xutils 数据库管理工具
 */

public class XDB {
    static DbManager.DaoConfig daoConfig;
    public static DbManager.DaoConfig getDaoConfig(){
        File file=new File(Environment.getExternalStorageDirectory().getPath() + "/" + Constant.app + "/db");
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
