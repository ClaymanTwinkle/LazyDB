package org.kesar.lazy.lazydb;

import org.junit.Test;

import java.lang.reflect.Field;

/**
 *
 * Created by kesar on 2016/10/28 0028.
 */
public class ClassTest {
    @Test
    public void test() {
        Field[] fields = Entity.class.getDeclaredFields();
        for (Field field : fields) {
            System.err.println(field.getName());
        }
    }
}
