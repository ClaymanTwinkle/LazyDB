package org.kesar.lazy.lazydb.util;

/**
 * 数据库表操作工具类
 * Created by kesar on 2016/6/21 0021.
 */
public final class TableUtil {
    /**
     * 获取表名
     *
     * @param object 类实例
     * @return 表名
     */
    public static String getTableName(Object object) {
        return getTableName(object.getClass());
    }

    /**
     * 获取表名
     *
     * @param clazz 类
     * @return 表名
     */
    public static String getTableName(Class<?> clazz) {
        return clazz.getName().replace(".", "_");
    }
}
