package superclean.solution.com.superspeed.task;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import superclean.solution.com.superspeed.R;
import superclean.solution.com.superspeed.bean.AppProcessInfo;
import superclean.solution.com.superspeed.listener.OnScanMemoryListener;
import superclean.solution.com.superspeed.processes.ProcessManager;
import superclean.solution.com.superspeed.utils.SettingDefaultList;
import superclean.solution.com.superspeed.view.XmlShareUtil;


public class ScanMemoryTask extends AsyncTask<Void, Integer, List<AppProcessInfo>> {

    private Context context;
    long totalMemory = 0;
    long noSysMemory = 0;
    private OnScanMemoryListener scanMemoryListener;
    private PackageManager packageManager = null;
    private ActivityManager activityManager = null;

    private String whiteList = "";

    public ScanMemoryTask (Context context, OnScanMemoryListener scanMemoryListener) {
        this.context = context;
        this.scanMemoryListener = scanMemoryListener;
        packageManager = context.getPackageManager();
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        whiteList = XmlShareUtil.getString(context, XmlShareUtil.TAG_MEMORY_WHITE_LIST_PKG);
    }

    @Override
    protected void onPreExecute () {
        if ( scanMemoryListener != null ) {
            scanMemoryListener.onScanStarted(context);
        }
    }

    @Override
    protected List<AppProcessInfo> doInBackground (Void... params) {
        totalMemory = 0;
        noSysMemory = 0;

        List<AppProcessInfo> runningAppList = new ArrayList<AppProcessInfo>();

        ApplicationInfo applicationInfo = null;
        ActivityManager.RunningAppProcessInfo appProcessInfo = null;
        List<ActivityManager.RunningAppProcessInfo> appRunList = ProcessManager.getRunningAppProcessInfo(context);

        String hasAdd = "";

        for ( int j = 0; j < appRunList.size(); j++ ) {
            appProcessInfo = appRunList.get(j);

            if ( appProcessInfo.processName.equals(context.getPackageName()) || whiteList.contains(appProcessInfo.processName) ) continue;

            AppProcessInfo appProInfo = new AppProcessInfo();
            appProInfo.setProcessName(appProcessInfo.processName);
            appProInfo.setPid(appProcessInfo.pid);
            appProInfo.setUid(appProcessInfo.uid);
            appProInfo.setCheck(false);

            try {
                applicationInfo = packageManager.getApplicationInfo(appProcessInfo.processName, 0);

                if ( (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 ) {
                    appProInfo.setSystem(true);
                } else {
                    appProInfo.setSystem(false);
                    appProInfo.setCheck(true);

                    appProInfo.setAppIcon(applicationInfo.loadIcon(packageManager));
                    appProInfo.setAppName(applicationInfo.loadLabel(packageManager).toString());

                    long memsize = activityManager.getProcessMemoryInfo(new int[]{appProcessInfo.pid})[0].getTotalPrivateDirty() * 1024;

                    appProInfo.setMemory(memsize);

                    totalMemory += memsize;
                    if ( !appProInfo.isSystem() ) noSysMemory += memsize;

                    hasAdd = hasAdd + "\'" + appProInfo.getAppPkg() + "\'";
                    runningAppList.add(appProInfo);
                    publishProgress(j, appRunList.size());
                }
            } catch ( PackageManager.NameNotFoundException e ) {
                //后台服务

                if ( appProcessInfo.processName.indexOf(":") != -1 ) {
                    applicationInfo = getApplicationInfo(appProcessInfo.processName.split(":")[0]);
                    if ( applicationInfo != null ) {
                        appProInfo.setAppIcon(applicationInfo.loadIcon(packageManager));
                    } else {
                        appProInfo.setAppIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
                    }
                } else {
                    appProInfo.setAppIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
                }

                appProInfo.setSystem(true);
                appProInfo.setAppName(appProcessInfo.processName);
            }

            try {
                Thread.sleep(new Random().nextInt(50) + 30);
            } catch ( Exception e ) {
            }
        }

        //随机添加5-10个非系统应用
        List<AppProcessInfo> notSysList = SettingDefaultList.getInstance(context).getNonSysAPPList();
        int rand = new Random().nextInt(6) + 5;
        for ( int i = 0; i < rand; i++ ) {
            AppProcessInfo appInfo = notSysList.get(new Random().nextInt(notSysList.size()));
            int index = 0;
            while ( hasAdd.contains("\'" + appInfo.getAppPkg() + "\'") && index < 5 ) {
                appInfo = notSysList.get(new Random().nextInt(notSysList.size()));
                index++;
            }
            if ( index < 5 ) {
                if ( !hasAdd.contains("\'" + appInfo.getAppPkg() + "\'") ) {

                    appInfo.setMemory((new Random().nextInt(100) + 30) * 1024 * 1024);
                    appInfo.setSystem(false);
                    appInfo.setCheck(true);

                    totalMemory += appInfo.getMemory();
                    if ( !appInfo.isSystem() ) noSysMemory += appInfo.getMemory();

                    hasAdd = hasAdd + "\'" + appInfo.getAppPkg() + "\'";
                    runningAppList.add(appInfo);
                    publishProgress(i, rand);
                }
            }

            try {
                Thread.sleep(new Random().nextInt(50) + 30);
            } catch ( Exception e ) {
            }
        }

        //随机添加5-10个系统应用
        List<AppProcessInfo> sysList = SettingDefaultList.getInstance(context).getSysAPPList();
        rand = new Random().nextInt(6) + 5;
        for ( int i = 0; i < rand; i++ ) {
            AppProcessInfo appInfo = sysList.get(new Random().nextInt(sysList.size()));
            int index = 0;
            while ( hasAdd.contains("\'" + appInfo.getAppPkg() + "\'") && index < 5 ) {
                appInfo = sysList.get(new Random().nextInt(sysList.size()));
                index++;
            }

            if ( index < 5 ) {
                if ( !hasAdd.contains("\'" + appInfo.getAppPkg() + "\'") ) {
                    appInfo.setMemory((new Random().nextInt(100) + 30) * 1024 * 1024);
                    appInfo.setSystem(false);
                    appInfo.setCheck(false);

                    hasAdd = hasAdd + "\'" + appInfo.getAppPkg() + "\'";
                    totalMemory += appInfo.getMemory();
                    if ( !appInfo.isSystem() ) noSysMemory += appInfo.getMemory();

                    runningAppList.add(appInfo);
                    publishProgress(i, rand);
                }
            }
            try {
                Thread.sleep(new Random().nextInt(50) + 30);
            } catch ( Exception e ) {
            }
        }

        return runningAppList;
    }

    @Override
    protected void onProgressUpdate (Integer... values) {
        if ( scanMemoryListener != null ) {
            scanMemoryListener.onScanMemeory(context, values[0], values[1], totalMemory);
        }
    }

    @Override
    protected void onPostExecute (List<AppProcessInfo> result) {
        if ( noSysMemory < 1024 ) {
            if ( scanMemoryListener != null ) scanMemoryListener.onSmoothly();
        } else {
            if ( scanMemoryListener != null ) scanMemoryListener.onScanCompleted(context, result);
        }
    }

    public ApplicationInfo getApplicationInfo (String processName) {
        if ( processName == null ) {
            return null;
        }
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for ( ApplicationInfo appInfo : appList ) {
            if ( processName.equals(appInfo.processName) ) {
                return appInfo;
            }
        }
        return null;
    }
}