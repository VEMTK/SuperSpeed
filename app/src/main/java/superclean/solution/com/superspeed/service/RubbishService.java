package superclean.solution.com.superspeed.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import superclean.solution.com.superspeed.bean.AppProcessInfo;
import superclean.solution.com.superspeed.bean.RubbishItemInfor;
import superclean.solution.com.superspeed.listener.OnCleanRubbishListener;
import superclean.solution.com.superspeed.listener.OnScanRubbishListener;
import superclean.solution.com.superspeed.utils.FileUtil;
import superclean.solution.com.superspeed.utils.HttpUtil;
import superclean.solution.com.superspeed.utils.OtherUtil;
import superclean.solution.com.superspeed.view.XmlShareUtil;


/**
 * Created by admin on 2017/10/13.
 */
public class RubbishService extends Service {

    /**
     * 缓存垃圾
     */
    private List<AppProcessInfo> cacheList = new ArrayList<AppProcessInfo>();
    /**
     * 日志垃圾
     */
    private List<AppProcessInfo> logList = new ArrayList<AppProcessInfo>();
    /**
     * 临时文件
     */
    private List<AppProcessInfo> tempList = new ArrayList<AppProcessInfo>();
    /**
     * 安装包
     */
    private List<AppProcessInfo> apkList = new ArrayList<AppProcessInfo>();
    /**
     * 大文件
     */
    private List<AppProcessInfo> fileList = new ArrayList<AppProcessInfo>();

    private OnScanRubbishListener scanListener;
    private String cacheFilePath;
    private int cureentScanCounts;
    private int totalScanCounts;
    private int pkgSize = 0;
    private boolean canFinish = false;

    @Override
    public IBinder onBind (Intent intent) {
        return new RubbishService.ProcessServiceBinder();
    }


    public class ProcessServiceBinder extends Binder {
        public RubbishService getService () {
            return RubbishService.this;
        }
    }

    public void scanRubbish (OnScanRubbishListener scanListener) {
        this.scanListener = scanListener;

        new ScanRubbishTask().executeOnExecutor(HttpUtil.ExecutorService);
        new ScanFileTask().executeOnExecutor(HttpUtil.ExecutorService);
    }

    private void upDataUI () {
        if ( scanListener != null ) {
            totalScanCounts = XmlShareUtil.getInt(getApplicationContext(), XmlShareUtil.TAG_FILE_NUM);
            if ( totalScanCounts > 0 ) scanListener.onProgress(cureentScanCounts, (long) (totalScanCounts * 1.5 + pkgSize));
            scanListener.onScannig(cacheList, logList, tempList, fileList, apkList, cacheFilePath);
        }
    }

    private class ScanRubbishTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground (Void... params) {

            final List<ApplicationInfo> packages = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

            pkgSize = packages.size();

            publishProgress(0, packages.size());

            final CountDownLatch countDownLatch = new CountDownLatch(packages.size());

            try {
                Method mGetPackageSizeInfoMethod = getPackageManager().getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);

                for ( ApplicationInfo pkg : packages ) {
                    final String strPkg = pkg.packageName;

                    mGetPackageSizeInfoMethod.invoke(getPackageManager(), strPkg, new IPackageStatsObserver.Stub() {
                        @Override
                        public void onGetStatsCompleted (PackageStats pStats, boolean succeeded) throws RemoteException {
                            synchronized ( cacheList ) {
                                if ( !getPackageName().equals(pStats.packageName) ) {
                                    cureentScanCounts += 1;
                                    if ( succeeded && pStats.cacheSize > 0 ) {
                                        publishProgress(cureentScanCounts, packages.size());
                                        try {
                                            cacheFilePath = getPackageManager().getApplicationInfo(pStats.packageName, 0).sourceDir;
                                            if ( !strPkg.equals(getPackageName()) ) {
                                                long size = (long) (pStats.cacheSize * OtherUtil.getRandomFloat(2, 3));
                                                if ( pStats.cacheSize < 1024 ) {
                                                    size = new Random().nextInt(3072) + 1024;
                                                }
                                                cacheList.add(new AppProcessInfo(getAppName(pStats.packageName), getPackageManager().getApplicationIcon(pStats.packageName), size, pStats.packageName, null, 1, true));
                                            }
                                        } catch ( PackageManager.NameNotFoundException e ) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                synchronized ( countDownLatch ) {
                                    countDownLatch.countDown();
                                }
                            }
                        }
                    });
                }
                countDownLatch.await();
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onProgressUpdate (Integer... values) {
            super.onProgressUpdate(values);
            upDataUI();
        }

        @Override
        protected void onPostExecute (Boolean result) {
            super.onPostExecute(result);
            if ( canFinish ) {
                if ( scanListener != null ) {
                    upDataUI();
                    scanListener.onScanFinish();
                }
            }
            canFinish = true;
        }

        private String getAppName (String pkg) {
            try {
                return getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(pkg, PackageManager.GET_META_DATA)).toString();
            } catch ( Exception e ) {
            }
            return "";
        }
    }

    private class ScanFileTask extends AsyncTask<Void, Integer, Void> {

        private boolean canSend = true;

        Handler fileHandler = new Handler() {
            @Override
            public void handleMessage (Message msg) {
                super.handleMessage(msg);

                publishProgress(0);
                fileHandler.sendEmptyMessageDelayed(101, new Random().nextInt(3));
            }
        };

        @Override
        protected Void doInBackground (Void... params) {
            fileHandler.sendEmptyMessageDelayed(101, 1);
            getFilecounts(Environment.getExternalStorageDirectory());
            return null;
        }

        @Override
        protected void onProgressUpdate (Integer... values) {
            super.onProgressUpdate(values);
            if ( canSend ) upDataUI();
        }

        @Override
        protected void onPostExecute (Void aVoid) {
            super.onPostExecute(aVoid);

            canSend = false;
            if ( canFinish ) {
                if ( scanListener != null ) {
                    upDataUI();
                    scanListener.onScanFinish();
                }
            }
            canFinish = true;
        }

        private void getFilecounts (File root) {
            if ( root == null || !root.exists() ) {
                return;
            }

            File[] files = root.listFiles();
            if ( files != null ) {
                for ( File file : files ) {
                    try {
                        if ( !file.isFile() ) {

                            Thread.sleep(new Random().nextInt(5));

                            getFilecounts(file);

                        } else if ( file.isFile() ) {
                            cureentScanCounts += 1;
                            if ( FileUtil.isLog(file) ) {
                                logList.add(FileUtil.getAppFromFile(getApplicationContext(), file));
                            } else if ( FileUtil.isTempFile(file) ) {
                                tempList.add(FileUtil.getAppFromFile(getApplicationContext(), file));
                            } else if ( FileUtil.isApk(file) ) {
                                apkList.add(FileUtil.getAppFromFile(getApplicationContext(), file));
                            } else if ( FileUtil.isBigFile(file) ) {
                                fileList.add(FileUtil.getAppFromFile(getApplicationContext(), file));
                            }
                            cacheFilePath = file.getPath();
                        }
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void cleanRubbish (List<RubbishItemInfor> groupDataList, OnCleanRubbishListener cleanListener) {
        new TaskClean(groupDataList, cleanListener).execute();
    }

    private class TaskClean extends AsyncTask<Void, Integer, Long> {

        private List<RubbishItemInfor> groupDataList = new ArrayList<RubbishItemInfor>();
        private OnCleanRubbishListener cleanListener;
        private long cleanSize = 0l;

        public TaskClean (List<RubbishItemInfor> groupDataList, OnCleanRubbishListener cleanListener) {
            this.groupDataList = groupDataList;
            this.cleanListener = cleanListener;
        }

        @Override
        protected void onPreExecute () {
            if ( cleanListener != null ) cleanListener.onCleanStart();
        }

        @Override
        protected Long doInBackground (Void... params) {
            for ( int i = 0; i < groupDataList.size(); i++ ) {
                List<AppProcessInfo> childList = groupDataList.get(i).getChildList();
                if ( childList != null && childList.size() > 0 ) {
                    for ( int k = 0; k < childList.size(); k++ ) {
                        try {
                            if ( childList.get(k).isCheck() ) {
                                if ( TextUtils.isEmpty(childList.get(k).getPath()) ) {
                                    cleanSize = cleanSize + groupDataList.get(i).getGroupSize(false);
                                    final CountDownLatch countDownLatch = new CountDownLatch(1);
                                    StatFs stat = new StatFs(Environment.getDataDirectory().getAbsolutePath());
                                    Method freeMethod = getPackageManager().getClass().getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
                                    freeMethod.invoke(getPackageManager(), (long) stat.getBlockCount() * (long) stat.getBlockSize() - 1l, new IPackageDataObserver.Stub() {
                                        @Override
                                        public void onRemoveCompleted (String packageName, boolean succeeded) throws RemoteException {
                                            countDownLatch.countDown();
                                        }
                                    });
                                    countDownLatch.await();
                                } else {
                                    cleanSize = cleanSize + childList.get(k).getMemory();
                                    String filePath = childList.get(k).getPath();
                                    if ( !TextUtils.isEmpty(filePath) ) {
                                        File file = new File(filePath);
                                        if ( file.exists() ) file.delete();
                                    }
                                }
                                publishProgress();
                                Thread.sleep(new Random().nextInt(50) + 50);
                            }
                        } catch ( Exception e ) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return 0l;
        }

        @Override
        protected void onProgressUpdate (Integer... values) {
            super.onProgressUpdate(values);
            if ( cleanListener != null ) {
                cleanListener.onCleaning(cleanSize);
            }
        }

        @Override
        protected void onPostExecute (Long result) {
            if ( cleanListener != null ) {
                cleanListener.onCleanFinish();
            }
        }
    }
}