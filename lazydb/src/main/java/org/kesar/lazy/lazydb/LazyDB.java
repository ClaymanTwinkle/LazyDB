package org.kesar.lazy.lazydb;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.kesar.lazy.lazydb.config.DBConfig;
import org.kesar.lazy.lazydb.config.DeBugLogger;
import org.kesar.lazy.lazydb.core.SQLBuilder;
import org.kesar.lazy.lazydb.core.SQLiteDBExecutor;
import org.kesar.lazy.lazydb.core.SQLiteDBHelper;
import org.kesar.lazy.lazydb.core.SelectBuilder;
import org.kesar.lazy.lazydb.domain.ColumnInfo;
import org.kesar.lazy.lazydb.domain.KeyValue;
import org.kesar.lazy.lazydb.util.FieldUtil;
import org.kesar.lazy.lazydb.util.TableUtil;

import java.text.ParseException;
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

    private SQLiteDBExecutor executor;

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
        SQLiteDBHelper helper = new SQLiteDBHelper(config);
        executor = new SQLiteDBExecutor(helper);
        DeBugLogger.setDebug(config.isDebug());
    }

    /**
     * 创建表
     *
     * @param clazz 类
     */
    public void createTable(Class<?> clazz) throws Exception {
        String sql = SQLBuilder.buildCreateTableSql(clazz);
        executor.execSQL(sql);
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
    public void createTable(String tableName, String idColumn, String idDataType, boolean isAutoIncrement, String... columns) throws Exception {
        String sql = SQLBuilder.buildCreateTableSql(tableName, idColumn, idDataType, isAutoIncrement, columns);
        executor.execSQL(sql);
    }


    /**
     * 删除表
     *
     * @param clazz 类
     */
    public void dropTable(final Class<?> clazz) throws Exception {
        String sql = SQLBuilder.buildDropTableSql(clazz);
        executor.execSQL(sql);
    }

    /**
     * 删除数据库中所有的表
     */
    public void dropAllTables() throws Exception {
        executor.dropAllTables();
    }

    /**
     * 查询所有表名
     *
     * @return 所有表名的集合；若没有表，则是空集合
     */
    public List<String> queryAllTableNames() {
        String sql = SQLBuilder.buildQueryAllTableNamesSql();
        return executor.queryAllTableNames(sql);
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
        String sql = SQLBuilder.buildQueryTableInfoSql(clazz);
        return executor.queryAllColumnsFromTable(sql);
    }

    /**
     * 表是否存在
     *
     * @param clazz 类
     * @return true 表存在，false 表不存在
     */
    public boolean isTableExist(Class<?> clazz) {
        return isTableExist(TableUtil.getTableName(clazz));
    }

    /**
     * 表是否存在
     *
     * @param tableName 表名
     * @return true 表存在，false 表不存在
     */
    public boolean isTableExist(String tableName) {
        String sql = SQLBuilder.buildQueryTableIsExistSql(tableName);
        return executor.isTableExist(sql);
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
        Class<?> clazz = object.getClass();
        if (isTableExist(clazz)) {
            KeyValue idColumn = FieldUtil.getIDColumn(object);
            if (idColumn == null) {
                return false;
            }

            Cursor cursor = query(clazz)
                    .select("count(*)")
                    .where(idColumn.getKey() + "=?", new String[]{idColumn.getValue().toString()})
                    .executeNative();
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
    public void insert(@NonNull Object object) throws Exception {
        final Class<?> clazz = object.getClass();
        // 表不存在，创建表
        if (!isTableExist(clazz)) {
            // 创建表
            createTable(clazz);
        }

        executor.insert(object);
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

        executor.insert(objectList);
    }

    /**
     * 更新对象
     *
     * @param object 对象
     * @throws IllegalAccessException
     */
    public void update(@NonNull final Object object) throws Exception {
        executor.update(object);
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
        executor.delete(object);
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
        executor.delete(objectList);
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
        executor.delete(clazz, whereClause, whereArgs);
    }

    /**
     * 执行查询操作
     *
     * @param clazz 类
     * @return select操作的构建器
     */
    public <T> SelectBuilder<T> query(Class<T> clazz) {
        return executor.query(clazz);
    }

    /**
     * 通过id查询
     *
     * @param clazz   类
     * @param idValue idValue
     * @return object
     * @throws NoSuchFieldException
     * @throws InstantiationException
     * @throws ParseException
     * @throws IllegalAccessException
     */
    public <T> T queryById(Class<T> clazz, Object idValue) throws Exception {
        T object = null;
        String tableName = TableUtil.getTableName(clazz);
        if (isTableExist(tableName)) {
            String idName = FieldUtil.getIdName(clazz);
            // 如果id不存在
            if (TextUtils.isEmpty(idName)) {
                throw new IllegalStateException("object have to have a id column!");
            }

            object = executor.query(clazz)
                    .selectAll()
                    .where(idName + "=?", new String[]{idValue.toString()})
                    .findFirst();
        }
        return object;
    }
}