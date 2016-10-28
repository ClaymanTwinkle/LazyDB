package org.kesar.lazy.lazydb.config;

import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库版本更新监听器
 * Created by kesar on 2016/6/21 0021.
 */
public interface DBUpgradeListener
{
    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}