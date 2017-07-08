package org.kesar.lazy.lazydb.util;

import android.content.ContentValues;

import org.kesar.lazy.lazydb.annotate.ID;
import org.kesar.lazy.lazydb.domain.DataType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

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

    /**
     * 通过反射机制获取object中属性值，添加到ContentValues中
     *
     * @param object 对象
     * @return ContentValues
     * @throws IllegalAccessException
     */
    public static ContentValues getContentValues(Object object) throws IllegalAccessException {
        ContentValues values = new ContentValues();
        Class<?> clazz = object.getClass();
        Field[] fields = ReflectUtil.getDeclaredFields(clazz);
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {// 移除是final和static的字段
                continue;
            }
            Object value = ReflectUtil.getFieldValue(field, object);
            if (value == null) {
                continue;
            }
            String name = field.getName();

            if (value instanceof String) {
                values.put(name, String.valueOf(value));
            } else if (value instanceof Double
                    || value.getClass() == double.class) {
                values.put(name, Double.valueOf(String.valueOf(value)));
            } else if (value instanceof Float
                    || value.getClass() == float.class) {
                values.put(name, Float.valueOf(String.valueOf(value)));
            } else if (value instanceof Long
                    || value.getClass() == long.class) {
                values.put(name, Long.valueOf(String.valueOf(value)));
            } else if (value instanceof Integer
                    || value.getClass() == int.class) {
                values.put(name, Integer.valueOf(String.valueOf(value)));
            } else if (value instanceof Short
                    || value.getClass() == short.class) {
                values.put(name, Short.valueOf(String.valueOf(value)));
            } else if (value instanceof Byte
                    || value.getClass() == byte.class) {
                values.put(name, Byte.valueOf(String.valueOf(value)));
            } else if (value instanceof Boolean
                    || value.getClass() == boolean.class) {
                values.put(name, Boolean.valueOf(String.valueOf(value)));
            } else if (value instanceof byte[]) {
                values.put(name, String.valueOf(value).getBytes());
            } else if (value instanceof Date) {
                values.put(name, ((Date) value).getTime());
            } else {
                values.put(name, ObjectUtil.object2Byte(value));
            }
        }
        return values;
    }

    /**
     * 通过反射机制获取object中属性值，添加到ContentValues中，除了ID
     *
     * @param object 对象
     * @return ContentValues
     * @throws IllegalAccessException
     */
    public static ContentValues getContentValuesWithOutID(Object object) throws IllegalAccessException {
        ContentValues values = new ContentValues();
        Class<?> clazz = object.getClass();
        Field[] fields = ReflectUtil.getDeclaredFields(clazz);
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {// 移除是final和static的字段
                continue;
            }
            ID id = field.getAnnotation(ID.class);
            if (id != null) {
                continue;
            }

            Object value = ReflectUtil.getFieldValue(field, object);
            if (value == null) {
                continue;
            }

            String name = field.getName();
            if (value instanceof String) {
                values.put(name, String.valueOf(value));
            } else if (value instanceof Double
                    || value.getClass() == double.class) {
                values.put(name, Double.valueOf(String.valueOf(value)));
            } else if (value instanceof Float
                    || value.getClass() == float.class) {
                values.put(name, Float.valueOf(String.valueOf(value)));
            } else if (value instanceof Long
                    || value.getClass() == long.class) {
                values.put(name, Long.valueOf(String.valueOf(value)));
            } else if (value instanceof Integer
                    || value.getClass() == int.class) {
                values.put(name, Integer.valueOf(String.valueOf(value)));
            } else if (value instanceof Short
                    || value.getClass() == short.class) {
                values.put(name, Short.valueOf(String.valueOf(value)));
            } else if (value instanceof Byte
                    || value.getClass() == byte.class) {
                values.put(name, Byte.valueOf(String.valueOf(value)));
            } else if (value instanceof Boolean
                    || value.getClass() == boolean.class) {
                values.put(name, Boolean.valueOf(String.valueOf(value)));
            } else if (value instanceof byte[]) {
                values.put(name, String.valueOf(value).getBytes());
            } else if (value instanceof Date) {
                values.put(name, ((Date) value).getTime());
            } else {
                values.put(name, ObjectUtil.object2Byte(value));
            }
        }
        return values;
    }

    /**
     * 获取id的数据类型
     *
     * @param clazz 类
     * @return 字段的数据类型
     */
    public static DataType getDataType(Class<?> clazz) {
        if (clazz == int.class
                || clazz == Integer.class
                || clazz == byte.class
                || clazz == Byte.class
                || clazz == short.class
                || clazz == Short.class
                || clazz == long.class
                || clazz == Long.class
                || clazz == Boolean.class
                || clazz == boolean.class
                || clazz == Date.class
                ) {
            return DataType.INTEGER;
        } else if (clazz == float.class
                || clazz == Float.class
                || clazz == double.class
                || clazz == Double.class
                ) {
            return DataType.REAL;
        } else if (clazz == String.class) {
            return DataType.TEXT;
        } else {
            return DataType.BLOB;
        }
    }
}
