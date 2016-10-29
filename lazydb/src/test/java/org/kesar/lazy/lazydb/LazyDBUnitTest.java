package org.kesar.lazy.lazydb;

import android.app.Application;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kesar.lazy.lazydb.util.DateUtil;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", sdk = 16)
public class LazyDBUnitTest
{
    LazyDB lazyDB;
    long start_time;

    @Before
    public void setUp()
    {
        Application application = RuntimeEnvironment.application;
        lazyDB = LazyDB.create(application);
        start_time = System.currentTimeMillis();
    }

    @Test
    public void testAll() throws Exception
    {
        createTable();
        queryAllTableNames();
        dropTable();
        insertObject();
        updateObject();
        deleteObject();
        insertOrUpdateObject();
        queryAllObject();
        deleteAllObject();
        queryObject();
        queryById();
    }

    @Test
    public void createTable() throws Exception
    {
        lazyDB.createTable(Entity.class);
        boolean result = lazyDB.isTableExist(Entity.class);
        print(result ? "创建表成功" : "创建表失败");
    }

    @Test
    public void queryAllTableNames() throws Exception
    {
        List<String> list = lazyDB.queryAllTableNames();
        print("查询所有表：" + list.toString());
    }

    @Test
    public void dropTable() throws Exception
    {
        lazyDB.dropTable(Entity.class);
        boolean result = lazyDB.isTableExist(Entity.class);
        print(result ? "删除表失败" : "删除表成功");
    }

    @Test
    public void insertObject() throws Exception
    {
        Entity entity = createEntity();
        lazyDB.insert(entity);
        boolean result = lazyDB.isObjectExist(entity);
        print(result ? "插入数据成功" : "插入数据失败");
    }

    @Test
    public void insertManyObject() throws Exception
    {
        List<Entity> entities = new ArrayList<>();
        for (int i = 0; i < 100; i++)
        {
            Entity entity = createEntity();
            entities.add(entity);
        }
        lazyDB.insert(entities);
        queryAllObject();
    }

    @Test
    public void updateObject() throws Exception
    {
        Entity entity = createEntity();
        lazyDB.insert(entity);
        queryAllObject();
        entity.setName("修改后");
        entity.setMoney(100);
        entity.setBirthday(new Date());
        entity.setSex(true);
        lazyDB.update(entity);
        queryAllObject();
    }

    @Test
    public void insertOrUpdateObject() throws Exception
    {
        Entity entity = createEntity();
        lazyDB.insertOrUpdate(entity);
        queryAllObject();
        entity.setName("修改后");
        entity.setMoney(100);
        entity.setBirthday(new Date());
        entity.setSex(true);
        lazyDB.insertOrUpdate(entity);
        queryAllObject();
    }

    @Test
    public void deleteObject() throws Exception
    {
        Entity entity = createEntity();
        lazyDB.insert(entity);
        queryAllObject();
        lazyDB.delete(entity);
        queryAllObject();
    }

    @Test
    public void deleteAllObject() throws Exception
    {
        insertManyObject();
        lazyDB.delete(Entity.class, null, null);
        queryAllObject();
    }

    @Test
    public void queryObject() throws Exception
    {
        Entity entity = createEntity();
        lazyDB.insert(entity);
        List<Entity> entities = lazyDB
                .query(Entity.class)
                .selectAll()
                .where("id=? and name=? and age=? and birthday=? and sex=? and money=?"
                        , new String[]{
                                entity.getId(),
                                entity.getName(),
                                entity.getAge() + "",
                                DateUtil.date2String(entity.getBirthday()),
                                entity.isSex() ? "1" : "0",
                                Double.toString(entity.getMoney())
                        }
                )
                .execute();
        print(entities.toString());
    }

    @Test
    public void queryAllObject() throws Exception
    {
        List<Entity> entities = lazyDB.query(Entity.class).selectAll().execute();
        print(entities.toString());
    }

    @Test
    public void queryById() throws Exception
    {
        Entity entity=createEntity();
        lazyDB.insert(entity);
        lazyDB.queryById(entity.getClass(),entity.getId());
    }

    @After
    public void setDown()
    {
        double cost_time = System.currentTimeMillis() - start_time;
        print("========================================================");
        print("耗时：" + cost_time + "ms");
    }

    private Entity createEntity()
    {
        Entity entity = new Entity();
        entity.setId(UUID.randomUUID().toString());
        entity.setAge(88);
        entity.setBirthday(new Date());
        entity.setMoney(66.66);
        entity.setName("哈哈");
        entity.setSex(false);
        return entity;
    }

    public void print(String s)
    {
        System.err.println(s);
    }
}