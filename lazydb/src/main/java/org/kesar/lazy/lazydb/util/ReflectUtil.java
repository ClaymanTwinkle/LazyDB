package org.kesar.lazy.lazydb.util;

import java.lang.reflect.Field;

/**
 * 反射工具类，统一管理反射
 * Created by kesar on 2016/6/22 0022.
 */
public final class ReflectUtil
{
    public static <T> T newInstance(Class<T> clazz) throws IllegalAccessException, InstantiationException
    {
        return clazz.newInstance();
    }

    public static Field[] getDeclaredFields(Class<?> clazz){
        return clazz.getDeclaredFields();
    }
}