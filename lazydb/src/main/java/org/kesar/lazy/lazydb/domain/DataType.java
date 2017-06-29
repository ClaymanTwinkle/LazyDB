package org.kesar.lazy.lazydb.domain;

/**
 * SQLite数据类型
 * Created by kesar on 2016/6/21 0021.
 */
public enum DataType
{
    NULL,//这个值为空值

    VARCHAR,//长度不固定且其最大长度为 n 的字串，n不能超过 4000。

    CHAR,//长度固定为n的字串，n不能超过 254。

    INTEGER,//值被标识为整数,依据值的大小可以依次被存储为1,2,3,4,5,6,7,8.

    REAL,//所有值都是浮动的数值,被存储为8字节的IEEE浮动标记序号.

    TEXT,//值为文本字符串,使用数据库编码存储(TUTF-8, UTF-16BE or UTF-16-LE).

    BLOB,//值是BLOB数据块，以输入的数据格式进行存储。如何输入就如何存储,不改变格式。

    DATE,//包含了 年份、月份、日期。

    TIME //包含了 小时、分钟、秒。
}
