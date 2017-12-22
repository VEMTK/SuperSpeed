package superclean.solution.com.superspeed.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;

import superclean.solution.com.superspeed.listener.OnScanMemoryListener;
import superclean.solution.com.superspeed.task.ScanMemoryTask;


public class MemoryCleanService extends Service {

    private Context context;
    private OnScanMemoryListener scanMemoryListener;

    private ActivityManager activityManager = null;
    private PackageManager packageManager = null;

    private ProcessServiceBinder mBinder = new ProcessServiceBinder();

    @Override
    public void onCreate () {
        context = getApplicationContext();

        try {
            activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            packageManager = getApplicationContext().getPackageManager();
        } catch ( Exception e ) {

        }

    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    public void scanRunningAppInfor (OnScanMemoryListener scanMemoryListener) {
        this.scanMemoryListener = scanMemoryListener;
        new ScanMemoryTask(context, scanMemoryListener).execute();
    }

    @Override
    public IBinder onBind (Intent intent) {
        return mBinder;
    }

    public class ProcessServiceBinder extends Binder {
        public MemoryCleanService getService () {
            return MemoryCleanService.this;
        }
    }

}