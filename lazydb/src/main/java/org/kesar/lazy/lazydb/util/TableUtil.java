package org.kesar.lazy.lazydb.util;

import android.content.ContentValues;

import org.kesar.lazy.lazydb.annotate.ID;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

import static android.R.attr.name;

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
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {// 移除是final和static的字段
                continue;
            }
            String name = field.getName();
            field.setAccessible(true);
            Object value = field.get(object);

            if (value == null) {
                continue;
            }

            if (value instanceof String) {
                values.put(name, String.valueOf(value));
            } else if (value instanceof Date) {
                String dateString = DateUtil.date2String((Date) value);
                values.put(name, dateString);
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
            } else {
                // TODO: 2016/6/22 0022 处理其他数据类型
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
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {// 移除是final和static的字段
                continue;
            }
            ID id = field.getAnnotation(ID.class);
            if (id != null) {
                continue;
            }
            String name = field.getName();
            field.setAccessible(true);
            Object value = field.get(object);

            if (value == null) {
                continue;
            }

            if (value instanceof String) {
                values.put(name, String.valueOf(value));
            } else if (value instanceof Date) {
                String dateString = DateUtil.date2String((Date) value);
                values.put(name, dateString);
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
            } else {
                // TODO: 2016/6/22 0022 处理其他数据类型
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
                ) {
            return DataType.INTEGER;
        } else if (clazz == float.class
                || clazz == Float.class
                || clazz == double.class
                || clazz == Double.class
                ) {
            return DataType.REAL;
        } else if (clazz == Date.class) {
            return DataType.DATE;
        }
        return DataType.TEXT;
    }

    /**
     * 获取id String
     *
     * @param objectClass 类
     * @return id String
     * @throws IllegalAccessException
     */
    public static String getId(Class objectClass) throws IllegalAccessException {
        Field[] fields = objectClass.getDeclaredFields();
        // 1. 从注解中抽取
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {// 移除是final和static的字段
                continue;
            }
            ID id = field.getAnnotation(ID.class);
            if (id != null) {
                return "".equals(id.column()) ? field.getName() : id.column();
            }
        }
        // 2. 从field name 中抽取
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {// 移除是final和static的字段
                continue;
            }
            String name = field.getName();
            if ("id".equals(name)) {
                return name;
            }
        }
        return null;
    }

    /**
     * 获取id的属性名和属性值
     *
     * @param object 对象
     * @return 属性（名字和值）
     * @throws IllegalAccessException
     */
    public static KeyValue getIDColumn(Object object) throws IllegalAccessException {
        Class objectClass = object.getClass();
        Field[] fields = objectClass.getDeclaredFields();
        // 1. 从注解中抽取
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {// 移除是final和static的字段
                continue;
            }
            ID id = field.getAnnotation(ID.class);
            if (id != null) {
                String name = "".equals(id.column()) ? field.getName() : id.column();
                field.setAccessible(true);
                Object value = field.get(object);
                KeyValue column = new KeyValue();
                column.setKey(name);
                column.setValue(value);
                return column;
            }
        }
        // 2. 从field name 中抽取
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {// 移除是final和static的字段
                continue;
            }
            String name = field.getName();
            if ("id".equals(name)) {
                field.setAccessible(true);
                Object value = field.get(object);
                KeyValue column = new KeyValue();
                column.setKey(name);
                column.setValue(value);
                return column;
            }
        }
        return null;
    }
}
