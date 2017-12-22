package superclean.solution.com.superspeed.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.File;

import superclean.solution.com.superspeed.R;
import superclean.solution.com.superspeed.bean.AppProcessInfo;


/**
 * Created by admin on 2017/10/17.
 */

public class FileUtil {
    private static final long BIG_FILE = 5 * 1024 * 1024;

    public static boolean isApk (File f) {
        String path = getFileName(f);
        return path.endsWith(".apk");
    }

    public static boolean isMusic (File f) {
        final String REGEX = "(.*/)*.+\\.(mp3|m4a|ogg|wav|aac)$";
        return f.getName().matches(REGEX);
    }

    public static boolean isBigFile (File f) {
        return f.length() > BIG_FILE;
    }

    public static boolean isTempFile (File f) {
        String name = getFileName(f);
        return name.endsWith(".tmp") || name.endsWith(".temp");
    }

    public static boolean isLog (File f) {
        String name = getFileName(f);
        return name.endsWith(".log");
    }

    public static String getFileName (File f) {
        return f.getAbsolutePath();
    }

    public static AppProcessInfo getAppFromFile (Context context, File file) {
        if ( file.isDirectory() ) return null;

        AppProcessInfo appInfor = new AppProcessInfo();
        appInfor.setAppName(file.getName());
        appInfor.setPath(file.getAbsolutePath());
        appInfor.setFileName(file.getName());
        long size = (long) ((file.length() == 0 ? 1024 : file.length()) * OtherUtil.getRandomFloat(1, 3));

        if ( FileUtil.isLog(file) ) {
            appInfor.setAppIcon(context.getResources().getDrawable(R.drawable.file_type_txt));
            appInfor.setMemory(size);
            appInfor.setAppPkg(null);
            appInfor.setType(2);
            appInfor.setCheck(true);
        } else if ( FileUtil.isTempFile(file) ) {
            appInfor.setAppIcon(context.getResources().getDrawable(R.drawable.file_type_other));
            appInfor.setMemory(size);
            appInfor.setAppPkg(null);
            appInfor.setType(3);
            appInfor.setCheck(true);
        } else if ( FileUtil.isApk(file) ) {
            appInfor = OtherUtil.getAppInforByFile(context, file.getAbsolutePath());
            appInfor.setAppIcon(context.getResources().getDrawable(R.drawable.file_type_apk));
            appInfor.setMemory(file.length());
            appInfor.setType(4);
            appInfor.setCheck(false);
        } else if ( FileUtil.isBigFile(file) ) {
            String path = file.getAbsolutePath();
            Drawable drawable;
            if ( path.endsWith(".webm") || path.endsWith(".mp4") ) {
                drawable = context.getResources().getDrawable(R.drawable.file_type_mv);
            } else if ( path.endsWith(".zip") ) {
                drawable = context.getResources().getDrawable(R.drawable.file_type_zip);
            } else {
                drawable = context.getResources().getDrawable(R.drawable.file_type_other);
            }

            appInfor.setAppIcon(drawable);
            appInfor.setMemory(file.length());
            appInfor.setAppPkg(null);
            appInfor.setType(5);
            appInfor.setCheck(false);
        }
        return appInfor;
    }

    public static AppProcessInfo getAppFromFile (Context context, String path) {
        return getAppFromFile(context, new File(path));
    }
}