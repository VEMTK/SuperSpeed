package superclean.solution.com.superspeed.view;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by hwl on 2017/10/13.
 */

public class XmlShareUtil {

    //SharedPreferences文件名称
    private static final String XML_SHARE_NAME = "android.easyclean.share.name";
    //电池信息
    public static final String TAG_BATTERY_INFOR = "tag.easyclean.battery.infors";
    //GPU厂商
    public static final String TAG_GPU_FIRM = "tag.easybox.gpu.firm";
    //GPU类型
    public static final String TAG_GPU_TYPE = "tag.easybox.gpu.type";
    //电量
    public static final String TAG_INFOR_POWER_LEVEL = "tag.easybox.power.infor.level";
    //电池温度
    public static final String TAG_INFOR_POWER_TEMP = "tag.easybox.power.infor.temp";
    //降温间隔时间
    public static final String TAG_CPUTEMP_TIME = "tag.easybox.temp.time";
    //通知栏快捷工具
    public static final String TAG_SET_NOTIFICTOOL = "tag.easybox.notific.tool.boolean";
    //CPU使用提醒
    public static final String TAG_SET_CPUNOTIFIC = "tag.easybox.cpu.notific.boolean";
    //内存使用提醒
    public static final String TAG_SET_RAMNOTIFIC = "tag.easybox.ram.notific.boolean";
    //上次内存使用提醒时间
    public static final String TAG_SET_RAMNOTIFIC_TIME = "tag.easybox.ram.notific.time.long";

    //智能锁屏
    public static final String TAG_SET_LOCKSCREEN = "tag.easybox.lock.screen.boolean";
    //垃圾清理提醒频率（0:每天、1:每3天、2:每7天、3:不提醒）
    public static final String TAG_CACHE_FREQ = "tag.easybox.cache.freq.int";
    //垃圾清理提醒
    public static final String TAG_CACHE_NOTIFIC = "tag.easybox.cache.notific.boolean";
    //上次垃圾清理提醒时间
    public static final String TAG_CACHE_NOTIFIC_TIME = "tag.easybox.cache.notific.time.long";
    //侧边栏点击时间
    public static final String TAG_SIDERBAR_TIME = "tag.easybox.sider.click.time";
    //内存加速白名单，应用包名
    public static final String TAG_MEMORY_WHITE_LIST_PKG = "tag.easybox.white.list.pkgname.str";

    public static final String TAG_TOTAL_ROM_SIZE = "tag.easybox.total.rom.str";
    public static final String TAG_AVAIL_ROM_SIZE = "tag.easybox.avail.rom.str";
    public static final String TAG_TOTAL_RAM_SIZE = "tag.easybox.total.ram.str";
    public static final String TAG_AVAIL_RAM_SIZE = "tag.easybox.avail.ram.str";
    public static final String TAG_CPU_TEMP = "tag.easybox.cpu.temp.int";

    //密码错误时间
    public static final String TAG_LOCK_TIME = "tag.easybox.lock.pass.miss.time";
    //锁屏获取位置时间
    public static final String TAG_LOCATION_TIME = "tag.easybox.location.execute.time";
    //锁屏位置详细地址
    public static final String TAG_LOCATION_DETAILNAME = "tag.easybox.location.detail.name.str";
    //锁屏位置城市名称
    public static final String TAG_LOCATION_CITYNAME = "tag.easybox.location.city.name.str";
    //内存加速时间
    public static final String TAG_MEMORY_CLEAN_TIME = "tag.easybox.memory.clean.time";

    public static final String TAG_WEATHER_LOW = "tag.easybox.weather.temp.low";
    public static final String TAG_WEATHER_HEIGHT = "tag.easybox.weather.temp.height";
    public static final String TAG_WEATHER_CODE = "tag.easybox.weather.code.str";
    public static final String CURRENT_TEMP = "tag.easybox.current.temp.str";
    //保存最后查询地址的woeid
    public static final String TAG_LOCATION_CODE = "tag.easybox.location.code.str";
    // 记录用户是否手动设置地址 0没，1有
    public static final String USER_SER_LOACTION = "user_set_locatio";
    //应用锁的场景    0:锁屏后自动加锁、1:退出APP后自动加锁
    public static final String TAG_APPLOCK_SCENARIOS = "tag.easybox.scenarios.type";
    //应用在本次锁屏是否解锁过
    public static final String TAG_APPLOCK_STAUES = "tag.easyboox.app.lock.statues.";
    //可以显示Toast
    public static final String TAG_CANSHOW_WINDOW = "tag.easybox.window.show.boolean";
    //垃圾清理文件总数
    public static final String TAG_FILE_NUM = "tag.easybox.file.num.int";


    public static SharedPreferences getShare (Context context) {
        return context.getSharedPreferences(XML_SHARE_NAME, 0);
    }

    public static void saveString (Context context, String tag, String value) {
        SharedPreferences.Editor editor = getShare(context).edit();
        editor.putString(tag, value);
        editor.apply();
    }

    public static void saveLong (Context context, String tag, long value) {
        SharedPreferences.Editor editor = getShare(context).edit();
        editor.putLong(tag, value);
        editor.apply();
    }

    public static void saveInt (Context context, String tag, int value) {
        SharedPreferences.Editor editor = getShare(context).edit();
        editor.putInt(tag, value);
        editor.apply();
    }

    public static void saveBoolean (Context context, String tag, boolean value) {
        SharedPreferences.Editor editor = getShare(context).edit();
        editor.putBoolean(tag, value);
        editor.apply();
    }

    public static void saveSystemTime (Context context, String tag) {
        saveLong(context, tag, System.currentTimeMillis());
    }

    public static String getString (Context context, String tag) {
        return getString(context, tag, "");
    }

    public static String getString (Context context, String tag, String def) {
        return getShare(context).getString(tag, def);
    }

    public static long getLong (Context context, String tag) {
        return getShare(context).getLong(tag, 0);
    }

    public static int getInt (Context context, String tag) {
        return getIntDefault(context, tag, 0);
    }

    public static int getIntDefault (Context context, String tag, int defaul) {
        return getShare(context).getInt(tag, defaul);
    }

    public static boolean getBoolean (Context context, String tag) {
        return getBoolean(context, tag, true);
    }

    public static boolean getBoolean (Context context, String tag, boolean value) {
        return getShare(context).getBoolean(tag, value);
    }

    public static void saveSharedInfor (Context context, Map<String, Long> data) {
        SharedPreferences.Editor editor = getShare(context).edit();
        for ( Map.Entry<String, Long> entry : data.entrySet() ) {
            editor.putLong(entry.getKey(), entry.getValue());
        }
        editor.commit();
    }

    /**
     * 以秒为单位比较时间
     */
    public static boolean checkTimeSecond (Context context, String tag, long time) {
        return Math.abs(getLong(context, tag) - System.currentTimeMillis()) >= time * 1000;
    }

    /**
     * 以分为单位比较时间
     */
    public static boolean checkTimeMinute (Context context, String tag, long time) {
        return checkTimeSecond(context, tag, time * 60);
    }

    /**
     * 以小时为单位比较时间
     */
    public static boolean checkTimeHour (Context context, String tag, long time) {
        return checkTimeMinute(context, tag, time * 60);
    }

    /**
     * 以天为单位比较时间
     */
    public static boolean checkTimeDay (Context context, String tag, long time) {
        return checkTimeHour(context, tag, time * 24);
    }


    public static void update_status (Context context, boolean value) {
        saveBoolean(context, "status", value);
    }

    public static boolean check_access_status (Context context) {
        return getBoolean(context, "status");
    }

    public static void save_cleanRubbishTime (Context context) {
        saveSystemTime(context, "clean_rub");
    }

    public static boolean checkCleanRubbishTime (Context context) {
        return checkTimeMinute(context, "clean_rub", 10);
    }

}
