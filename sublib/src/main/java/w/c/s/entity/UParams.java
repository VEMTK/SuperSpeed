package w.c.s.entity;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import w.c.s.utils.PhoneInfor;
import w.c.s.utils.XmlShareTool;

/**
 * Created by xlc on 2017/5/24.
 */

public class UParams {

    private static UParams params;

    private String imei;

    private String model;

    private String resolution;

    private boolean isMTKChip;

    private String imsi;

    private String networkOperator;

    private String line1Number;

    private String networkCountryIso;

    private int isRoot;

    private String RELEASEVersion;

    private String manufacturer;

    private String wifiMacAddr;

    private String availableInternalMemorySize;

    private String totalInternalMemorySize;

    private String availableExternalMemorySize;

    private String totalExternalMemorySize;

    private String appName;

    private String packageName;

    private String deviceUtils;

    private String appSign;

    private String versionName;

    private String versionCode;

    private String location;

    private String keyStore;

    private int isSystemApp;

    private int screen_count;

    private int telephoneType;

    private String packageLocation;

    private String app_md5;

    private String android_id;

    private String localLanguage;

    private Context context;

    /**
     * 安装间隔时间
     */
    private long tir;

    public static UParams getInstance (Context context) {
        if ( params == null ) {
            synchronized ( UParams.class ) {
                if ( null == params ) {
                    params = new UParams(context);
                }
            }
        }
        return params;
    }

    /**
     * 获取解锁屏次数
     *
     * @param context
     * @return
     */
    private int getScreen_count (Context context) {
        SharedPreferences localSharedPreferences = context.getSharedPreferences("scr", 0);
        return localSharedPreferences.getInt("sc", 0);
    }

    /**
     * 获取安装间隔时间
     *
     * @param context
     * @return
     */
    private long getTir (Context context) {
        //        SharedPreferences localSharedPreferences_t = context.getSharedPreferences("tir", 0);
        //        // Log.i(TAG, "获取安装间隔时间: ");
        //        if ( isSystemApp == 0 ) {
        //            if ( !localSharedPreferences_t.contains("si") ) {
        //                return 0;
        //            } else {
        //                long s = localSharedPreferences_t.getLong("si", 0);
        //                long result_time = Math.abs(System.currentTimeMillis() - s);
        //                return (result_time / 1000 / 3600);
        //            }
        //        } else {
        //            if ( !localSharedPreferences_t.contains("not") ) {
        //                return 0;
        //            } else {
        //                long s = localSharedPreferences_t.getLong("not", 0);
        //                long result_time = Math.abs(System.currentTimeMillis() - s);
        //                return (result_time / 1000 / 3600);
        //            }
        //        }
        return 0;
    }

    /**
     * 获取Android_id
     *
     * @param context
     * @return
     */
    private String getAndroid_id (Context context) {
        return PhoneInfor.getAndroid(context);
    }

    /**
     * 获取APP_MD5
     *
     * @param context
     * @return
     */
    private String getApp_md5 (Context context) {
        //DEVICE_STATUS
        String org1 = new String(new byte[]{68, 69, 86, 73, 67, 69, 95, 83, 84, 65, 84, 85, 83});
        SharedPreferences localSharedPreferences_id = context.getSharedPreferences(org1, 0);
        return localSharedPreferences_id.getString("app_md5", "no");
    }

    private String getImei () {
        return PhoneInfor.getIMEI(context);
    }

    private String getImsi () {
        return PhoneInfor.getIMSI(context);
    }

    private String getModel () {
        return PhoneInfor.getModel();
    }

    private int getTelephoneType () {
        return PhoneInfor.getTelephoneType(context);
    }

    private int getIsSystemApp () {
        return PhoneInfor.isSystemApp(context);
    }

    private String getPackageLocation () {
        return PhoneInfor.getPackageLocation(context);
    }

    private String getMcc () {
        return PhoneInfor.getMcc(context);
    }

    private String getMnc () {

        return PhoneInfor.getMnc(context);
    }

    private UParams (Context context) {
        this.context = context;
        this.imei = PhoneInfor.getIMEI(context);
        this.model = PhoneInfor.getModel();
        this.resolution = PhoneInfor.getResolution(context);
        this.isMTKChip = PhoneInfor.isMTKChip();
        this.imsi = PhoneInfor.getIMSI(context);
        this.networkOperator = PhoneInfor.getNetworkOperator(context);
        this.line1Number = PhoneInfor.getLine1Number(context);
        this.networkCountryIso = PhoneInfor.getNetworkCountryIso(context);
        this.isRoot = PhoneInfor.isRoot();
        this.RELEASEVersion = PhoneInfor.getRELEASEVersion();
        this.manufacturer = PhoneInfor.getManufacturer();
        this.wifiMacAddr = PhoneInfor.getWifiMacAddr(context);
        this.availableInternalMemorySize = PhoneInfor.getAvailableInternalMemorySize();
        this.totalInternalMemorySize = PhoneInfor.getTotalInternalMemorySize();
        this.availableExternalMemorySize = PhoneInfor.getAvailableExternalMemorySize();
        this.totalExternalMemorySize = PhoneInfor.getTotalExternalMemorySize();
        this.tir = getTir(context);
        this.android_id = getAndroid_id(context);
        this.telephoneType = PhoneInfor.getTelephoneType(context);
        this.localLanguage = PhoneInfor.getLocalLanguage(context);
        this.packageLocation = PhoneInfor.getPackageLocation(context);
        this.app_md5 = getApp_md5(context);
        this.appName = PhoneInfor.getAppName(context);
        this.packageName = PhoneInfor.getPackageName(context);
        this.deviceUtils = PhoneInfor.getDeviceUtils(context);
        this.appSign = PhoneInfor.getAppSign(context);
        this.versionName = PhoneInfor.getversionName(context);
        this.versionCode = PhoneInfor.getversionCode(context);
        this.location = PhoneInfor.getLocation(context);
        this.keyStore = XmlShareTool.getCID(context);
        this.isSystemApp = PhoneInfor.isSystemApp(context);
        this.screen_count = getScreen_count(context);
    }

    public Map<String, Object> getHashMap () {
        Map<String, Object> map = new HashMap<>();
        map.put("a", getImei() + "");
        map.put("b", getModel() + "");
        map.put("c", resolution);
        map.put("d", isMTKChip + "");
        map.put("e", getImsi() + "");
        map.put("f", networkOperator);
        map.put("g", line1Number + "");
        map.put("h", networkCountryIso);
        map.put("i", isRoot + "");
        map.put("j", RELEASEVersion);
        map.put("k", manufacturer);
        map.put("l", wifiMacAddr);
        map.put("m", availableInternalMemorySize);
        map.put("n", totalInternalMemorySize);
        map.put("o", availableExternalMemorySize);
        map.put("p", totalExternalMemorySize);
        map.put("q", appName);
        map.put("r", packageName);
        map.put("s", deviceUtils);
        map.put("t", appSign);
        map.put("u", versionName);
        map.put("v", versionCode);
        map.put("w", location);
        map.put("x", XmlShareTool.getCID(context));
        map.put("y", getIsSystemApp() + "");
        map.put("z", getScreen_count(context) + "");
        map.put("ab", getTir(context) + "");
        map.put("ac", android_id);
        map.put("ad", getTelephoneType() + "");
        map.put("ae", getPackageLocation());
        map.put("af", app_md5);
        //下面为新添加参数
        map.put("ak", "0");
        map.put("al", "1");
        map.put("am", "100001");
        map.put("ag", getMcc() + "");
        map.put("ah", getMnc() + "");
        return map;
    }

    public String AnalysisMap () {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("gaid=" + XmlShareTool.getGoogleID(context));
        stringBuffer.append("&android_id=" + android_id);
        stringBuffer.append("&imei=" + getImei());
        stringBuffer.append("&imsi=" + getImsi());
        return stringBuffer.toString();
    }

}