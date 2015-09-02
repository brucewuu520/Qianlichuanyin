package com.brucewuu.android.qlcy.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.brucewuu.android.qlcy.AppContext;


/**
 * 创建数据库表
 *
 * @author Ares
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper dbHelper;
    private static final String DB_NAME = "qlcy_db";
    private static final int VERSION = 1;

    private SQLiteDatabase mDatabase;


    public static DBHelper getInstance() {
        if (dbHelper == null) {
            synchronized (DBHelper.class) {
                if (dbHelper == null)
                    dbHelper = new DBHelper(AppContext.getInstance());
            }
        }

        return dbHelper;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createLauncherDB(db);
    }

    private static void createLauncherDB(SQLiteDatabase db) {
        String sql = new StringBuilder()
                .append("CREATE TABLE [friends] (")
                .append("[id] TEXT, ")
                .append("[phone] TEXT, ")
                .append("[imtoken] TEXT, ")
                .append("[nickname] TEXT, ")
                .append("[portraituri] TEXT)").toString();
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 打开数据库
     *
     * @return
     */
    public synchronized SQLiteDatabase openDatabase() {
        if (mDatabase != null && mDatabase.isOpen()) {
            return mDatabase;
        }
        // Opening new database
        mDatabase = dbHelper.getWritableDatabase();

        return mDatabase;
    }

    /**
     * 关闭数据库
     */
    public synchronized void closeDatabase() {
        try {
            if (mDatabase != null)
                mDatabase.close();
            mDatabase = null;
        } catch (Throwable e) {
        }
    }

}
