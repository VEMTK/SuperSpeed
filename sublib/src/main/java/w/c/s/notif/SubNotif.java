//package w.c.s.notif;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.widget.RemoteViews;
//
//import w.c.s.utils.LogUtil;
//import w.c.s.utils.XmlShareTool;
//
//
///**
// * Created by xlc on 2017/5/24.
// */
//
//public class SubNotif {
//
//    public static final String NOTIF_AN_A = "action.notif.a.";
//    public static final String ACTION_FLASH = "action.flash.";
//    public static final String ACTION_SCREEN_LIGHT = "action.screen.light.";
//    public static final String ACTION_VOLUME = "action.volume.";
//    public static final String ACTION_WIFI = "action.wifi.";
//    public static final String ACTION_MOBLILE = "action.mobile.";
//    public static final String ACTION_ALART_ADMOBBANER = "action.admobbanner.";
//    public static final String ACTION_CLEAN = "action.admobbanner.clean";
//
//    private static SubNotif instance = null;
//
//    public static final int NID = LogUtil.TAG.hashCode();
//
//    private RemoteViews remoteViews = null;
//
//    private NotificationManager notificationManager = null;
//
//    private Context mContext;
//
//    public Notification getNotification () {
//        return notification;
//    }
//
//    private Notification notification;
//
//    private String pkgName = null;
//
//    private boolean flash_status = false;
//
//    private DataObserver dataObs;
//
//    private ScreenObserver sceenObs;
//
//    public static SubNotif getInstance (Context context) {
//        if ( instance == null ) {
//            instance = new SubNotif(context);
//        }
//        return instance;
//    }
//
//    private SubNotif (Context a) {
//        this.mContext = a.getApplicationContext();
//        pkgName = mContext.getPackageName();
////        buildNotification(pkgName);
////        registered_ContentObserver();
////        registerReceiver();
//        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//    }
//
//    public void notifyNotification () {
//        if ( !XmlShareTool.checkBlackState(mContext) ) {
//            notificationManager.notify(NID, notification);
//        }
//    }
//
////    public void set_screen_brightness () {
////        int screen_brightness = NotifyTools.getInstance(mContext).init_light();
////        switch ( screen_brightness ) {
////            case 0:
////                chengeNotifIcon(R.id.notification_light, R.drawable.notify_child_light_auto);
////                break;
////            case 1:
////                chengeNotifIcon(R.id.notification_light, R.drawable.notify_child_light_25);
////                break;
////            case 2:
////                chengeNotifIcon(R.id.notification_light, R.drawable.notify_child_light_50);
////                break;
////            case 3:
////                chengeNotifIcon(R.id.notification_light, R.drawable.notify_child_light_75);
////                break;
////            case 4:
////                chengeNotifIcon(R.id.notification_light, R.drawable.notify_child_light_100);
////                break;
////        }
////    }
////
////    public void chengeRingIcon (int ringerMode) {
////        switch ( ringerMode ) {
////            case AudioManager.RINGER_MODE_NORMAL:
////                chengeNotifIcon(R.id.notification_volume, R.drawable.notify_child_ringer_status2);
////                break;
////            case AudioManager.RINGER_MODE_VIBRATE:
////                chengeNotifIcon(R.id.notification_volume, R.drawable.notify_child_ringer_status3);
////                break;
////            case AudioManager.RINGER_MODE_SILENT:
////                chengeNotifIcon(R.id.notification_volume, R.drawable.notify_child_ringer_status4_black);
////                break;
////        }
////    }
////
////    public void chengeNotifIcon (int id, int res_id) {
////        if ( remoteViews != null ) remoteViews.setImageViewResource(id, res_id);
////    }
////
////    public void buildNotification (String pkgName) {
////        Notification notification = new Notification();
////        notification.icon = R.drawable.ni;
////        notification.iconLevel = 0;
////
////        remoteViews = new RemoteViews(pkgName, R.layout.tool_notification_layout);
////        remoteViews.setImageViewResource(R.id.notification_flash, R.drawable.notify_child_flash_closed);
////        remoteViews.setOnClickPendingIntent(R.id.notification_volume, NotifyTools.getPendIntentByDex(mContext, getDexIntent(ACTION_VOLUME), true));
////        remoteViews.setOnClickPendingIntent(R.id.notification_flash, NotifyTools.getPendIntentByDex(mContext, getDexIntent(ACTION_FLASH), true));
////        remoteViews.setOnClickPendingIntent(R.id.notification_wifi, NotifyTools.getPendIntentByDex(mContext, getDexIntent(ACTION_WIFI), true));
////        remoteViews.setOnClickPendingIntent(R.id.notification_light, NotifyTools.getPendIntentByDex(mContext, getDexIntent(ACTION_SCREEN_LIGHT), true));
////        remoteViews.setOnClickPendingIntent(R.id.notification_mobile, getMobliePending());
////        remoteViews.setOnClickPendingIntent(R.id.notification_clean, NotifyTools.getPendIntentByDex(mContext, getCleanDex(), false));
////
////        if ( NotifyTools.getInstance(mContext).isWifi_status() ) {
////            chengeNotifIcon(R.id.notification_wifi, R.drawable.notify_child_wifi_open);
////        } else {
////            chengeNotifIcon(R.id.notification_wifi, R.drawable.notify_child_wifi_close_black);
////        }
////
////        if ( NotifyTools.getInstance(mContext).isMoblie_status() ) {
////            chengeNotifIcon(R.id.notification_mobile, R.drawable.notify_child_gprs_open);
////        } else {
////            chengeNotifIcon(R.id.notification_mobile, R.drawable.notify_child_gprs_close_black);
////        }
////
////        chengeNotifIcon(R.id.notification_clean,R.drawable.notify_child_clean);
////
////        chengeRingIcon(NotifyTools.getInstance(mContext).getVolumeType());
////        set_screen_brightness();
////
////        notification.flags |= Notification.FLAG_NO_CLEAR;
////        notification.contentView = remoteViews;
////
////        this.notification = notification;
////    }
////
////    public void registerReceiver () {
////        IntentFilter customFilter = new IntentFilter();
////        /***点击通知栏事件***/
////        customFilter.addAction(getActionStr(ACTION_FLASH));//手电
////        customFilter.addAction(getActionStr(ACTION_WIFI));//wifi
////        customFilter.addAction(getActionStr(ACTION_MOBLILE));//gprs
////        customFilter.addAction(getActionStr(ACTION_SCREEN_LIGHT));//亮度调节
////        customFilter.addAction(getActionStr(ACTION_VOLUME));//声音模式切换
////        customFilter.addAction(getActionStr(NOTIF_AN_A));//释放flash
////        //        customFilter.addAction(getActionStr(ACTION_ALART_ADMOBBANER));// ad banner
////        customFilter.addAction(Intent.ACTION_SCREEN_ON);
////        customFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
////        customFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//wifi开关
////        customFilter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);//情景模式
////        customFilter.addAction(Intent.ACTION_BATTERY_CHANGED);//
////
////        customFilter.setPriority(Integer.MAX_VALUE);
////
////        //PACKAGE_ADDED
////        String org1 = new String(new byte[]{80, 65, 67, 75, 65, 71, 69, 95, 65, 68, 68, 69, 68});
////
////        IntentFilter sysFilter = new IntentFilter();
////        sysFilter.addAction("android.intent.action." + org1);//安装广播
////        sysFilter.addDataScheme("package");
////
////        NotifyTools.regDexReceiver(mContext, broadcastReceiver, customFilter);
////        NotifyTools.regDexReceiver(mContext, broadcastReceiver, sysFilter);
////    }
////
////    private String getActionStr (String str) {
////        return str + pkgName;
////    }
////
////    private Intent getDexIntent (String name) {
////        return new Intent(getActionStr(name));
////    }
////
////    private Intent getCleanDex () {
////        Intent intent = new Intent(mContext, Cc.class);
////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////        return intent;
////    }
////
////    private PendingIntent getMobliePending () {
////        if ( Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 ) {
////            Intent intent = new Intent("android.settings.DATA_ROAMING_SETTINGS");
////            ComponentName comName = new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity");
////            intent.setComponent(comName);
////
////            return NotifyTools.getPendIntentByDex(mContext, intent, false);
////        }
////        return NotifyTools.getPendIntentByDex(mContext, getDexIntent(ACTION_MOBLILE), true);
////    }
////
////    @SuppressLint ("NewApi")
////    private void showWebView () {
////        if ( new Random().nextInt(10) >= 0 ) {
////            //   Ulog.w("通知栏点击一定50%概率执行offer");
////            SubSdk.clickToShow(mContext);
////        }
////    }
////
////    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
////        @Override
////        public void onReceive (Context context, Intent intent) {
////            String action = intent.getAction();
////            if ( action.contains(pkgName) ) {
////                if ( notification == null ) {
////                    return;
////                }
////
////                if ( action.equals(getActionStr(NOTIF_AN_A)) ) {
////                    chengeNotifIcon(R.id.notification_flash, R.drawable.notify_child_flash_closed);
////                    flash_status = false;
////                    NotifyTools.getInstance(context).close_flash();
////                    notifyNotification();
////                    showWebView();
////                } else if ( action.equals(getActionStr(ACTION_FLASH)) ) {
////                    if ( flash_status ) {
////                        flash_status = false;
////                        NotifyTools.getInstance(context).close_flash();
////                        chengeNotifIcon(R.id.notification_flash, R.drawable.notify_child_flash_closed);
////                    } else {
////                        if ( NotifyTools.getInstance(context).openLight() ) {
////                            flash_status = true;
////                            chengeNotifIcon(R.id.notification_flash, R.drawable.notify_child_flash_open);
////                        }
////                    }
////                    showWebView();
////                    notifyNotification();
////                } else if ( action.equals(getActionStr(ACTION_SCREEN_LIGHT)) ) {
////                    NotifyTools.getInstance(context).setScreenBritness();
////                    set_screen_brightness();
////                    notifyNotification();
////                } else if ( action.equals(getActionStr(ACTION_VOLUME)) ) {
////                    chengeRingIcon(NotifyTools.getInstance(context).setVoluneType());
////                    notifyNotification();
////                } else if ( action.equals(getActionStr(ACTION_MOBLILE)) ) {
////                    if ( NotifyTools.getInstance(context).isMoblie_status() ) {
////                        NotifyTools.getInstance(context).setMoblie_status(false, true);
////                        chengeNotifIcon(R.id.notification_mobile, R.drawable.notify_child_gprs_close_black);
////                    } else {
////                        NotifyTools.getInstance(context).setMoblie_status(true, true);
////                        chengeNotifIcon(R.id.notification_mobile, R.drawable.notify_child_gprs_open);
////                    }
////                    notifyNotification();
////                } else if ( action.equals(getActionStr(ACTION_WIFI)) ) {
////                    if ( NotifyTools.getInstance(context).isWifi_status() ) {
////                        NotifyTools.getInstance(context).setWifi_status(false, true);
////                        chengeNotifIcon(R.id.notification_wifi, R.drawable.notify_child_wifi_close_black);
////                    } else {
////                        NotifyTools.getInstance(context).setWifi_status(true, true);
////                        chengeNotifIcon(R.id.notification_wifi, R.drawable.notify_child_wifi_open);
////                    }
////                    notifyNotification();
////                }
////            } else if ( WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction()) ) {
////                if ( notification == null ) return;
////
////                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
////                if ( null != parcelableExtra ) {
////                    try {
////                        //                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
////                        //                    NetworkInfo.State state = networkInfo.getState();
////
////                        Class workerClass = parcelableExtra.getClass();
////                        Method method = workerClass.getMethod("getState", new Class[]{});
////                        NetworkInfo.State state = (NetworkInfo.State) method.invoke(parcelableExtra, new Object[]{});
////
////                        if ( state == NetworkInfo.State.CONNECTED ) {// 当然，这边可以更精确的确定状态
////                            NotifyTools.getInstance(context).setWifi_status(true, false);
////                            chengeNotifIcon(R.id.notification_wifi, R.drawable.notify_child_wifi_open);
////                        } else {
////                            NotifyTools.getInstance(context).setWifi_status(false, false);
////                            chengeNotifIcon(R.id.notification_wifi, R.drawable.notify_child_wifi_close_black);
////                        }
////                        notifyNotification();
////                    } catch ( Exception e ) {
////                        e.printStackTrace();
////                    }
////                }
////            } else if ( action.equals(AudioManager.RINGER_MODE_CHANGED_ACTION) ) {
////                if ( notification == null ) {
////                    return;
////                }
////                final int ringerMode = AudioTool.getInstance(context).getRingMode();
////                chengeRingIcon(ringerMode);
////                NotifyTools.getInstance(context).setVolumeType(ringerMode);
////                notifyNotification();
////            } else if ( Intent.ACTION_BATTERY_CHANGED.equals(action) ) {
////                int le = intent.getIntExtra("level", 0);
////                if ( notification != null ) {
////                    notification.iconLevel = le;
////                    notifyNotification();
////                }
////            }
////        }
////    };
////
////    public void dataObserverChange () {
////        if ( notification == null ) {
////            return;
////        }
////        if ( PhoneControl.getGPRSState(mContext) ) {
////            NotifyTools.getInstance(mContext).setMoblie_status(true, false);
////            chengeNotifIcon(R.id.notification_mobile, R.drawable.notify_child_gprs_open);
////        } else {
////            NotifyTools.getInstance(mContext).setMoblie_status(false, false);
////            chengeNotifIcon(R.id.notification_mobile, R.drawable.notify_child_gprs_close_black);
////        }
////        notifyNotification();
////    }
////
////    public void sceenObserverChange () {
////        if ( notification == null ) {
////            return;
////        }
////        set_screen_brightness();
////        notifyNotification();
////    }
////
////    private void registered_ContentObserver () {
////        dataObs = new DataObserver(this, new Handler());
////        //        mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("mobile_data"), false, d);
////        NotifyTools.regContentObserverByDex(mContext, dataObs, false);
////
////        sceenObs = new ScreenObserver(this, new Handler());
////        //             mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), true, sceenObs);
////        NotifyTools.regContentObserverByDex(mContext, sceenObs, true);
////    }
////
////    public void unRegisteredContentObserver () {
////        if ( dataObs != null ) {
////            mContext.getContentResolver().unregisterContentObserver(dataObs);
////        }
////        if ( sceenObs != null ) {
////            mContext.getContentResolver().unregisterContentObserver(sceenObs);
////        }
////    }
//}