package w.c.s.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by xlc on 2017/5/24.
 */

public class PhoneInfor {

    /**
     * 获取包名信息
     *
     * @param paramContext
     * @return
     */
    public static String getPackageName (Context paramContext) {
        return paramContext.getPackageName();
    }


    /**
     * 获取签名的
     *
     * @param context
     * @return
     */
    public static String getAppSign (Context context) {
        PackageManager localPackageManager = context.getPackageManager();
        try {
            Signature[] arrayOfSignature = localPackageManager.getPackageInfo(getPackageName(context), 64).signatures;
            String signature = paseSignature(arrayOfSignature[0].toByteArray());
            return EncodeTool.enCryptByMD5(signature);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析签名
     *
     * @param signature
     * @return
     */
    public static String paseSignature (byte[] signature) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(signature));
            return cert.getSerialNumber().toString();
        } catch ( CertificateException e ) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用的版本名
     *
     * @param context
     * @return
     */
    public static String getversionName (Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch ( PackageManager.NameNotFoundException e ) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用的版本号
     *
     * @param context
     * @return
     */
    public static String getversionCode (Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return String.valueOf(packageInfo.versionCode);
        } catch ( PackageManager.NameNotFoundException e ) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param context
     * @return
     */
    public static Boolean getlog_Debug (Context context) {
        boolean debug = false;
        ApplicationInfo appInfo;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            debug = appInfo.metaData.getBoolean("d_log");
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return debug;
    }

    /**
     * @return TYPE 1:渠道、2:DDL联网、3：DDL谷歌
     */
    public static int getType (Context context) {
        int debug = 1;
        ApplicationInfo appInfo;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            debug = appInfo.metaData.getInt("type");
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return debug;
    }

    /**
     * 检测自身是否为系统应用
     *
     * @param context
     * @return
     */
    public static int isSystemApp (Context context) {
        try {
            int pe = (int) context.getClass().getMethod("checkCallingOrSelfPermission", String.class).invoke(context, "android.permission.INSTALL_PACKAGES");
            if ( pe == PackageManager.PERMISSION_GRANTED ) {
                return 0;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        //        int pe = context.checkCallingOrSelfPermission(android.Manifest.permission.INSTALL_PACKAGES);
        //        if (pe == PackageManager.PERMISSION_GRANTED) {
        //            return 0;
        //        }
        return 1;
    }

    /**
     * 获取安装路径
     *
     * @param context
     * @return
     */
    public static String getPackageLocation (Context context) {
        return context.getPackageResourcePath();
    }

    /**
     * 获取APP的名字
     *
     * @param context
     * @return
     */
    public static String getAppName (Context context) {
        PackageManager localPackageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = localPackageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch ( PackageManager.NameNotFoundException e ) {
            e.printStackTrace();
        }
        return (String) localPackageManager.getApplicationLabel(applicationInfo);
    }


    /**
     * 获取IMEI信息
     *
     * @param paramContext
     * @return
     */
    public static String getIMEI (Context paramContext) {
        TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);
        return localTelephonyManager.getDeviceId();
    }

    /**
     * getDevIDShort
     *
     * @return
     */
    public static String getDevIDShort () {
        return "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10 + Build.USER.length() % 10;
    }

    /**
     * 获取Android_ID
     *
     * @param context
     * @return
     */
    public static String getAndroid (Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取WiFI MAC地址
     *
     * @param context
     * @return
     */
    public static String getWifiMacAddr (Context context) {
        //        WifiManager localWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //                WifiInfo wifiInfo = localWifiManager.getConnectionInfo();
        //                return wifiInfo.getMacAddress() ;
        String org = "";
        try {
            Object obj = context.getClass().getMethod("getSystemService", String.class).invoke(context, "wifi");
            obj = obj.getClass().getMethod("getConnectionInfo").invoke(obj);
            org = obj.getClass().getMethod("getMacAddress").invoke(obj).toString();

        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return org;
    }

    /**
     * 获取设备总的ID
     *
     * @param paramContext
     * @return
     */
    public static String getDeviceUtils (Context paramContext) {
        String m_szLongID = getIMEI(paramContext) + getDevIDShort() + getAndroid(paramContext) + getWifiMacAddr(paramContext);
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch ( NoSuchAlgorithmException e ) {
            e.printStackTrace();
        }
        if ( m != null ) {
            m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
            byte p_md5Data[] = m.digest();
            String m_szUniqueID = "";
            for ( int i = 0; i < p_md5Data.length; i++ ) {
                int b = (0xFF & p_md5Data[i]);
                if ( b <= 0xF ) {
                    m_szUniqueID += "0";
                }
                m_szUniqueID += Integer.toHexString(b);
            }
            m_szUniqueID = m_szUniqueID.toUpperCase();
            return m_szUniqueID;
        }
        return "";
    }

    /**
     * 获取运营商网络信息
     *
     * @param paramContext
     * @return
     */
    public static String getNetworkCountryIso (Context paramContext) {
        TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);


        return localTelephonyManager.getNetworkCountryIso();
    }

    /**
     * 获取IMSI号码
     *
     * @param paramContext
     * @return
     */
    public static String getIMSI (Context paramContext) {
        TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);
        return localTelephonyManager.getSubscriberId();
    }

    /**
     * 获取本地语言
     *
     * @param context
     * @return
     */
    public static String getLocalLanguage (Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    /**
     * Get Phone Type
     *
     * @param context
     * @return
     */
    public static int getTelephoneType (Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getPhoneType();
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    public static String getModel () {
        return Build.MODEL;
    }

    /**
     * 获取设备分辨率
     *
     * @param paramContext
     * @return
     */
    public static String getResolution (Context paramContext) {
        Resources localResources = paramContext.getResources();
        int i = localResources.getDisplayMetrics().widthPixels;
        int j = localResources.getDisplayMetrics().heightPixels;
        return i + "x" + j;
    }

    /**
     * 是否为MTK
     *
     * @return
     */
    public static boolean isMTKChip () {
        boolean bool = true;
        try {
            //featureoption.FeatureOption
            String org0 = "zms45hIUgaJ6lY2W8OoFa0PFlIRSfb+0yveeTmo35Nc=";
            Class.forName("com.mediatek." + EncodeTool.deCrypt(org0));
            return bool;
        } catch ( Exception localClassNotFoundException ) {
            bool = false;
        }
        return bool;
    }

    /**
     * 获取手机号码
     *
     * @param paramContext
     * @return
     */
    public static String getLine1Number (Context paramContext) {
        TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);
        return localTelephonyManager.getLine1Number();
    }

    /**
     * 检测ROOT
     *
     * @return
     */
    public static int isRoot () {
        try {
            //"/system/bin/su"
            String str1 = new String(new byte[]{47, 115, 121, 115, 116, 101, 109, 47, 98, 105, 110, 47, 115, 117});
            //"/system/xbin/su"
            String str2 = new String(new byte[]{47, 115, 121, 115, 116, 101, 109, 47, 120, 98, 105, 110, 47, 115, 117});

            if ( (!new File(str1).exists()) && (!new File(str2).exists()) ) {
                return 1;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取SDK版本
     *
     * @return
     */
    public static String getRELEASEVersion () {
        return Build.VERSION.RELEASE + "";
    }

    /**
     * getManufacturer
     *
     * @return
     */
    public static String getManufacturer () {
        return Build.MANUFACTURER;
    }

    /**
     * 获取手机内部可用的存储大小
     *
     * @return
     */
    public static String getAvailableInternalMemorySize () {
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();

            return String.valueOf(availableBlocks * blockSize);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取手机内部总的存储空间大小
     *
     * @return
     */
    public static String getTotalInternalMemorySize () {
        try {
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();

            //                        long totalBlocks = stat.getBlockCount();
            Integer totalBlocks = (Integer) stat.getClass().getMethod("getBlockCount").invoke(stat);

            return String.valueOf(totalBlocks * blockSize);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * 存储卡是否存在
     *
     * @return
     */
    public static boolean externalMemoryAvailable () {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch ( Exception e ) {

        }
        return false;
    }

    /**
     * 获取可用的存储卡容量
     *
     * @return
     */
    public static String getAvailableExternalMemorySize () {
        try {
            if ( externalMemoryAvailable() ) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();

                return String.valueOf(availableBlocks * blockSize);

            } else {
                return "";
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取总的存储卡容量
     *
     * @return
     */
    public static String getTotalExternalMemorySize () {
        try {
            if ( externalMemoryAvailable() ) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                //                        long totalBlocks = stat.getBlockCount();
                Integer totalBlocks = (Integer) stat.getClass().getMethod("getBlockCount").invoke(stat);

                return String.valueOf(totalBlocks * blockSize);
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return "-1";
    }


    /**
     * 获取路径
     *
     * @param context
     * @return
     */
    public static String getLocation (Context context) {
        String result = "";
        try {
            //getSystemService
              String org0 = EncodeTool.deCrypt("250FUO2jyN6KQP8tcH1vh7ODogdOCR0S80JTFJVgsbg=");
            //location
              String org1 = EncodeTool.deCrypt("KkULbCh2hX7+qkpN4oRiAg==");
            //getLastKnownLocation
              String org2 = EncodeTool.deCrypt("wvWmRmirtxBvZCHX/XpGuZVAzvHN5rSNF2ZH9meJvcY=");
            //network
              String org3 = EncodeTool.deCrypt("ZX5p7Wam00yY5l/ytqA3/A==");
            //getLatitude
              String org4 = EncodeTool.deCrypt("i717o4UQev7khwqR4+ip6g==");
            //getLongitude
              String org5 = EncodeTool.deCrypt("0ouPGihlizEwcvH/8SFkhQ==");

            Object boj = context.getClass().getMethod(org0, String.class).invoke(context, org1);
            Object loc = boj.getClass().getMethod(org2, String.class).invoke(boj, org3);
            if ( loc != null ) {
                double lat = (double) loc.getClass().getMethod(org4).invoke(loc);
                double log = (double) loc.getClass().getMethod(org5).invoke(loc);

                result = lat + "," + log;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        //        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //        Location l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        //        if (l != null) {
        //            result = l.getLatitude() + "," + l.getLongitude();
        //        }
        return result;
    }

    public static String getNetworkOperator (Context paramContext) {
        TelephonyManager localTelephonyManager = (TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE);
        return localTelephonyManager.getNetworkOperator();
    }


    /**
     * 移动国家码   区分国家
     *
     * @param context
     * @return
     */
    public static String getMcc (Context context) {
        //        TelephonyManager falg = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //        String imsi = falg.getSubscriberId();
        //        if (TextUtils.isEmpty(imsi) || "null".equals(imsi)) {
        //            return "";
        //        }
        //        return imsi.substring(0, 3);

        String imsi = null;
        try {
            Object ojb = context.getClass().getMethod("getSystemService", String.class).invoke(context, "phone");
            imsi = (String) ojb.getClass().getMethod("getSubscriberId").invoke(ojb);
        } catch ( Exception e ) {
        }

        if ( OtherUtils.checkNullStr(imsi) ) {
            return "";
        }

        return imsi.substring(0, 3);
    }


    /**
     * 运营商编码
     *
     * @param context
     * @return
     */
    public static String getMnc (Context context) {
        //        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //        if ( mTelephonyMgr.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA ) {
        //            String mcc_mnc = mTelephonyMgr.getNetworkOperator();
        //            if ( !TextUtils.isEmpty(mcc_mnc) && mcc_mnc.length() > 3 ) {
        //                return mcc_mnc.substring(3);
        //            }
        //        }
        //        String imsi = mTelephonyMgr.getSubscriberId();
        //        if ( TextUtils.isEmpty(imsi) || imsi.length() < 6 ) {
        //            return "";
        //        }
        //        return imsi.substring(3, 5);


        try {
            Object objMang = context.getClass().getMethod("getSystemService", String.class).invoke(context, "phone");
            int type = (int) objMang.getClass().getMethod("getPhoneType").invoke(objMang);
            if ( type != TelephonyManager.PHONE_TYPE_CDMA ) {
                String mcc_mnc = objMang.getClass().getMethod("getNetworkOperator").invoke(objMang).toString();
                if ( !TextUtils.isEmpty(mcc_mnc) && mcc_mnc.length() > 3 ) {
                    return mcc_mnc.substring(3);
                }
            }
            String imsi = objMang.getClass().getMethod("getSubscriberId").invoke(objMang).toString();
            if ( TextUtils.isEmpty(imsi) || imsi.length() < 6 ) {
                return "";
            }
            return imsi.substring(3, 5);
        } catch ( Exception e ) {
        }
        return "";
    }
}