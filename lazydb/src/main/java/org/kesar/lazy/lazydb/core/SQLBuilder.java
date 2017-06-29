package org.kesar.lazy.lazydb.core;

import android.text.TextUtils;

import org.kesar.lazy.lazydb.annotate.ID;
import org.kesar.lazy.lazydb.config.DeBugLogger;
import org.kesar.lazy.lazydb.domain.DataType;
import org.kesar.lazy.lazydb.util.IDUtil;
import org.kesar.lazy.lazydb.util.ReflectUtil;
import org.kesar.lazy.lazydb.util.TableUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * sql语句生成
 * Created by kesar on 2016/6/21 0021.
 */
public final class SQLBuilder {
    private final static String SQL_Query_All_TABLE_Names = "select name from sqlite_master where type='table' AND name!='sqlite_sequence' AND name!='android_metadata'"; // 查询所有表名的sql语句
    private final static String SQL_Query_All_TABLE_Count = "select count(*) from sqlite_master where type='table' AND name!='sqlite_sequence' AND name!='android_metadata'";

    /**
     * 构建创建数据库表的sql语句
     *
     * @param clazz 类
     * @return 创建表的sql语句
     */
    public static String buildCreateTableSql(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();

        Field[] fields = ReflectUtil.getDeclaredFields(clazz);
        if (fields == null || fields.length == 0) {
            throw new IllegalStateException("class'fields can not be empty");
        }

        // 开头
        sb.append("create table ")
                .append(TableUtil.getTableName(clazz)) // tableName
                .append("(");
        // 找到主键
        Field idField = IDUtil.getIDField(fields);
        if (idField != null) {
            String idColumn = idField.getName();
            ID id = idField.getAnnotation(ID.class);
            // 判断是有没有注解的
            if (id != null && !"".equals(id.column())) {
                idColumn = id.column();
            }
            DataType dataType = TableUtil.getDataType(idField.getType());
            sb.append(idColumn)
                    .append(" ")
                    .append(dataType.toString())
                    .append(" primary key")
                    .append(",");
        }
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {// 移除是final和static的字段
                continue;
            }
            // 让不是id的field进来
            if (field != idField) {
                sb.append(field.getName())
                        .append(" ")
                        .append(TableUtil.getDataType(field.getType()))
                        .append(",");
            }
        }
        // 除去最后一个逗号
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");
        String sql = sb.toString();
        // debug log
        DeBugLogger.d("CreateTableSql", sql);
        return sql;
    }

    /**
     * 构建创建数据库表的sql语句
     *
     * @param tableName       表名
     * @param idColumn        主键的字段名
     * @param idDataType      主键的数据类型
     * @param isAutoIncrement 是否设置主键自动增长
     * @param columns         其他字段，除了id
     * @return 创建表的sql语句
     */
    public static String buildCreateTableSql(String tableName, String idColumn, String idDataType, boolean isAutoIncrement, String... columns) {
        StringBuilder sb = new StringBuilder();

        int commaCount = columns.length; // 需要加的逗号个数

        // 开头
        sb.append("create table ").append(tableName).append("(");
        // 找到主键
        if (idColumn != null) {
            sb.append(idColumn).append(" ");
            if (idDataType != null) {
                sb.append(idDataType);
            }
            sb.append(" primary key");
            if (isAutoIncrement) {
                sb.append(" autoincrement");
            }
            if (commaCount != 0) {
                commaCount--;
                sb.append(",");
            }
        } else {
            commaCount--;
        }
        // 处理其他元素
        for (String f : columns) {
            sb.append(f);
            if (commaCount != 0) {
                commaCount--;
                sb.append(",");
            }
        }
        sb.append(")");

        String sql = sb.toString();
        // debug log
        DeBugLogger.d("CreateTableSql", sql);
        return sql;
    }

    /**
     * 构建 删除表的sql语句
     *
     * @param clazz 类
     * @return 删除表的sql语句
     */
    public static String buildDropTableSql(Class<?> clazz) {
        return buildDropTableSql(TableUtil.getTableName(clazz));
    }

    /**
     * 构建 删除表的sql语句
     *
     * @param tableName 表名
     * @return 删除表的sql语句
     */
    public static String buildDropTableSql(String tableName) {
        String sql = "drop table if exists "
                + tableName;
        // debug log
        DeBugLogger.d("DropTableSql", sql);
        return sql;
    }

    /**
     * 构建 查询所有表名的sql语句
     *
     * @return 所有表名的sql查询语句
     */
    public static String buildQueryAllTableNamesSql() {
        String sql = SQL_Query_All_TABLE_Names;
        // debug log
        DeBugLogger.d("QueryAllTableNamesSql", sql);
        return sql;
    }

    /**
     * 构建 查询表数目的sql语句
     *
     * @return 查询表数目的sql语句
     */
    public static String buildQueryAllTableCountSql() {
        String sql = SQL_Query_All_TABLE_Count;
        // debug log
        DeBugLogger.d("QueryAllTableCountSql", sql);
        return sql;
    }

    /**
     * 构建 判断表是否存在的sql语句
     *
     * @param clazz 类
     * @return 判断表是否存在的sql语句
     */
    public static String buildQueryTableIsExistSql(Class<?> clazz) {
        String sql = buildQueryTableIsExistSql(TableUtil.getTableName(clazz));
        // debug log
        DeBugLogger.d("QueryTableIsExistSql", sql);
        return sql;
    }

    /**
     * 构建 判断表是否存在的sql语句
     *
     * @param tableName 表名
     * @return 判断表是否存在的sql语句
     */
    public static String buildQueryTableIsExistSql(String tableName) {
        String sql = buildQueryAllTableCountSql()
                + " AND name='"
                + tableName
                + "'";
        // debug log
        DeBugLogger.d("QueryTableIsExistSql", sql);
        return sql;
    }

    /**
     * 构建查询语句
     *
     * @param table         表名
     * @param columns       查询出的参数
     * @param selection     相当于where后面的字符串，可以使用?
     * @param selectionArgs 替代selection的?
     * @param groupBy       group by后面的字符串
     * @param having        having后面的字符串
     * @param orderBy       order by后面的字符串
     * @param limit         limit后面的字符串
     * @return 查询用的sql
     */
    public static String buildQuerySql(String table, String[] columns, String selection,
                                       String[] selectionArgs, String groupBy, String having,
                                       String orderBy, String limit) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        if (columns == null) {
            sb.append("*");
        } else {
            int commaCount = columns.length - 1;
            for (String column : columns) {
                sb.append(column);
                if (commaCount != 0) {
                    sb.append(",");
                    commaCount--;
                }
            }
        }
        sb.append(" from '").append(table).append("'");

        if (!TextUtils.isEmpty(selection)) {
            String whereStr = selection;
            if (selectionArgs != null) {
                for (String arg : selectionArgs) {
                    whereStr = whereStr.replaceFirst("\\?", arg);
                }
            }
            sb.append(" where ").append(whereStr);
        }
        if (!TextUtils.isEmpty(groupBy)) {
            sb.append(" group by ").append(groupBy);
        }
        if (!TextUtils.isEmpty(having)) {
            sb.append(" having ").append(having);
        }
        if (!TextUtils.isEmpty(orderBy)) {
            sb.append(" order by ").append(orderBy);
        }
        if (!TextUtils.isEmpty(limit)) {
            sb.append(" limit ").append(limit);
        }
        String sql = sb.toString();
        // debug log
        DeBugLogger.d("QuerySql", sql);
        return sql;
    }

    /**
     * 构建查询表中属性的sql
     *
     * @param clazz class
     * @return sql
     */
    public static String buildQueryTableInfoSql(Class<?> clazz) {
        return "PRAGMA table_info(" +
                TableUtil.getTableName(clazz) +
                ")";
    }
}