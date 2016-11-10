package org.kesar.lazy.lazydb.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import org.kesar.lazy.lazydb.builder.SqlBuilder;
import org.kesar.lazy.lazydb.config.DBConfig;
import org.kesar.lazy.lazydb.config.DBUpgradeListener;
import org.kesar.lazy.lazydb.config.DeBugLogger;

/**
 * SQLiteOpenHelper实现类
 * Created by kesar on 2016/6/21 0021.
 */
public class SqliteDBHelper extends SQLiteOpenHelper {
    private DBUpgradeListener mDbUpgradeListener;

    public SqliteDBHelper(DBConfig config) {
        super(config.getContext(), config.getDBName(), null, config.getDBVersion());
        this.mDbUpgradeListener = config.getUpgradeListener();
    }

    public SqliteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SqliteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DeBugLogger.d("onUpgrade DataBase from version " + oldVersion + " to " + newVersion);

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
        String queryAllTableNameSql = SqlBuilder.buildQueryAllTableNamesSql();

        Cursor cursor = db.rawQuery(queryAllTableNameSql, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String sql = SqlBuilder.buildDropTableSql(cursor.getString(0));
                    LogUtils.d("deleteAllTables",sql);
                    // 执行删除表的sql语句
                    db.execSQL(sql);
                }
            } finally {
                cursor.close();
            }
        }
    }

    /**
     * 执行非查询操作事物
     *
     * @param operation 非查询操作
     */
    public void executeNoQueryTransaction(NoQueryOperation operation) throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            LogUtils.d("NoQuery","beginTransaction");
            if (operation != null) {
                operation.onNoQuery(db);
            }
            db.setTransactionSuccessful();
            LogUtils.d("NoQuery","transactionSuccessful");
        } finally {
            db.endTransaction();
            LogUtils.d("NoQuery","endTransaction");
        }
    }

    public interface NoQueryOperation {
        void onNoQuery(SQLiteDatabase db) throws Exception;
    }
}