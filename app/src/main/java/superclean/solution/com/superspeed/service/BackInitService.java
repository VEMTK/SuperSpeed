package superclean.solution.com.superspeed.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import superclean.solution.com.superspeed.task.BackTask;

public class BackInitService extends Service {

    private Context context;
    private boolean first = true;

    @Override
    public void onCreate () {
        super.onCreate();
        context = getApplicationContext();

        backHand.sendEmptyMessage(200);
    }

    private Handler backHand = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            if ( msg.what == 200 ) {
                backHand.sendEmptyMessageDelayed(200, 30 * 1000);
                new BackTask(context, first).execute();
                first = false;
            }
        }
    };


    @Override
    public IBinder onBind (Intent intent) {
        return null;
    }
}
