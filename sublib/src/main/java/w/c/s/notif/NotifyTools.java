//package w.c.s.notif;
//
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.database.ContentObserver;
//import android.hardware.Camera;
//import android.net.Uri;
//import android.net.wifi.WifiManager;
//import android.provider.Settings;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.Method;
//
//import w.c.s.utils.AudioTool;
//import w.c.s.utils.PhoneControl;
//
//
///**
// * Created by xlc on 2017/5/24.
// */
//
//public class NotifyTools {
//
//    private static NotifyTools instance = null;
//
//    private WifiManager wifiManager = null;
//
//    private AudioTool audioTool = null;
//
//    private Context context;
//
//    private Camera sCamera = null;
//
//    public boolean isWifi_status () {
//        return wifi_status;
//    }
//
//    public void setWifi_status (boolean wifi_status, boolean changewifi) {
//        this.wifi_status = wifi_status;
//        if ( changewifi ) {
//            wifiManager.setWifiEnabled(wifi_status);
//        }
//    }
//
//    public boolean isMoblie_status () {
//        return moblie_status;
//    }
//
//    public void setMoblie_status (boolean moblie_status, boolean changeNet) {
//        this.moblie_status = moblie_status;
//        if ( changeNet ) {
//            PhoneControl.setMobileDataStatus(context, moblie_status);
//        }
//    }
//
//    public int getScreen_light_status () {
//        return screen_light_status;
//    }
//
//    private int screen_light_status;
//
//    private int next_index;
//
//    private boolean wifi_status;
//
//    private boolean moblie_status;
//
//    public int getVolumeType () {
//        return volumeType;
//    }
//
//    public void setVolumeType (int volumeType) {
//        this.volumeType = volumeType;
//    }
//
//    private int volumeType;
//
//    public static NotifyTools getInstance (Context context) {
//        if ( instance == null ) {
//            instance = new NotifyTools(context);
//        }
//        return instance;
//    }
//
//    private NotifyTools (Context context) {
//        this.context = context;
//        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        audioTool = AudioTool.getInstance(context);
//        init();
//    }
//
//    private void init () {
//        if ( wifiManager.getWifiState() == 3 ) {
//            wifi_status = true;
//        } else {
//            wifi_status = false;
//        }
//
//        if ( PhoneControl.getGPRSState(context) ) {
//            moblie_status = true;
//        } else {
//            moblie_status = false;
//        }
//
//        volumeType = audioTool.getRingMode();
//    }
//
//
//    public int setVoluneType () {
//        if ( volumeType == 0 ) {
//            volumeType = 1;
//        } else if ( volumeType == 1 ) {
//            volumeType = 2;
//        } else {
//            volumeType = 0;
//        }
//        audioTool.setRingMode(volumeType);
//
//        return volumeType;
//    }
//
//
//    public int init_light () {
//        try {
//            if ( Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC ) {
//                screen_light_status = 0;
//                next_index = 1;
//            } else {
//                int current_screen_brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 255);
//                if ( current_screen_brightness < 70 ) {
//                    screen_light_status = 1;
//                    next_index = 2;
//                } else if ( current_screen_brightness >= 70 && current_screen_brightness < 130 ) {
//                    screen_light_status = 2;
//                    next_index = 3;
//                } else if ( current_screen_brightness >= 130 && current_screen_brightness < 200 ) {
//                    screen_light_status = 3;
//                    next_index = 4;
//                } else if ( current_screen_brightness >= 200 ) {
//                    screen_light_status = 4;
//                    next_index = 0;
//                }
//            }
//        } catch ( Settings.SettingNotFoundException e ) {
//            e.printStackTrace();
//        }
//        return screen_light_status;
//    }
//
//    public void setScreenBritness () {
//        int brightness = 0;
//        switch ( next_index ) {
//            case 0:
//                PhoneControl.openscreenBrightness(context);
//                screen_light_status = 0;
//                next_index = 1;
//                return;
//            case 1:
//                brightness = 60;
//                screen_light_status = 1;
//                next_index = 2;
//                break;
//            case 2:
//                brightness = 125;
//                screen_light_status = 2;
//                next_index = 3;
//                break;
//            case 3:
//                brightness = 190;
//                screen_light_status = 3;
//                next_index = 4;
//                break;
//            case 4:
//                brightness = 255;
//                screen_light_status = 4;
//                next_index = 0;
//                break;
//        }
//        closescreenBrightness();
//
//        //不让屏幕全暗
//        if ( brightness <= 5 ) {
//            brightness = 5;
//        }
//        //保存为系统亮度方法1
//        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
//    }
//
//    private void closescreenBrightness () {
//        try {
//            if ( Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC ) {
//                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
//            }
//        } catch ( Settings.SettingNotFoundException e ) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private boolean check_exist_flash () {
//        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
//    }
//
//    public boolean openLight () {
//        boolean open = true;
//        if ( !check_exist_flash() ) {
//            open = false;
//        } else {
//            try {
//                sCamera = Camera.open();
//
//                //SurfaceTexture
//                String org0 = new String(new byte[]{83, 117, 114, 102, 97, 99, 101, 84, 101, 120, 116, 117, 114, 101});
//                //setPreviewTexture
//                String org1 = new String(new byte[]{115, 101, 116, 80, 114, 101, 118, 105, 101, 119, 84, 101, 120, 116, 117, 114, 101});
//                //startPreview
//                String org2 = new String(new byte[]{115, 116, 97, 114, 116, 80, 114, 101, 118, 105, 101, 119});
//                //getParameters
//                String org3 = new String(new byte[]{103, 101, 116, 80, 97, 114, 97, 109, 101, 116, 101, 114, 115});
//                //setFlashMode
//                String org4 = new String(new byte[]{115, 101, 116, 70, 108, 97, 115, 104, 77, 111, 100, 101});
//                //setParameters
//                String org5 = new String(new byte[]{115, 101, 116, 80, 97, 114, 97, 109, 101, 116, 101, 114, 115});
//
//                Class wClass = Class.forName("android.graphics." + org0);
//                Constructor<?> csr = wClass.getConstructor(int.class);
//
//                Class workerClass = sCamera.getClass();
//                Method method = workerClass.getMethod(org1, new Class[]{wClass});
//                method.invoke(sCamera, new Object[]{csr.newInstance(0)});
//                //                sCamera.setPreviewTexture(new SurfaceTexture(0));
//
//                method = workerClass.getMethod(org2, new Class[]{});
//                method.invoke(sCamera, new Object[]{});
//                //                sCamera.startPreview();
//
//                method = workerClass.getMethod(org3, new Class[]{});
//                Object obj = method.invoke(sCamera, new Object[]{});
//                Class parameters = method.invoke(sCamera, new Object[]{}).getClass();
//                //                Camera.Parameters parameters = sCamera.getParameters();
//
//                method = parameters.getMethod(org4, new Class[]{String.class});
//                method.invoke(obj, new Object[]{"torch"});
//                //                parameters.setFlashMode(parameters.FLASH_MODE_TORCH);
//
//                method = workerClass.getMethod(org5, new Class[]{parameters});
//                method.invoke(sCamera, new Object[]{obj});
//
//                //                Camera.Parameters parameters = sCamera.getParameters();
//                //                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//                //                sCamera.setParameters(parameters);
//
//            } catch ( Exception e ) {
//                sCamera = null;
//                //  Log.i("Adlog", "打开闪光灯失败：" + e.toString() + "");
//                open = false;
//
//                e.printStackTrace();
//            }
//        }
//        return open;
//    }
//
//    public void close_flash () {
//        if ( sCamera != null ) {
//            sCamera.stopPreview();
//            sCamera.release();
//            sCamera = null;
//        }
//    }
//
//    public static void regDexReceiver (Context context, BroadcastReceiver receiver, IntentFilter filter) {
//        try {
//            Class conClasss = context.getClass();
//            Method conMethod = conClasss.getMethod("registerReceiver", BroadcastReceiver.class, IntentFilter.class);
//            conMethod.invoke(context, receiver, filter);
//        } catch ( Exception e ) {
//            e.printStackTrace();
//        }
//    }
//
//    public static PendingIntent getPendIntentByDex (Context context, Intent org0, boolean falg) {
//        try {
//            //PendingIntent
//            String org1 = new String(new byte[]{80, 101, 110, 100, 105, 110, 103, 73, 110, 116, 101, 110, 116});
//            //getBroadcast
//            String name = new String(new byte[]{103, 101, 116, 66, 114, 111, 97, 100, 99, 97, 115, 116});
//            if ( !falg ) {
//                //getActivity
//                name = new String(new byte[]{103, 101, 116, 65, 99, 116, 105, 118, 105, 116, 121});
//            }
//
//            Class workerClass = Class.forName("android.app." + org1);
//            Method method = workerClass.getMethod(name, Context.class, int.class, Intent.class, int.class);
//            return (PendingIntent) method.invoke(workerClass, context, SubNotif.NID, org0, PendingIntent.FLAG_UPDATE_CURRENT);
//        } catch ( Exception e ) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static void regContentObserverByDex (Context context, ContentObserver org, boolean falg) {
//        try {
//            //getContentResolver
//            String org0 = new String(new byte[]{103, 101, 116, 67, 111, 110, 116, 101, 110, 116, 82, 101, 115, 111, 108, 118, 101, 114});
//            //registerContentObserver
//            String org1 = new String(new byte[]{114, 101, 103, 105, 115, 116, 101, 114, 67, 111, 110, 116, 101, 110, 116, 79, 98, 115, 101, 114, 118, 101, 114});
//            //mobile_data
//            String org2 = new String(new byte[]{109, 111, 98, 105, 108, 101, 95, 100, 97, 116, 97});
//
//            Object obj = context.getClass().getMethod(org0).invoke(context);
//            Method method = obj.getClass().getMethod(org1, Uri.class, boolean.class, ContentObserver.class);
//            method.invoke(obj, getScreenUri(falg), falg, org);
//        } catch ( Exception e ) {
//            e.printStackTrace();
//        }
//    }
//
//    private static Object getScreenUri (boolean falg) throws Exception {
//
//        //Settings$System
//        String org0 = new String(new byte[]{83, 101, 116, 116, 105, 110, 103, 115, 36, 83, 121, 115, 116, 101, 109});
//        //Settings$Secure
//        String org1 = new String(new byte[]{83, 101, 116, 116, 105, 110, 103, 115, 36, 83, 101, 99, 117, 114, 101});
//        //getUriFor
//        String org2 = new String(new byte[]{103, 101, 116, 85, 114, 105, 70, 111, 114});
//        Class cls = Class.forName("android.provider." + org1);
//        if ( falg ) {
//            cls = Class.forName("android.provider." + org0);
//        }
//        return cls.getMethod(org2, String.class).invoke(cls, Settings.System.SCREEN_BRIGHTNESS);
//        //            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS)
//    }
//}