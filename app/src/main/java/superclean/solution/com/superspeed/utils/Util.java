package superclean.solution.com.superspeed.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/10/21.
 */

public class Util {

    /**
     * 获取系统短信应用
     *
     * @param context
     * @return
     */
    public static String getSmsApp (Context context) {

        if ( Build.VERSION.SDK_INT >= 19 ) {
            try {
                return (String) Class.forName("android.provider.Telephony$Sms").getMethod("getDefaultSmsPackage", new Class[]{Context.class}).invoke(null, new Object[]{context});
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        } else {
            Intent intent = new Intent("android.intent.action.SENDTO", Uri.fromParts("smsto", "", null));
            PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 65536);
            if ( queryIntentActivities != null && queryIntentActivities.size() > 0 ) {
                for ( ResolveInfo resolveInfo : queryIntentActivities ) {
                    if ( resolveInfo != null ) {
                        List arrayList = new ArrayList();
                        packageManager.getPreferredActivities(new ArrayList(), arrayList, resolveInfo.activityInfo.packageName);

                        if ( arrayList.size() > 0 ) {
                            return resolveInfo.activityInfo.packageName;
                        }
                    }
                }
            }
        }
        return "";
    }

    public static String getEmilApp (Context context) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra("return-data", true);
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 65536);
        if ( queryIntentActivities != null && queryIntentActivities.size() > 0 ) {
            for ( ResolveInfo resolveInfo : queryIntentActivities ) {
                if ( resolveInfo != null ) {

                    Log.e("Adlog", "resolveInfo：" + resolveInfo.activityInfo.packageName);

                    // return resolveInfo.activityInfo.packageName;
                }
            }
        } else {
            Log.e("Adlog", "为空");
        }
        return "";
    }


    @TargetApi (Build.VERSION_CODES.LOLLIPOP)
    private static boolean hasModule (Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @TargetApi (Build.VERSION_CODES.LOLLIPOP)
    public static boolean hasEnable (Context context) {
        if ( !hasModule(context) ) return false;
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, ts);
        if ( queryUsageStats == null || queryUsageStats.isEmpty() ) {
            return false;
        }
        return true;
    }


    public static int getStatusBarHeight (Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch ( Exception e1 ) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }


    public static void dismission (Context context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if ( imm.isActive() ) {
            final View v = ((Activity) context).getWindow().peekDecorView();
            if ( v != null && v.getWindowToken() != null ) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }

        }
    }

    public static void hideBottomUIMenu (View view) {
        //隐藏虚拟按键，并且全屏
        if ( Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19 ) { // lower api
            view.setSystemUiVisibility(View.GONE);
        } else if ( Build.VERSION.SDK_INT >= 19 ) {
            //for new api versions.
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            view.setSystemUiVisibility(uiOptions);
        }
    }


}
