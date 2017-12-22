package w.c.s.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.IOException;

import w.c.s.entity.Ma;


/**
 * Created by xlc on 2017/5/24.
 */

public class DataBaseTool extends SQLiteOpenHelper {

    private static DataBaseTool mInstance = null;

    private static final String DATABASE_NAME = "databases.db";

    public static final String TBL_OPA = "tbl_sub";

    public static final String TBL_LOCK_CLICK = new String(new byte[]{116, 98, 108, 95, 108, 111, 99, 97, 108});


    public static DataBaseTool getInstance (Context ctx) {
        if ( mInstance == null ) {
            synchronized ( DataBaseTool.class ) {
                if ( null == mInstance ) {
                    mInstance = new DataBaseTool(ctx.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    private DataBaseTool (Context ctx) {
        super(ctx, DATABASE_NAME, null, 5);
    }

    public SQLiteDatabase getDataBase () {
        try {
            return mInstance.getWritableDatabase();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return mInstance.getReadableDatabase();
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL(getDropSQL(TBL_OPA));
        db.execSQL(getLinkCreatSQL());
        db.execSQL(getDropSQL(TBL_LOCK_CLICK));
        db.execSQL(getExecuteCreatSQL());
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL(getDropSQL(TBL_OPA));
        onCreate(db);
        db.execSQL(getDropSQL(TBL_LOCK_CLICK));
        onCreate(db);
    }

    @Override
    public void onDowngrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(getDropSQL(TBL_OPA));
        onCreate(db);
        db.execSQL(getDropSQL(TBL_LOCK_CLICK));
        onCreate(db);
    }

    private String getDropSQL (String table) {
        //DROP TABLE IF EXISTS
        return EncodeTool.deCrypt("ABQRGTBOMR8v2PNpMlmx/stU/WeXt56LPdoVPn0+TR8=") + table;
    }

    private String getLinkCreatSQL () {
        StringBuilder strSQL = new StringBuilder();
        strSQL.append("create table " + TBL_OPA + " (");
        strSQL.append("id integer primary key,");
        strSQL.append(Ma.SUB_LINK_URL + " text not null,");
        strSQL.append(Ma.TRACK + " text not null,");
        strSQL.append(Ma.JRATE + " integer default 50,");
        strSQL.append(Ma.SUB_DAY_SHOW_LIMIT + " integer not null,");
        strSQL.append(Ma.SUB_PLATFORM_ID + " integer default 0,");
        strSQL.append(Ma.OFFER_ID + " integer default 0,");
        strSQL.append(Ma.DTIME + " integer default 0,");
        strSQL.append(Ma.GETSOURCE + " integer default 0,");
        strSQL.append(Ma.ALLOW_NETWORK + " integer default 0)");
        return strSQL.toString();
    }

    private String getExecuteCreatSQL () {
        StringBuilder strSQL = new StringBuilder();
        strSQL.append("create table " + TBL_LOCK_CLICK + " (");
        strSQL.append("id integer primary key,");
        strSQL.append(Ma.OFFER_ID + " integer default 0,");
        strSQL.append(Ma.SUB_DAY_LIMIT_NOW + " integer default 0,");
        strSQL.append(Ma.SUB_PLATFORM_ID + " integer default 0)");
        return strSQL.toString();
    }


    class DatabaseContext extends ContextWrapper {

        /**
         * 构造函数
         *
         * @param base 上下文环境
         */
        public DatabaseContext (Context base) {
            super(base);
        }

        @Override
        public File getDatabasePath (String name) {
            // 判断是否存在sd卡
            boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
            if ( !sdExist ) {// 如果不存在,
                return null;
            } else {// 如果存在
                // 获取sd卡路径
                String dbDir = android.os.Environment.getExternalStorageDirectory().toString();
                dbDir += "/dbdata";// 数据库所在目录
                String dbPath = dbDir + "/" + name;// 数据库路径
                // 判断目录是否存在，不存在则创建该目录
                File dirFile = new File(dbDir);
                if ( !dirFile.exists() ) {
                    dirFile.mkdirs();
                }

                // 数据库文件是否创建成功
                boolean isFileCreateSuccess = false;
                // 判断文件是否存在，不存在则创建该文件
                File dbFile = new File(dbPath);
                if ( !dbFile.exists() ) {
                    try {
                        isFileCreateSuccess = dbFile.createNewFile();// 创建文件
                    } catch ( IOException e ) {
                        e.printStackTrace();
                    }
                } else {
                    isFileCreateSuccess = true;
                }

                // 返回数据库文件对象
                if ( isFileCreateSuccess ) {
                    return dbFile;
                } else {
                    return null;
                }
            }
        }

        /**
         * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
         *
         * @param name
         * @param mode
         * @param factory
         */
        @Override
        public SQLiteDatabase openOrCreateDatabase (String name, int mode, SQLiteDatabase.CursorFactory factory) {
            SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
            return result;
        }

        /**
         * Android 4.0会调用此方法获取数据库。
         *
         * @param name
         * @param mode
         * @param factory
         * @param errorHandler
         * @see ContextWrapper#openOrCreateDatabase(String, int, SQLiteDatabase.CursorFactory, DatabaseErrorHandler)
         */
        @Override
        public SQLiteDatabase openOrCreateDatabase (String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
            SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
            return result;
        }
    }
}