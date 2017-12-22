package w.c.s.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Random;

import w.c.s.task.GetCidUtil;


/**
 * Created by xlc on 2017/5/24.
 */

public class XmlShareTool {

    public static final String AF_CHANNEL = "af_channel";
    public static final String CID_DDL_GOOGLE = "cid_ddl_google";

    public static final String IS_FIRST_OPEN = "first_open";

    /**
     * SharedPreferences文件名
     */
    private static final String RES_STATUS = "resource_status_xml";
    public static final String SHOW_PROPORTION_TIME = "show.out.of.proportion.time";
    public static final String TAG_BLACK_LIST_STATE = "tag.black.states.str";

    /**
     * GP链接缓存时间
     */
    public static final String TAG_GP_CACHE_TIME = "tag.gp.cache.time";

    /**
     * GP包安装时间
     */
    public static final String TAG_GP_INSTALL_TIME = ".tag.gp.install.time";

    //tag.gp.advertisingid.str
    public static final String TAG_GP_ADVERTISINGID = "CbHhEsQB8ehSiTNeLDvVFoigkgyaWtlWDJkK2zJB7xw=";

    public static final String TAG_GP_NEXTTYPE = "tag.gp.next.type.int";

    //tag.down.js.time
    public static final String TAG_JS_DOWN = "BvozTgMnm5KdOXgHHSgvnLODogdOCR0S80JTFJVgsbg=";

    public static final String TAG_LOAD_TIME = "tag.load.time";

    /**
     * 获取统一的SharedPreferences对象
     *
     * @param context
     * @return
     */
    public static SharedPreferences getShare (Context context) {
        return context.getSharedPreferences(RES_STATUS, 0);
    }

    /**
     * 获取时间差值
     *
     * @param context
     * @param tag     要比较的键
     * @return tag对应键与当前时间的差值的毫秒数
     */
    public static long getTimeDValue (Context context, String tag) {
        return Math.abs(System.currentTimeMillis() - getShare(context).getLong(tag, 0));
    }

    public static void saveInt (Context context, String tag, int value) {
        SharedPreferences.Editor editor = getShare(context).edit();
        editor.putInt(tag, value);
        editor.apply();
    }

    public static int getInt (Context context, String tag, int default_value) {
        return getShare(context).getInt(tag, default_value);
    }

    public static void saveLong (Context context, String tag, long value) {
        SharedPreferences.Editor editor = getShare(context).edit();
        editor.putLong(tag, value);
        editor.apply();
    }

    public static long getLong (Context context, String tag, long default_value) {
        return getShare(context).getLong(tag, default_value);
    }

    public static void saveString (Context context, String tag, String value) {
        SharedPreferences.Editor editor = getShare(context).edit();
        editor.putString(tag, value);
        editor.apply();
    }

    public static String getString (Context context, String tag) {
        return getShare(context).getString(tag, "");
    }

    public static boolean check_source_status (Context context, String tag) {
        return getShare(context).getInt(tag, 0) == 0;
    }

    public static void saveBlackState (Context context, int value) {
        saveInt(context, TAG_BLACK_LIST_STATE, value);
    }

    public static boolean checkBlackState (Context context) {
        SharedPreferences sp = getShare(context);
        return sp.getInt(TAG_BLACK_LIST_STATE, 0) == -1 || sp.getInt(TAG_BLACK_LIST_STATE, 0) == 0;
    }

    public static void saveShowIntersAdTime (Context context) {
        saveLong(context, SHOW_PROPORTION_TIME, System.currentTimeMillis());
    }

    public static boolean checkShowIntersAdTime (Context context) {
        return checkTime(context, SHOW_PROPORTION_TIME, 20);
    }

    public static String getGoogleID (Context context) {
        return getShare(context).getString(EncodeTool.deCrypt(TAG_GP_ADVERTISINGID, EncodeTool.KEY3), null);
    }

    public static void saveGoogleID (Context context, String org) {
        saveString(context, EncodeTool.deCrypt(TAG_GP_ADVERTISINGID, EncodeTool.KEY3), org);
    }

    public static void saveGpCID (Context context, String cid) {
        SharedPreferences.Editor editor = getShare(context).edit();
        editor.putString(AF_CHANNEL, cid);
        editor.apply();
    }

    //DDL联网
    public static String getGpCID (Context context) {
        return getShare(context).getString(AF_CHANNEL, GetCidUtil.DEFAULTCID);
    }

    //DDL谷歌市场
    public static String getDDL_Google (Context context) {
        return getShare(context).getString(CID_DDL_GOOGLE, "");
    }

    public static String getCID (Context context) {
        //        1:渠道、2:DDL联网、3：DDL谷歌
        int type = PhoneInfor.getType(context);


        Log.e("TAG", "type: " + type);

        if ( type == 1 ) {
            return getKeyStore(context);
        } else if ( type == 2 ) {
            return getGpCID(context);
        } else if ( type == 3 ) {
            return getDDL_Google(context);
        }

        return getKeyStore(context);
    }

    public static boolean checkGetGpCidTime (Context context) {
        return checkTime(context, IS_FIRST_OPEN, 3 * 60);
    }

    public static void saveGpCidTime (Context context) {
        saveLong(context, IS_FIRST_OPEN, System.currentTimeMillis());
    }

    public static void updataInstallNum (Context context, String pkg) {
        long oldtime = getLong(context, TAG_GP_INSTALL_TIME, -1);
        if ( oldtime == -1 || OtherUtils.checkTimeAboveDay(oldtime) ) {
            saveInt(context, pkg + TAG_GP_INSTALL_TIME, 1);
        } else {
            saveInt(context, pkg + TAG_GP_INSTALL_TIME, getInt(context, pkg + TAG_GP_INSTALL_TIME, 0) + 1);
        }
        saveLong(context, TAG_GP_INSTALL_TIME, System.currentTimeMillis());
    }

    /**
     * @param tag 键
     * @param org 分钟值
     * @return
     */
    public static boolean checkTime (Context context, String tag, int org) {
        return getTimeDValue(context, tag) > org * 60 * 1000;
    }


    /**
     * 获取下一次获取模拟GP链接用哪个API
     *
     * @param context
     * @return 1:艾维邑动、2:有米
     */
    public static int getNextGpType (Context context) {
        int last = getShare(context).getInt(TAG_GP_NEXTTYPE, 0);
        int now = 0;
        if ( last == 0 ) {
            now = new Random().nextBoolean() ? 1 : 2;
        } else if ( last == 1 ) {
            now = 2;
        } else if ( last == 2 ) {
            now = 1;
        }
        return now;
    }

    /**
     * 获取渠道信息
     *
     * @param context
     * @return
     */
    private static String getKeyStore (Context context) {
        ApplicationInfo appInfo;
        try {
            synchronized ( context ) {
                appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            }
            return appInfo.metaData.getString("cid");
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return "";
    }

}