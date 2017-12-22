package superclean.solution.com.superspeed.task;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;

import java.util.List;
import java.util.Random;

import superclean.solution.com.superspeed.bean.AppProcessInfo;
import superclean.solution.com.superspeed.listener.OnCloseAppListener;
import superclean.solution.com.superspeed.view.XmlShareUtil;


/**
 * Created by hwl on 2017/11/20.
 */

public class KillBackAppTask extends AsyncTask<Void, Integer, Void> {

    private Context context;
    private List<AppProcessInfo> killAppList;
    private OnCloseAppListener onKillListener;

    public KillBackAppTask (Context context, List<AppProcessInfo> killAppList, OnCloseAppListener onKillListener) {
        this.context = context;
        this.killAppList = killAppList;
        this.onKillListener = onKillListener;
    }

    @Override
    protected Void doInBackground (Void... params) {

        XmlShareUtil.saveSystemTime(context, XmlShareUtil.TAG_MEMORY_CLEAN_TIME);

        XmlShareUtil.update_status(context, true);

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for ( int i = 0; i < killAppList.size(); i++ ) {

            String pkgName = killAppList.get(i).getProcessName();

            publishProgress(i, killAppList.size());

//            if ( isAccessibilitySettingsOn(context) ) {
//                notifyAppDetailView(pkgName);
//            }
//
//            activityManager.killBackgroundProcesses(killAppList.get(i).getProcessName());

            try {
                Thread.sleep((new Random().nextInt(7) + 5) * 100);
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate (Integer... values) {
        super.onProgressUpdate(values);

        if ( onKillListener != null ) onKillListener.onClose(killAppList.get(values[0]), values[0]);
    }

    @Override
    protected void onPostExecute (Void aVoid) {
        super.onPostExecute(aVoid);

        if ( onKillListener != null ) onKillListener.onCloseComplete();
    }

    public void notifyAppDetailView (String pkgname) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", pkgname, null));
        context.getApplicationContext().startActivity(intent);
    }
}
