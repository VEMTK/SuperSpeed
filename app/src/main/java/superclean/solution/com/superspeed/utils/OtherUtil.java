package superclean.solution.com.superspeed.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import superclean.solution.com.superspeed.bean.AppProcessInfo;

/**
 * Created by hwl on 2017/10/13.
 */

public class OtherUtil {

    public static void hideNaviga (Activity context) {
        context.getWindow().getDecorView().setSystemUiVisibility(//
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public static void hideNavigationBar (Activity activity) {
        int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        systemUiVisibility |= flags;
        activity.getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    public static int getSdkVersion () {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取屏幕高、宽、密度
     *
     * @param org 1:密度、2:宽、3:高
     */
    public static float getPhoneScreenSize (Context context, int org) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        if ( org == 1 ) return dm.density;
        if ( org == 2 ) return dm.widthPixels;
        return dm.heightPixels;
    }

    /**
     * 获取当前手机屏幕和768*1184的比值
     */
    public static float getGapRatio (Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float ws = (float) dm.widthPixels / 720f;
        float hs = (float) dm.heightPixels / 1184f;
        float ds = dm.density / 2.0f;
        return (ws + hs + ds) / 3f;
    }

    //系统状态栏高度
    public static int getStatusBarHeight (Context context) {
        int statusBarHeight = -1;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if ( resourceId > 0 ) statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        return statusBarHeight;
    }

    //系统底部导航栏高度
    public static int getNavigationBarHeight (Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static int dp2px (Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static WindowManager.LayoutParams getWindowParam () {
        WindowManager.LayoutParams windowParam = new WindowManager.LayoutParams();
        windowParam.x = 0;
        windowParam.y = 0;
        windowParam.width = WindowManager.LayoutParams.MATCH_PARENT;
        windowParam.height = WindowManager.LayoutParams.MATCH_PARENT;

        //mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;//当请求权限的弹出框无法看到会导致ANR
        windowParam.type = WindowManager.LayoutParams.TYPE_TOAST;

        windowParam.format = PixelFormat.RGBA_8888;
        windowParam.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN//全屏
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR | WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER;//

        return windowParam;
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp (Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 判断设备是否有 “有权查看使用情况的应用” 权限
     */
    public static boolean checkPhoneHasOtherPermission (Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 判断APP是否有“有权查看使用情况的应用” 权限
     */
    @RequiresApi (api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean checkAppHasOtherPermission (Context context) {
        try {
            long ts = System.currentTimeMillis();
            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, ts);
            if ( queryUsageStats == null || queryUsageStats.isEmpty() ) {
                return false;
            }
            return true;
        } catch ( Exception e ) {
        }
        return false;
    }

    /**
     * 获取从from到to的小数，不包含from，不包含to
     */
    public static float getRandomFloat (float from, float to) {
        float result = new Random().nextFloat();
        int org = (int) Math.abs(to - from);
        if ( org <= 0 ) {
            return from + 1;
        }
        if ( from > to ) {
            result = new Random().nextInt(org) + to + result;
        } else if ( from < to ) {
            result = new Random().nextInt(org) + from + result;
        } else if ( from == to ) {
            result = from + result;
        }
        if ( from != to && from == result ) {
            result = getRandomFloat(from, to);
        }

        return Math.round(result * 100f) / 100f;
    }

    public static String formatOnePoint (float str) {
        if ( (str + "").contains(".") ) {
            if ( !(str + "").endsWith(".0") ) {
                return new DecimalFormat("##.#").format(str);
            }
            return str + "";
        }
        return str + ".0";
    }

    public static String formatTwoPoint (float str) {
        if ( (str + "").contains(".") ) {
            if ( !(str + "").endsWith(".00") ) {
                return new DecimalFormat("##.##").format(str);
            }
            return str + "";
        }
        return str + ".00";
    }

    public static String formatNoPoint (float str) {
        if ( (str + "").contains(".") ) {
            return new DecimalFormat("##").format(str);
        }
        return str + "";
    }


    /**
     * RAM总大小
     */
    public static long getTotalRAMSize () {
        String str1 = "/proc/meminfo";
        String str2 = "";
        String[] arrayOfString;
        long totalSize = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);

            String str3;
            while ( (str3 = localBufferedReader.readLine()) != null ) {
                if ( str3.contains("MemTotal") ) {
                    str2 = str3;
                }
            }

            arrayOfString = str2.split("\\s+");
            totalSize = Long.valueOf(arrayOfString[1]).longValue() * 1024;

            localBufferedReader.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return totalSize;
    }

    /**
     * RAM可用大小
     */
    public static long getAvailRAMSize (Context mContext) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return Math.abs(getTotalRAMSize() - mi.availMem);
    }

    public static List<AppProcessInfo> getPhoneAppList (Context context, boolean falg) {
        ArrayList<AppProcessInfo> appList = new ArrayList<AppProcessInfo>();
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);

        for ( int i = 0; i < packages.size(); i++ ) {
            PackageInfo packageInfo = packages.get(i);

            AppProcessInfo tmpInfo = new AppProcessInfo();
            tmpInfo.setCheck(false);
            tmpInfo.setMemory(0);
            tmpInfo.setProcessName(packageInfo.packageName);
            tmpInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(context.getPackageManager()));
            tmpInfo.setAppName(packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
            tmpInfo.setAppPkg(packageInfo.packageName);

            if ( (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0 ) {
                tmpInfo.setSystem(false);
                if ( falg ) appList.add(tmpInfo);
            } else {
                tmpInfo.setSystem(true);
                if ( !falg ) appList.add(tmpInfo);
            }
        }
        return appList;
    }

    /**
     * 判断某个应用是否安装
     *
     * @param packageName 包名
     */
    public static boolean isAppAvilible (Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if ( pinfo != null ) {
            for ( int i = 0; i < pinfo.size(); i++ ) {
                if ( pinfo.get(i).packageName.endsWith(packageName) ) return true;
            }
        }
        return false;//判断pName中是否有目标程序的包名，有TRUE，没有FALSE
    }

    /**
     * 获取对应APP文件的信息
     */
    public static AppProcessInfo getAppInforByFile (Context context, String appPath) {
        AppProcessInfo resultApp = new AppProcessInfo();
        PackageManager pm = context.getApplicationContext().getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(appPath, 0);
        if ( info != null ) {
            ApplicationInfo appInfo = info.applicationInfo;
            //一定要加上下面两句，要不然获取应用名会变成包名，图标会是默认的
            appInfo.sourceDir = appPath;
            appInfo.publicSourceDir = appPath;

            resultApp.setAppName(pm.getApplicationLabel(appInfo).toString());
            resultApp.setAppPkg(appInfo.packageName);
            resultApp.setVersion(info.versionName);
            resultApp.setPath(appPath);
            resultApp.setAppIcon(pm.getApplicationIcon(appInfo));
        }
        return resultApp;
    }
}