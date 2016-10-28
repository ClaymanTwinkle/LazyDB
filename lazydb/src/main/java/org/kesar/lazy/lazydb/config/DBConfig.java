package org.kesar.lazy.lazydb.config;

import android.content.Context;

/**
 * 本地数据库的配置
 * Created by kesar on 2016/6/21 0021.
 */
public class DBConfig
{
    private Context mContext;
    private String mDBName = "default.db";
    private int mDBVersion = 1;
    private DBUpgradeListener mUpgradeListener;
    private boolean mDebug;

    public static class Builder
    {
        private Context context;
        private String databaseName;
        private int databaseVersion;
        private DBUpgradeListener upgradeListener;
        private boolean debug;

        public Builder(Context context)
        {
            this.context = context;
        }

        public Builder setDataBaseName(String dbName)
        {
            this.databaseName = dbName;
            return this;
        }

        public Builder setDatabaseVersion(int dbVersion)
        {
            this.databaseVersion = dbVersion;
            return this;
        }

        public Builder setUpgradeListener(DBUpgradeListener upgradeListener)
        {
            this.upgradeListener = upgradeListener;
            return this;
        }

        public Builder setDebug(boolean debug)
        {
            this.debug = debug;
            return this;
        }

        public DBConfig build()
        {
            return new DBConfig(this);
        }
    }

    private DBConfig(Context context)
    {
        this.mContext = context;
    }

    private DBConfig(Builder builder)
    {
        this.mContext = builder.context;
        this.mDBName = builder.databaseName;
        this.mDBVersion = builder.databaseVersion;
        this.mUpgradeListener = builder.upgradeListener;
        this.mDebug =builder.debug;
    }

    public Context getContext()
    {
        return mContext;
    }

    public String getDBName()
    {
        return mDBName;
    }

    public int getDBVersion()
    {
        return mDBVersion;
    }

    public DBUpgradeListener getUpgradeListener()
    {
        return mUpgradeListener;
    }

    public boolean isDebug()
    {
        return mDebug;
    }

    /**
     * get default config
     *
     * @param context context
     * @return default config
     */
    public static DBConfig getDefaultConfig(Context context)
    {
        return new DBConfig(context);
    }
}