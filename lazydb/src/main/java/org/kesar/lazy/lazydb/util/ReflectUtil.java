package org.kesar.lazy.lazydb.util;

/**
 * 反射工具类
 * Created by kesar on 2016/6/22 0022.
 */
public final class ReflectUtil
{
    public static <T> T newInstance(Class<T> clazz) throws IllegalAccessException, InstantiationException
    {
        return clazz.newInstance();
    }
}