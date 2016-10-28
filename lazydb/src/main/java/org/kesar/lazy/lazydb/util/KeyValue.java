package org.kesar.lazy.lazydb.util;

/**
 * 属性
 * Created by kesar on 2016/6/23 0023.
 */
public class KeyValue
{
    private String key;
    private Object value;

    public String getKey()
    {
        return key;
    }

    public void setKey(String name)
    {
        this.key = name;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "KeyValue{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}