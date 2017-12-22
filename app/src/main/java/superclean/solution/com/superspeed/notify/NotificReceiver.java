package superclean.solution.com.superspeed.notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import superclean.solution.com.superspeed.R;
import superclean.solution.com.superspeed.activity.MainActivity;
import superclean.solution.com.superspeed.utils.OtherUtil;
import superclean.solution.com.superspeed.view.XmlShareUtil;

/**
 * Created by hwl on 2017/09/13.
 */

public class NotificReceiver extends BroadcastReceiver {

    public static final int NID = "MainNotify".hashCode();

    public static final String ACTION_SHOW = "com.notific.android.SHOW_ACTION.";
    public static final String ACTION_CANCEL = "com.notific.android.CANCEL_ACTION.";
    public static final String ACTION_PSL = "intent.action.notification.panshilong.zuishuai";

    private NotificationManager notifyManager = null;
    private Context context;
    private boolean show = false;

    @Override
    public void onReceive (Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();

        if ( !TextUtils.isEmpty(action) ) {
            if ( notifyManager == null ) notifyManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if ( action.equals(ACTION_SHOW + context.getPackageName()) ) {
                show = true;
                if ( notifyManager != null ) {
                    reveicerHandler.sendEmptyMessage(1025);
                }
            } else if ( action.equals(ACTION_CANCEL + context.getPackageName()) ) {
                show = false;
                notifyManager.cancel(NID);
                reveicerHandler.removeMessages(1025);
            }
        }
    }


    private Handler reveicerHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            if ( msg.what == 1025 ) {
                if ( show ) notifyManager.notify(NID, getNotify(context));
                reveicerHandler.sendEmptyMessageDelayed(1025, 1000);
            }
        }
    };

    @RequiresApi (api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Notification getNotify (Context context) {

        long totalRAM = OtherUtil.getTotalRAMSize();
        long availRAM = OtherUtil.getAvailRAMSize(context);

        int currentRam = Integer.valueOf(OtherUtil.formatNoPoint(((float) availRAM / (float) totalRAM) * 100));
        String title = String.format(context.getResources().getString(R.string.notify_title), currentRam + "%");
        String text = context.getResources().getString(R.string.notify_text);
        if ( !XmlShareUtil.checkTimeMinute(context, XmlShareUtil.TAG_MEMORY_CLEAN_TIME, 5) ) {
            text = context.getResources().getString(R.string.memory_fast_statues);
        }

        Intent notifyIntent = new Intent(context, MainActivity.class);
        notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, 100, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //2.通过Notification.Builder来创建通知
        Notification.Builder notifyBuilder = new Notification.Builder(context);
        notifyBuilder.setContentTitle(title)//
                .setContentText(text)//
                .setSmallIcon(R.drawable.ic_launcher_small)//
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))//
                .setShowWhen(false)//
                .setContentIntent(pi);

        Notification notify = notifyBuilder.build();
        notify.flags = Notification.FLAG_NO_CLEAR;

        return notify;
    }
}