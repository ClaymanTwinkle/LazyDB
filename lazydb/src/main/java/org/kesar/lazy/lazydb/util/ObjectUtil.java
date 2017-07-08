package org.kesar.lazy.lazydb.util;

import android.database.Cursor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
     * @throws InstantiationException InstantiationException
     * @throws IllegalAccessException IllegalAccessException
     * @throws NoSuchFieldException   NoSuchFieldException
     * @throws ParseException         NoSuchFieldException
     */
    public static <T> T buildObject(Class<T> objectClass, Cursor cursor) throws InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException {
        T object = ReflectUtil.newInstance(objectClass);
        int columnCount = cursor.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            if (cursor.isNull(i)) {
                continue;
            }
            String columnName = cursor.getColumnName(i);
            Field field = ReflectUtil.getDeclaredField(objectClass, columnName);
            if (field == null) {
                continue;
            }
            Class fieldClass = field.getType();
            if (!field.isAccessible()) {
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
                try {
                    field.setByte(object, Byte.parseByte(value));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
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
                long value = cursor.getLong(i);
                field.set(object, new Date(value));
            } else {
                byte[] value = cursor.getBlob(i);
                field.set(object, byte2Object(value));
            }
        }
        return object;
    }


    public static byte[] object2Byte(Object object) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            return arrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }
                arrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Object byte2Object(byte[] data) {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);
        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(arrayInputStream);
            return inputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                arrayInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
