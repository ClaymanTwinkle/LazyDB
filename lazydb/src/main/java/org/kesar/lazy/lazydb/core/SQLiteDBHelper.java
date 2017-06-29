package org.kesar.lazy.lazydb.core;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import org.kesar.lazy.lazydb.config.DBConfig;
import org.kesar.lazy.lazydb.config.DeBugLogger;

/**
 * SQLiteOpenHelper实现类
 * Created by kesar on 2016/6/21 0021.
 */
public class SQLiteDBHelper extends SQLiteOpenHelper {
    private DBConfig.DBUpgradeListener mDbUpgradeListener;

    public SQLiteDBHelper(DBConfig config) {
        super(config.getContext(), config.getDBName(), null, config.getDBVersion());
        this.mDbUpgradeListener = config.getUpgradeListener();
    }

    public SQLiteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SQLiteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DeBugLogger.d("onUpgrade","onUpgrade DataBase from version " + oldVersion + " to " + newVersion);

        if (mDbUpgradeListener != null) {
            mDbUpgradeListener.onUpgrade(db, oldVersion, newVersion);
        } else {
            // 默认删除表
            deleteAllTables(db);
        }
    }

    /**
     * 删除数据库中所有表
     *
     * @param db
     */
    public void deleteAllTables(SQLiteDatabase db) {
        String queryAllTableNameSql = SQLBuilder.buildQueryAllTableNamesSql();

        Cursor cursor = db.rawQuery(queryAllTableNameSql, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String sql = SQLBuilder.buildDropTableSql(cursor.getString(0));
                    // 执行删除表的sql语句
                    db.execSQL(sql);
                }
            } finally {
                cursor.close();
            }
        }
    }
}