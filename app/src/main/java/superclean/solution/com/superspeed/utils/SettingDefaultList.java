package superclean.solution.com.superspeed.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import superclean.solution.com.superspeed.bean.AppProcessInfo;
import superclean.solution.com.superspeed.view.XmlShareUtil;

public class SettingDefaultList {

    private static SettingDefaultList params = null;

    private Context context;
    //所有的APP列表
    private List<AppProcessInfo> allAPPList = new ArrayList<AppProcessInfo>();
    //内存加速白名单
    private List<AppProcessInfo> whiteList = new ArrayList<AppProcessInfo>();

    private List<AppProcessInfo> nonSysAPPList = new ArrayList<AppProcessInfo>();

    private List<AppProcessInfo> sysAPPList = new ArrayList<AppProcessInfo>();

    private List<AppProcessInfo> recommendList = new ArrayList<AppProcessInfo>();


    public static SettingDefaultList getInstance (Context context) {
        if ( params == null ) {
            synchronized ( SettingDefaultList.class ) {
                if ( null == params ) {
                    params = new SettingDefaultList(context);
                }
            }
        }
        return params;
    }

    public SettingDefaultList (Context context) {

        this.context = context;

        nonSysAPPList = new ArrayList<AppProcessInfo>();
        sysAPPList = new ArrayList<AppProcessInfo>();
        allAPPList = new ArrayList<AppProcessInfo>();

        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for ( int i = 0; i < packages.size(); i++ ) {
            try {
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
                    nonSysAPPList.add(tmpInfo);
                } else {
                    tmpInfo.setSystem(true);
                    sysAPPList.add(tmpInfo);
                }
                allAPPList.add(tmpInfo);
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }

        recommendList = new ArrayList<AppProcessInfo>();
        for ( int i = 0; i < allAPPList.size(); i++ ) {
            AppProcessInfo appInfor = allAPPList.get(i);
            if ( Util.getSmsApp(context).equals(appInfor.getAppPkg()) || (appInfor.getAppPkg()).contains(("com.android.contacts")) ) {
                appInfor.setCheck(true);
                recommendList.add(appInfor);
            }
        }

        LogUtil.showI("APP load end");
    }


    public AppProcessInfo getAppInforByPkg (String appPkg) {
        AppProcessInfo appInfor = new AppProcessInfo(appPkg);
        for ( int k = 0; k < allAPPList.size(); k++ ) {
            if ( allAPPList.get(k).getAppPkg().equals(appPkg) ) {
                appInfor = allAPPList.get(k);
                appInfor.setCheck(false);
                return appInfor;
            }
        }
        return appInfor;
    }

    public List<AppProcessInfo> getAllAPPList () {
        return allAPPList;
    }

    public List<AppProcessInfo> getWhiteList () {
        whiteList = new ArrayList<AppProcessInfo>();
        try {
            JSONArray jsonArray = new JSONArray(XmlShareUtil.getString(context, XmlShareUtil.TAG_MEMORY_WHITE_LIST_PKG));
            for ( int j = 0; j < jsonArray.length(); j++ ) {
                whiteList.add(getAppInforByPkg(jsonArray.getString(j)));
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            whiteList = new ArrayList<AppProcessInfo>();
        }
        return whiteList;
    }

    public List<AppProcessInfo> getNonSysAPPList () {
        return nonSysAPPList;
    }

    public List<AppProcessInfo> getSysAPPList () {
        return sysAPPList;
    }

    public List<AppProcessInfo> getRecommendList () {
        return recommendList;
    }

}