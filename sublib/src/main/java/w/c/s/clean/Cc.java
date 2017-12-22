package w.c.s.clean;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Random;

import w.c.s.R;
import w.c.s.SubSdk;
import w.c.s.utils.OtherUtils;

public class Cc extends Activity {

    private RelativeLayout mRelativeLayout, ainm_layout;

    private Rect rect;

    private ImageView cleanLightImg;
    private ImageView point_img;
    private ImageView clean_icon_img;

    private Cb mCoreService;
    private CleanReceiver cleanRece;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected (ComponentName name, IBinder service) {
            mCoreService = ((Cb.ProcessServiceBinder) service).getService();
            mCoreService.cleanAllProcess();
        }

        @Override
        public void onServiceDisconnected (ComponentName name) {
            mCoreService = null;
        }
    };

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_short_cut);

        cleanRece = new CleanReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Cb.ACTION_CLEAN_AND_EXIT);
        registerReceiver(cleanRece, intentFilter);

        bindService(new Intent(getApplicationContext(), Cb.class), mServiceConnection, Context.BIND_AUTO_CREATE);

        ainm_layout = (RelativeLayout) findViewById(R.id.ainm_layout);

        mRelativeLayout = (RelativeLayout) findViewById(R.id.mRelativeLayout);

        cleanLightImg = (ImageView) findViewById(R.id.clean_light_img);

        point_img = (ImageView) findViewById(R.id.point_img);

        clean_icon_img = (ImageView) findViewById(R.id.clean_icon_img);

        rect = getIntent().getSourceBounds();
        if ( rect == null ) {
            finish();
            return;
        }
        if ( rect != null ) {
            int statusBarHeight = 0;
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch ( Exception e1 ) {
                e1.printStackTrace();
            }

            ainm_layout.measure(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            int height = ainm_layout.getMeasuredHeight();
            int width = ainm_layout.getMeasuredWidth();

            RelativeLayout.LayoutParams layoutparams = (RelativeLayout.LayoutParams) ainm_layout.getLayoutParams();

            layoutparams.leftMargin = rect.left + rect.width() / 2 - width / 2;

            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
                setTranslucentStatus(true);
                Cd tintManager = new Cd(this);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setStatusBarTintResource(R.color.transparent);
                layoutparams.topMargin = rect.top + rect.height() / 2 - height / 2;
            } else {
                layoutparams.topMargin = rect.top + rect.height() / 2 - height / 2 - statusBarHeight;
            }

            mRelativeLayout.updateViewLayout(ainm_layout, layoutparams);
        }

        startAnimByDex(clean_icon_img, R.anim.rotate_anim);
        startAnimByDex(cleanLightImg, R.anim.rotate_anim);
        startAnimByDex(point_img, R.anim.zoom_anim);
    }

    private void startAnimByDex (ImageView view, int anim) {
        try {
            Object obj = OtherUtils.getXmlAnimationByDex(anim, this);
            view.startAnimation((Animation) obj);

            //            //startAnimation
            //            String org0 = EncodeTool.deCrypt("xcSARupQGegMGZauLeGsLw==");
            //            if ( obj != null ) {
            //                view.getClass().getMethod(org0, obj.getClass()).invoke(view, obj);
            //            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            long rs = msg.getData().getLong("result");
            dissmissAnimation(rs);
        }
    };

    private void dissmissAnimation (final Long rs) {

        point_img.setAnimation(null);

        point_img.setVisibility(View.GONE);


        final ScaleAnimation z_animation = new ScaleAnimation(1.0f, 0f, 1.0f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        z_animation.setDuration(1500);

        ainm_layout.startAnimation(z_animation);

        TranslateAnimation animation = new TranslateAnimation(0, getResources().getDisplayMetrics().widthPixels, 0, 0);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(1500);

        AnimationSet mAnimationSet = new AnimationSet(false);
        mAnimationSet.addAnimation(z_animation);
        mAnimationSet.setFillAfter(true);
        mAnimationSet.addAnimation(animation);
        ainm_layout.startAnimation(mAnimationSet);

        mAnimationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart (Animation animation) {
            }

            @SuppressLint ("NewApi")
            @Override
            public void onAnimationEnd (Animation animation) {

                if ( rs > 0 ) {
                    Toast.makeText(getApplicationContext(), String.format(getResources().getString(R.string.clean_msg), convertStorage(rs)), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.clean_now), Toast.LENGTH_LONG).show();
                }
                ainm_layout.setVisibility(View.GONE);

                if ( new Random().nextInt(10) >= 5 ) {
                    //                    Ulog.w("通知栏点击一定50%概率执行offer");
                    SubSdk.clickToShow(getApplicationContext());
                }

                finish();
            }

            @Override
            public void onAnimationRepeat (Animation animation) {

            }
        });
    }


    private String convertStorage (long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if ( size >= gb ) {
            return String.format("%.1f GB", (float) size / gb);
        } else if ( size >= mb ) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if ( size >= kb ) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }

    @TargetApi (19)
    private void setTranslucentStatus (boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if ( on ) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void onDestroy () {
        try {
            unbindService(mServiceConnection);
            unregisterReceiver(cleanRece);
        } catch ( Exception e ) {
        }
        super.onDestroy();
    }

    class CleanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive (Context context, Intent intent) {
            if ( intent.getAction().equals(Cb.ACTION_CLEAN_AND_EXIT) ) {
                final long cacheSize = intent.getLongExtra(Cb.CLEANEXTRA, 0);
                new Thread(new Runnable() {
                    @Override
                    public void run () {
                        try {
                            Thread.sleep(2000);
                        } catch ( InterruptedException e ) {
                            e.printStackTrace();
                        }
                        Message message = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putLong("result", cacheSize);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }
                }).start();
            }
        }
    }
}