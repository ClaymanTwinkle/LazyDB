package org.kesar.lazy.lazydb.util;

import android.database.Cursor;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;

/**
 * 对象工具类
 * Created by kesar on 2016/6/25 0025.
 */
public final class ObjectUtil {
    /**
     * 构建 对象，class实例化对象，游标获取数据填充对象
     *
     * @param objectClass 对象类
     * @param cursor      游标
     * @return 填充数据后的对象
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws ParseException
     */
    public static <T> T buildObject(Class<T> objectClass, Cursor cursor) throws InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException {
        T object = ReflectUtil.newInstance(objectClass);
        int columnCount = cursor.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            if (cursor.isNull(i)) {
                continue;
            }
            String columnName = cursor.getColumnName(i);
            Field field = objectClass.getDeclaredField(columnName);
            if (field == null) {
                continue;
            }
            Class fieldClass = field.getType();
            if(!field.isAccessible()){
                field.setAccessible(true);
            }
            if (fieldClass == String.class) {
                field.set(object, cursor.getString(i));
            } else if (fieldClass == double.class
                    || fieldClass == Double.class) {
                field.setDouble(object, cursor.getDouble(i));
            } else if (fieldClass == float.class
                    || fieldClass == Float.class) {
                field.setFloat(object, cursor.getFloat(i));
            } else if (fieldClass == Long.class
                    || fieldClass == long.class) {
                field.setLong(object, cursor.getLong(i));
            } else if (fieldClass == Integer.class
                    || fieldClass == int.class) {
                field.setInt(object, cursor.getInt(i));
            } else if (fieldClass == byte.class
                    || fieldClass == Byte.class) {
                String value = cursor.getString(i);
                field.setByte(object, Byte.parseByte(value));
            } else if (fieldClass == Short.class
                    || fieldClass == short.class) {
                short value = cursor.getShort(i);
                field.setShort(object, value);
            } else if (fieldClass == byte[].class) {
                byte[] value = cursor.getBlob(i);
                field.set(object, value);
            } else if (fieldClass == Boolean.class
                    || fieldClass == boolean.class) {
                int value = cursor.getInt(i);
                field.setBoolean(object, value != 0);
            } else if (fieldClass == Date.class) {
                String value = cursor.getString(i);
                field.set(object, DateUtil.string2Date(value));
            }
            else{
                // TODO: 16-11-11 这里如果不是基本类型的话，可以拓展到其他对象的递归构建，所以这个方法将不能放在这个类中
            }
        }
        return object;
    }
}
