package org.kesar.lazy.lazydb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.kesar.lazy.lazydb.builder.SelectBuilder;
import org.kesar.lazy.lazydb.builder.SqlBuilder;
import org.kesar.lazy.lazydb.config.DBConfig;
import org.kesar.lazy.lazydb.domain.ColumnInfo;
import org.kesar.lazy.lazydb.util.KeyValue;
import org.kesar.lazy.lazydb.util.ObjectUtil;
import org.kesar.lazy.lazydb.util.SqliteDBHelper;
import org.kesar.lazy.lazydb.util.TableUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 简单数据库
 * Created by kesar on 2016/6/21 0021.
 */
public final class LazyDB {
    /**
     * 阻止通过new来实例化LazyDB
     * 应该使用create方法来创建LazyDB
     */
    private LazyDB() {
    }

    private SqliteDBHelper helper;

    /**
     * 创建默认配置的数据库
     *
     * @param context 上下文
     */
    public static LazyDB create(Context context) {
        return create(DBConfig.getDefaultConfig(context));
    }


    /**
     * 创建自定义配置的数据库
     *
     * @param config 配置
     */
    public static LazyDB create(DBConfig config) {
        return new LazyDB(config);
    }

    /**
     * 唯一的构造器
     *
     * @param config 数据库配置文件
     */
    private LazyDB(DBConfig config) {
        this.helper = new SqliteDBHelper(config);
    }

    /**
     * SQLiteOpenHelper
     *
     * @return SQLiteOpenHelper
     */
    public SqliteDBHelper getHelper() {
        return helper;
    }

    /**
     * 创建表
     *
     * @param clazz 类
     */
    public void createTable(Class<?> clazz) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = SqlBuilder.buildCreateTableSql(clazz);
        db.execSQL(sql);
    }

    /**
     * 创建表
     *
     * @param tableName       表名
     * @param idColumn        id
     * @param idDataType      id的类型
     * @param isAutoIncrement 是否自动增长
     * @param columns         其他列名，不包括id
     */
    public void createTable(String tableName, String idColumn, String idDataType, boolean isAutoIncrement, String... columns) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = SqlBuilder.buildCreateTableSql(tableName, idColumn, idDataType, isAutoIncrement, columns);
        db.execSQL(sql);
    }


    /**
     * 删除表
     *
     * @param clazz 类
     */
    public void dropTable(final Class<?> clazz) throws Exception {
        helper.executeNoQueryTransaction(new SqliteDBHelper.NoQueryOperation() {
            @Override
            public void onNoQuery(SQLiteDatabase db) throws Exception {
                String sql = SqlBuilder.buildDropTableSql(clazz);
                db.execSQL(sql);
            }
        });
    }

    /**
     * 删除数据库中所有的表
     */
    public void dropAllTables() throws Exception {
        helper.executeNoQueryTransaction(new SqliteDBHelper.NoQueryOperation() {
            @Override
            public void onNoQuery(SQLiteDatabase db) throws Exception {
                helper.deleteAllTables(db);
            }
        });
    }

    /**
     * 查询所有表名
     *
     * @return 所有表名的集合；若没有表，则是空集合
     */
    public List<String> queryAllTableNames() {
        List<String> tableNames = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = SqlBuilder.buildQueryAllTableNamesSql();
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


    /**
     * 从表中查找出所有字段名
     *
     * @param clazz class
     * @return 字段列表
     * @throws NoSuchFieldException
     * @throws InstantiationException
     * @throws ParseException
     * @throws IllegalAccessException
     */
    public List<ColumnInfo> queryAllColumnsFromTable(Class<?> clazz) throws NoSuchFieldException, InstantiationException, ParseException, IllegalAccessException {
        List<ColumnInfo> columnInfos = new ArrayList<>();
        String sql = SqlBuilder.buildQueryTableInfoSql(clazz);
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

    /**
     * 表是否存在
     *
     * @param clazz 类
     * @return true 表存在，false 表不存在
     */
    public boolean isTableExist(Class<?> clazz) {
        String sql = SqlBuilder.buildQueryTableIsExistSql(clazz);
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

    /**
     * 判断对象是否存在数据库中
     *
     * @param object 对象
     * @return true 存在，false 不存在
     * @throws IllegalAccessException
     */
    public boolean isObjectExist(Object object) throws IllegalAccessException {
        // 如果表存在
        if (isTableExist(object.getClass())) {
            KeyValue keyValue = TableUtil.getIDColumn(object);
            if (keyValue == null) {
                return false;
            }
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(TableUtil.getTableName(object.getClass()), new String[]{"count(*)"}, keyValue.getKey() + "=?", new String[]{keyValue.getValue().toString()}, null, null, null);
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
        }
        return false;
    }

    /**
     * 插入对象
     *
     * @param object 对象
     */
    public void insert(@NonNull final Object object) throws Exception {
        final Class<?> clazz = object.getClass();
        // 表不存在，创建表
        if (!isTableExist(clazz)) {
            // 创建表
            createTable(clazz);
        }
        // 插入表
        helper.executeNoQueryTransaction(new SqliteDBHelper.NoQueryOperation() {
            @Override
            public void onNoQuery(SQLiteDatabase db) throws Exception {
                db.insert(TableUtil.getTableName(clazz), null, TableUtil.getContentValues(object));
            }
        });
    }

    /**
     * 插入多个对象数据
     *
     * @param objectList 对象集合
     * @throws IllegalAccessException
     */
    public void insert(@NonNull final List objectList) throws Exception {
        // 对象为空不处理
        if (objectList.isEmpty()) {
            return;
        }
        final Class<?> clazz = objectList.get(0).getClass();
        // 表不存在，创建表
        if (!isTableExist(clazz)) {
            // 创建表
            createTable(clazz);
        }
        // 插入表
        helper.executeNoQueryTransaction(new SqliteDBHelper.NoQueryOperation() {
            @Override
            public void onNoQuery(SQLiteDatabase db) throws Exception {
                for (Object object : objectList) {
                    db.insert(TableUtil.getTableName(clazz), null, TableUtil.getContentValues(object));
                }
            }
        });
    }

    /**
     * 更新对象
     *
     * @param object 对象
     * @throws IllegalAccessException
     */
    public void update(@NonNull final Object object) throws Exception {
        helper.executeNoQueryTransaction(new SqliteDBHelper.NoQueryOperation() {
            @Override
            public void onNoQuery(SQLiteDatabase db) throws Exception {
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

    /**
     * 插入或更新对象
     *
     * @param object 对象
     * @throws IllegalAccessException
     */
    public void insertOrUpdate(@NonNull Object object) throws Exception {
        // 表不存在，创建表
        if (!isTableExist(object.getClass()) || !isObjectExist(object)) {
            insert(object);
        } else {
            update(object);
        }
    }

    /**
     * 删除对象
     *
     * @param object 对象
     * @throws IllegalAccessException
     */
    public void delete(@NonNull final Object object) throws Exception {
        // 数据库表不存在不处理
        if (!isTableExist(object.getClass())) {
            return;
        }
        helper.executeNoQueryTransaction(new SqliteDBHelper.NoQueryOperation() {
            @Override
            public void onNoQuery(SQLiteDatabase db) throws Exception {
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

    /**
     * 删除多个object集合
     *
     * @param objectList object集合
     * @throws IllegalAccessException
     */
    public void delete(@NonNull final List objectList) throws Exception {
        // 对象为空不处理
        if (objectList.isEmpty()) {
            return;
        }
        Class<?> clazz = objectList.get(0).getClass();
        // 数据库表不存在不处理
        if (!isTableExist(clazz)) {
            return;
        }
        helper.executeNoQueryTransaction(new SqliteDBHelper.NoQueryOperation() {
            @Override
            public void onNoQuery(SQLiteDatabase db) throws Exception {
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

    /**
     * 删除数据
     *
     * @param clazz       类
     * @param whereClause 例如：id=?
     * @param whereArgs   ?的值
     */
    public void delete(final Class<?> clazz, final String whereClause, final String[] whereArgs) throws Exception {
        // 数据库表不存在不处理
        if (!isTableExist(clazz)) {
            return;
        }
        helper.executeNoQueryTransaction(new SqliteDBHelper.NoQueryOperation() {
            @Override
            public void onNoQuery(SQLiteDatabase db) {
                db.delete(TableUtil.getTableName(clazz), whereClause, whereArgs);
            }
        });
    }

    /**
     * 执行查询操作
     *
     * @param clazz 类
     * @return select操作的构建器
     */
    public <T> SelectBuilder<T> query(Class<T> clazz) {
        return new SelectBuilder<>(helper, clazz);
    }

    /**
     * 通过id查询
     *
     * @param clazz 类
     * @param id    id
     * @return object
     * @throws NoSuchFieldException
     * @throws InstantiationException
     * @throws ParseException
     * @throws IllegalAccessException
     */
    public <T> T queryById(Class<T> clazz, Object id) throws NoSuchFieldException, InstantiationException, ParseException, IllegalAccessException {
        T object = null;

        String idName = TableUtil.getId(clazz);
        // 如果id不存在
        if (TextUtils.isEmpty(idName)) {
            throw new IllegalStateException("object have to have a id column!");
        }

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(TableUtil.getTableName(clazz), null, idName + "=?", new String[]{id.toString()}, null, null, null);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    object = ObjectUtil.buildObject(clazz, cursor);
                }
            } finally {
                cursor.close();
            }
        }
        return object;
    }
}