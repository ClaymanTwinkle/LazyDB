package org.kesar.lazy.lazydb.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.kesar.lazy.lazydb.config.DeBugLogger;
import org.kesar.lazy.lazydb.domain.ColumnInfo;
import org.kesar.lazy.lazydb.domain.KeyValue;
import org.kesar.lazy.lazydb.util.ObjectUtil;
import org.kesar.lazy.lazydb.util.TableUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * SqliteDBExecutor
 *
 * @author andyqtchen <br/>
 *         db执行器
 *         创建日期：2017/6/29 12:02
 */
public final class SQLiteDBExecutor {
    private final SQLiteDBHelper helper;

    public SQLiteDBExecutor(SQLiteDBHelper helper) {
        this.helper = helper;
    }

    public void createTable(String sql) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(sql);
    }

    public void dropTable(final String sql) throws Exception {
        executeTransaction(new NoQueryOperation() {
            @Override
            public void onOperation(SQLiteDatabase db) throws Exception {
                db.execSQL(sql);
            }
        });
    }

    public void dropAllTables() throws Exception {
        executeTransaction(new NoQueryOperation() {
            @Override
            public void onOperation(SQLiteDatabase db) throws Exception {
                helper.deleteAllTables(db);
            }
        });
    }

    public List<String> queryAllTableNames(String sql) {
        List<String> tableNames = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    tableNames.add(cursor.getString(0));
                }
            } finally {
                cursor.close();
            }
        }
        return tableNames;
    }

    public List<ColumnInfo> queryAllColumnsFromTable(String sql) throws NoSuchFieldException, InstantiationException, ParseException, IllegalAccessException {
        List<ColumnInfo> columnInfos = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    ColumnInfo columnInfo = ObjectUtil.buildObject(ColumnInfo.class, cursor);
                    columnInfos.add(columnInfo);
                }
            } finally {
                cursor.close();
            }
        }
        return columnInfos;
    }

    public boolean isTableExist(String sql) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    if (cursor.getInt(0) != 0) {
                        return true;
                    }
                }
            } finally {
                cursor.close();
            }
        }
        return false;
    }

    public void insert(final Object object) throws Exception {
        final String table = TableUtil.getTableName(object);
        final ContentValues values = TableUtil.getContentValues(object);
        // 插入表
        executeTransaction(new NoQueryOperation() {
            @Override
            public void onOperation(SQLiteDatabase db) throws Exception {
                db.insert(table, null, values);
            }
        });
    }

    public void insert(@NonNull final List objectList) throws Exception {
        executeTransaction(new NoQueryOperation() {
            @Override
            public void onOperation(SQLiteDatabase db) throws Exception {
                for (Object object : objectList) {
                    final String table = TableUtil.getTableName(object);
                    final ContentValues values = TableUtil.getContentValues(object);
                    db.insert(table, null, values);
                }
            }
        });
    }

    public void update(@NonNull final Object object) throws Exception {
        executeTransaction(new NoQueryOperation() {
            @Override
            public void onOperation(SQLiteDatabase db) throws Exception {
                // 根据id更新object
                KeyValue idColumn = TableUtil.getIDColumn(object);
                if (null == idColumn) {
                    throw new IllegalStateException("Object does not include the id field");
                }
                if (null == idColumn.getValue()) {
                    throw new IllegalStateException("The value of the id field cannot be null");
                }
                String tableName = TableUtil.getTableName(object);
                ContentValues values = TableUtil.getContentValuesWithOutID(object);
                String whereClause = idColumn.getKey() + "=?";
                String[] whereArgs = new String[]{idColumn.getValue().toString()};

                db.update(tableName, values, whereClause, whereArgs);
            }
        });
    }

    public void delete(@NonNull final Object object) throws Exception {
        executeTransaction(new NoQueryOperation() {
            @Override
            public void onOperation(SQLiteDatabase db) throws Exception {
                KeyValue column = TableUtil.getIDColumn(object);
                if (null == column) {
                    throw new IllegalStateException("Object does not include the id field");
                }
                if (null == column.getValue()) {
                    throw new IllegalStateException("The value of the id field cannot be null");
                }

                String tableName = TableUtil.getTableName(object);
                String whereClause = column.getKey() + "=?";
                String[] whereArgs = new String[]{column.getValue().toString()};
                // 根据id删除object
                db.delete(tableName, whereClause, whereArgs);
            }
        });
    }

    public void delete(@NonNull final List objectList) throws Exception {
        executeTransaction(new NoQueryOperation() {
            @Override
            public void onOperation(SQLiteDatabase db) throws Exception {
                for (Object object : objectList) {
                    KeyValue column = TableUtil.getIDColumn(object);
                    if (null == column) {
                        throw new IllegalStateException("Object does not include the id field");
                    }
                    if (null == column.getValue()) {
                        throw new IllegalStateException("The value of the id field cannot be null");
                    }
                    String tableName = TableUtil.getTableName(object);
                    String whereClause = column.getKey() + "=?";
                    String[] whereArgs = new String[]{column.getValue().toString()};
                    // 根据id删除object
                    db.delete(tableName, whereClause, whereArgs);
                }
            }
        });
    }

    public void delete(final Class<?> clazz, final String whereClause, final String[] whereArgs) throws Exception {
        executeTransaction(new NoQueryOperation() {
            @Override
            public void onOperation(SQLiteDatabase db) {
                db.delete(TableUtil.getTableName(clazz), whereClause, whereArgs);
            }
        });
    }

    public <T> SelectBuilder<T> query(Class<T> clazz) {
        return new SelectBuilder<>(this, clazz);
    }

    public <T> Cursor query(SelectBuilder<T> builder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        return db.query(TableUtil.getTableName(builder.objectClass),
                builder.columns,
                builder.whereSection,
                builder.whereArgs,
                builder.groupBy,
                builder.having,
                builder.orderBy,
                builder.limit);
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        SQLiteDatabase db = helper.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    /**
     * 执行非查询操作事物
     *
     * @param operation 非查询操作
     */
    public void executeTransaction(NoQueryOperation operation) throws Exception {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.beginTransaction();
            DeBugLogger.d("NoQuery", "beginTransaction");
            if (operation != null) {
                operation.onOperation(db);
            }
            db.setTransactionSuccessful();
            DeBugLogger.d("NoQuery", "transactionSuccessful");
        } finally {
            db.endTransaction();
            DeBugLogger.d("NoQuery", "endTransaction");
        }
    }

    public interface NoQueryOperation {
        void onOperation(SQLiteDatabase db) throws Exception;
    }
}
