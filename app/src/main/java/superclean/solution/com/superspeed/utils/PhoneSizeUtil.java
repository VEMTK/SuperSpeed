package superclean.solution.com.superspeed.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * ROM、RAM可用和总大小
 */
public class PhoneSizeUtil {

    private static boolean cache = false;

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
        return getTotalRAMSize() - mi.availMem;
    }

    /**
     * ROM总大小
     */
    public static long getTotalROMSize () {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;  // Byte
    }

    /**
     * ROM可用空间
     */
    public static long getAvailROMSize () {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return getTotalROMSize() - availableBlocks * blockSize; // Byte
    }

    /**
     * Byte转KB、MG、GB
     *
     * @param falg true 强制转GB
     */
    public static String formatSize (long size, boolean falg) {
        String suffix = "B";// Byte
        String encode = "#0";
        float fSize = 0;

        size = Math.abs(size);

        if ( size >= 1024 ) {
            suffix = "KB";
            fSize = size / 1024;

            if ( fSize >= 1024 ) {
                suffix = "MB";
                fSize /= 1024;
            }
            if ( fSize >= 1024 ) {
                suffix = "GB";
                fSize /= 1024;
                encode = "#0.00";
            }
        } else {
            fSize = size;
        }

        if ( falg && suffix.equals("MB") ) {
            fSize /= 1024;
            suffix = "GB";
            encode = "#0.00";
        }

        //        Locale.setDefault(Locale.CHINESE);//切换任何语言都显示阿拉伯数字
        java.text.DecimalFormat df = new java.text.DecimalFormat(encode);
        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));

        if ( suffix != null ) {
            resultBuffer.append(suffix);
        }

        return resultBuffer.toString();
    }


    public static float formatSizeFloat (long size, boolean falg) {
        String suffix = "B";// Byte
        float fSize = 0;

        size = Math.abs(size);

        if ( size >= 1024 ) {
            suffix = "KB";
            fSize = size / 1024;
            if ( fSize >= 1024 ) {
                suffix = "MB";
                fSize /= 1024;
            }
            if ( fSize >= 1024 ) {
                suffix = "GB";
                fSize /= 1024;
            }
        } else {
            fSize = size;
        }

        if ( falg && suffix.equals("MB") ) {
            fSize /= 1024;
        }
        return fSize;
    }


    /**
     * Unicode 转 String
     */
    public static String decodeUnicode (final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while ( start > -1 ) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if ( end == -1 ) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

    public static String getTotalCache (long size) {
        cache = true;
        if ( PhoneSizeUtil.formatSizeFloat(size, true) < 0.9 ) {
            return PhoneSizeUtil.formatSize(size, false);
        }
        return PhoneSizeUtil.formatSize(size, true);
    }

}