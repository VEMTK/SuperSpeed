package superclean.solution.com.superspeed.notify;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * Created by hwl on 2017/11/29.
 */

public class NotificService extends Service {

    private NotificReceiver notificReceiver;

    @Override
    public IBinder onBind (Intent intent) {
        return null;
    }

    @Override
    public void onCreate () {
        super.onCreate();

        notificReceiver = new NotificReceiver();

        IntentFilter notificFilter = new IntentFilter();
        notificFilter.addAction(NotificReceiver.ACTION_SHOW + getPackageName());
        notificFilter.addAction(NotificReceiver.ACTION_CANCEL + getPackageName());
        notificFilter.addAction(NotificReceiver.ACTION_PSL);
        registerReceiver(notificReceiver, notificFilter);

    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy () {
        super.onDestroy();

        if ( notificReceiver != null ) unregisterReceiver(notificReceiver);
    }
}
