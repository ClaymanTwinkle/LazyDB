package org.kesar.lazy.lazydb.domain;

/**
 * 表中列数据
 * Created by kesar on 2016/10/28 0028.
 */
public class ColumnInfo {
    private int cid;
    private String name;
    private String type;
    private int notnull;
    private String dflt_value;
    private int pk;

    public int getCid() {
        return cid;
    }

    public String getDflt_value() {
        return dflt_value;
    }

    public String getName() {
        return name;
    }

    public int getNotnull() {
        return notnull;
    }

    public int getPk() {
        return pk;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ColumnInfo{" +
                "cid=" + cid +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", notnull=" + notnull +
                ", dflt_value='" + dflt_value + '\'' +
                ", pk=" + pk +
                '}';
    }
}