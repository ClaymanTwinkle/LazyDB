package com.kesar.demo;

import android.content.Context;

import org.kesar.lazy.lazydb.LazyDB;
import org.kesar.lazy.lazydb.config.DBConfig;

/**
 * 数据库构建工程
 * Created by kesar on 16-11-10.
 */
public class LazyDBFactory {
    public static LazyDB createDB(Context context) {
        return LazyDB.create(new DBConfig.Builder(context)
                .setDataBaseName("demo.db")
                .setDatabaseVersion(1)
                .setDebug(true)
                .build());
    }
}
