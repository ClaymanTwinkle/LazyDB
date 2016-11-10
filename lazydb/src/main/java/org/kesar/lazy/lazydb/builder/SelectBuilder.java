package org.kesar.lazy.lazydb.builder;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.kesar.lazy.lazydb.util.ObjectUtil;
import org.kesar.lazy.lazydb.util.TableUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * select sql的构建器
 * Created by kesar on 2016/6/21 0021.
 */
public class SelectBuilder<T>
{
    private SQLiteOpenHelper helper;

    private Class<T> objectClass;
    private String[] columns;
    private String whereSection;
    private String[] whereArgs;

    private String having;
    private String orderBy;
    private String groupBy;
    private String limit;

    public SelectBuilder(SQLiteOpenHelper helper, Class<T> clazz)
    {
        this.objectClass = clazz;
        this.helper = helper;
    }

    public SelectBuilder<T> selectAll()
    {
        return this;
    }

    public SelectBuilder<T> select(String... columns)
    {
        this.columns = columns;
        return this;
    }

    public SelectBuilder<T> where(String whereSection, String[] whereArgs)
    {
        this.whereSection = whereSection;
        this.whereArgs = whereArgs;
        return this;
    }

    public SelectBuilder<T> having(String having)
    {
        this.having = having;
        return this;
    }

    /**
     * [ORDER BY column1, column2, .. columnN] [ASC | DESC];
     * @param orderBy column1 [ASC | DESC], column2 [ASC | DESC],
     * @return SelectBuilder
     */
    public SelectBuilder<T> orderBy(String orderBy)
    {
        this.orderBy = orderBy;
        return this;
    }

    public SelectBuilder<T> groupBy(String groupBy)
    {
        this.groupBy = groupBy;
        return this;
    }

    public SelectBuilder<T> limit(String limit)
    {
        this.limit = limit;
        return this;
    }

    /**
     * 执行查询操作，获取查询结果集
     *
     * @return 查询结果集，空集则查询不到
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws ParseException
     */
    public List<T> execute() throws InstantiationException, IllegalAccessException, NoSuchFieldException, ParseException
    {
        List<T> results = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();

        // 查询表是否存在
        String sql = SqlBuilder.buildQueryTableIsExistSql(objectClass);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    if (cursor.getInt(0) == 0) {
                        return results;
                    }
                }
            } finally {
                cursor.close();
            }
        }

        //String sql = SqlBuilder.buildQuerySql(TableUtil.getTableName(objectClass), columns, whereSection, whereArgs, groupBy, having, orderBy, limit);
        //Cursor cursor = db.rawQuery(sql, null);
        // 执行查询
        cursor=db.query(TableUtil.getTableName(objectClass),columns,whereSection,whereArgs,groupBy,having,orderBy,limit);
        if (cursor != null)
        {
            try
            {
                while (cursor.moveToNext())
                {
                    T object = ObjectUtil.buildObject(objectClass, cursor);
                    results.add(object);
                }
            }
            finally
            {
                cursor.close();
            }
        }
        return results;
    }
}
