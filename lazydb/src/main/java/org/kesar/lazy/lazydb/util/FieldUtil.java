package org.kesar.lazy.lazydb.util;

import android.content.ContentValues;

import org.kesar.lazy.lazydb.annotate.ID;
import org.kesar.lazy.lazydb.annotate.Ignore;
import org.kesar.lazy.lazydb.domain.DataType;
import org.kesar.lazy.lazydb.domain.KeyValue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;

/**
 * Field 工具类
 * Created by kesar on 16-11-11.
 */

public class FieldUtil {
    private final static String ID = "id";

    public static boolean isIgnoreField(Field field) {
        return Modifier.isStatic(field.getModifiers()) // 移除是final和static的字段
                || Modifier.isFinal(field.getModifiers())
                || field.getAnnotation(Ignore.class) != null;
    }

    /**
     * 获取id 字段
     *
     * @param clazz class
     * @return null is not found
     */
    public static Field getIDField(Class<?> clazz) {
        return getIDField(ReflectUtil.getDeclaredFields(clazz));
    }

    /**
     * 获取id 字段
     *
     * @param fields class.getDeclaredFields()
     * @return null is not found
     */
    public static Field getIDField(Field[] fields) {
        Field idField = null;
        if (fields != null && fields.length != 0) {
            // 1. 寻找id注解的field，并检查id注解是否只有一个
            for (Field field : fields) {
                if (isIgnoreField(field)) {
                    continue;
                }
                ID id = field.getAnnotation(ID.class);
                if (id != null) {
                    if (idField == null) {
                        idField = field;
                    } else {
                        throw new IllegalStateException("a class must have only one ID annotation");
                    }
                }
            }
            // 2. 没有id注解，只能用field名来查找
            if (idField == null) {
                for (Field field : fields) {
                    if (isIgnoreField(field)) {
                        continue;
                    }
                    String name = field.getName();
                    if (ID.equals(name)) {
                        return field;
                    }
                }
            }
        }
        return idField;
    }

    /**
     * 获取id String
     *
     * @param objectClass 类
     * @return id String
     * @throws IllegalAccessException IllegalAccessException
     */
    public static String getIdName(Class objectClass) throws IllegalAccessException {
        String name = null;
        Field field = FieldUtil.getIDField(objectClass);
        if (field != null) {
            name = field.getName();
            ID id = field.getAnnotation(ID.class);
            if (id != null && !"".equals(id.column())) {
                name = id.column();
            }
        }
        return name;
    }

    /**
     * 获取id的属性名和属性值
     *
     * @param object 对象
     * @return 属性（名字和值）
     * @throws IllegalAccessException IllegalAccessException
     */
    public static KeyValue getIDColumn(Object object) throws IllegalAccessException {
        Class objectClass = object.getClass();
        Field[] fields = ReflectUtil.getDeclaredFields(objectClass);
        Field field = FieldUtil.getIDField(fields);
        // 1. 判断null
        if (field != null) {
            ID id = field.getAnnotation(ID.class);
            String name = field.getName();
            // 判断是否有ID Annotation
            if (id != null && !"".equals(id.column())) {
                name = id.column();
            }
            Object value = ReflectUtil.getFieldValue(field, object);
            KeyValue column = new KeyValue();
            column.setKey(name);
            column.setValue(value);
            return column;
        }
        return null;
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
            if (isIgnoreField(field)) {
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
            if (isIgnoreField(field)) {
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