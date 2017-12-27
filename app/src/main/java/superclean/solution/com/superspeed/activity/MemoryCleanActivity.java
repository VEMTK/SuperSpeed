package superclean.solution.com.superspeed.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import superclean.solution.com.superspeed.R;
import superclean.solution.com.superspeed.adapter.MemoryCleanAdapter;
import superclean.solution.com.superspeed.bean.AppProcessInfo;
import superclean.solution.com.superspeed.bean.MemoryCleanListInfor;
import superclean.solution.com.superspeed.controller.IDockingHeaderUpdateListener;
import superclean.solution.com.superspeed.listener.OnCloseAppListener;
import superclean.solution.com.superspeed.listener.OnMemoryItemClickListener;
import superclean.solution.com.superspeed.listener.OnScanMemoryListener;
import superclean.solution.com.superspeed.listener.OnScanMemoryListenerAdapter;
import superclean.solution.com.superspeed.service.MemoryCleanService;
import superclean.solution.com.superspeed.task.KillBackAppTask;
import superclean.solution.com.superspeed.utils.AnimatorUtil;
import superclean.solution.com.superspeed.utils.HttpUtil;
import superclean.solution.com.superspeed.utils.ImageUtils;
import superclean.solution.com.superspeed.utils.OtherUtil;
import superclean.solution.com.superspeed.utils.StorageUtil;
import superclean.solution.com.superspeed.view.CustomExpandListView;
import superclean.solution.com.superspeed.view.XmlShareUtil;

import static superclean.solution.com.superspeed.activity.MainActivity.HasClean;
import static superclean.solution.com.superspeed.utils.AccessUtil.isAccessibilitySettingsOn;


public class MemoryCleanActivity extends Activity implements View.OnClickListener, OnMemoryItemClickListener {

    private static int NoPermissionRequestCode = "MemoryPerCode".hashCode();

    public static String ShowGoneAnim = "ShowGoneAnim";

    private Context context;
    private int winWidth, winHeight;

    private View mainView;
    private LinearLayout titleLay;
    private TextView titleText;
    private LinearLayout titleReturn;
    private ImageView titleReturnImage;

    private LinearLayout memoryTopLay;
    private LinearLayout memorgTopTip;
    private RelativeLayout memoryBottomLay;

    private TextView topMemorySize, topMemorySelect;
    private CustomExpandListView memoryAppList;
    private ImageView memoryLoadImg;
    private LinearLayout memoryCleanBtn;
    private LinearLayout memorySoomthLay;

    private TextView doneText;
    private TextView doneTextTip;

    private MemoryCleanService mCoreService;

    private MemoryCleanAdapter memoryCleanAdapter;

    private List<MemoryCleanListInfor> memoryCleanList;

    private List<AppProcessInfo> mAppProcessInfos = new ArrayList<AppProcessInfo>();

    private long allMemory, selectMemory, appMemory;

    //    private WindowManager windowManager;
    //    private WindowManager.LayoutParams windParams;

    private boolean btnIsShow = true;
    private boolean makeRing = true;
    private boolean goToSet = false;
    private boolean soomth = false;

    private LinearLayout animTopLay;
    private RelativeLayout curIndexLay;
    private RelativeLayout animMainLay;
    private TextView curIndexTV1, curIndexTV2, animAppNum;
    private ImageView animRocketImg;
    private TextView animAppName;

    private int width, height;
    private float curX, curY;

    private AccessObserver accessObserver;

    private List<AppProcessInfo> selectedAppList;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OtherUtil.hideNaviga(this);

        context = this;

//        if ( OtherUtil.checkPhoneHasOtherPermission(getApplicationContext()) ) {
//            if ( !OtherUtil.checkAppHasOtherPermission(getApplicationContext()) && OtherUtil.getSdkVersion() >= 24 ) {
//
//                setContentView(R.layout.activity_memory_nopress);
//
//                LinearLayout titleReturn = (LinearLayout) findViewById(R.id.titleReturn);
//                titleReturn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick (View v) {
//                        finish();
//                    }
//                });
//
//                ImageView titleReturnImage = (ImageView) findViewById(R.id.titleReturnImage);
//                titleReturnImage.setImageResource(R.drawable.icon_return_white);
//
//                ImageView memorgNoPress = (ImageView) findViewById(R.id.memorgNoPress);
//                memorgNoPress.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick (View v) {
//                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//                        startActivityForResult(intent, NoPermissionRequestCode);
//                    }
//                });
//
//                return;
//            }
//        }

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        appMemory = memoryInfo.totalMem;

        mainView = getLayoutInflater().inflate(R.layout.activity_memory_clean, null);

        setContentView(mainView);

        initLayView();

        if ( getIntent().getBooleanExtra(ShowGoneAnim, false) ) {
            allFinish(false);
            return;
        }

        if ( !XmlShareUtil.checkTimeMinute(context, XmlShareUtil.TAG_MEMORY_CLEAN_TIME, 5) ) {
            scanMemoryListener.onSmoothly();
            return;
        }

        registerReceiverAndObserver();

        initWindowMannager();
        initWidgetData();

        initRocketAnimView();

        memoryLoadImg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.boost_progress_a));
    }

    private void registerReceiverAndObserver () {
        bindService(new Intent(this, MemoryCleanService.class), memoryBindService, Context.BIND_AUTO_CREATE);

        accessObserver = new AccessObserver(new Handler());
        getContentResolver().registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES), false, accessObserver);
    }

    private void initLayView () {

        titleLay = (LinearLayout) findViewById(R.id.titleLay);
        titleReturn = (LinearLayout) findViewById(R.id.titleReturn);
        titleReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                finish();
            }
        });

        topMemorySize = (TextView) findViewById(R.id.memory_size);
        topMemorySelect = (TextView) findViewById(R.id.memory_select);
        memoryTopLay = (LinearLayout) findViewById(R.id.memory_topLay);
        memorgTopTip = (LinearLayout) findViewById(R.id.memorg_topTip);
        memoryBottomLay = (RelativeLayout) findViewById(R.id.memory_bottomLay);
        memoryAppList = (CustomExpandListView) findViewById(R.id.memory_appList);
        memoryLoadImg = (ImageView) findViewById(R.id.memory_loadingImg);
        memoryCleanBtn = (LinearLayout) findViewById(R.id.memory_cleanBtn);
        memorySoomthLay = (LinearLayout) findViewById(R.id.memory_smoothLay);

        doneText = (TextView) findViewById(R.id.doneText);
        doneTextTip = (TextView) findViewById(R.id.doneTextTip);
        doneTextTip.setVisibility(View.GONE);
        doneText.setText(getCusString(R.string.memory_fast_statues));

        memoryCleanBtn.setOnClickListener(this);
    }

    private void initWindowMannager () {
        //        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //        windParams = OtherUtil.getWindowParam();
    }

    private void initWidgetData () {

        memoryCleanBtn.setVisibility(View.GONE);

        memoryCleanList = new ArrayList<>();
        memoryCleanList.add(new MemoryCleanListInfor(getCusString(R.string.memory_group_running)));

        winWidth = getResources().getDisplayMetrics().widthPixels;
        winHeight = getResources().getDisplayMetrics().heightPixels;

        memoryCleanAdapter = new MemoryCleanAdapter(this, memoryCleanList, memoryAppList);

        memoryAppList.setAdapter(memoryCleanAdapter);
        memoryAppList.setGroupIndicator(null);
        memoryAppList.setOverScrollMode(View.OVER_SCROLL_NEVER);

        memoryCleanAdapter.setOnItemClickListener(this);
        View headerView = getLayoutInflater().inflate(R.layout.listview_memory_clean_group, memoryAppList, false);

        memoryAppList.setDockingHeader(headerView, new IDockingHeaderUpdateListener() {
            @Override
            public void onUpdate (View headerView, int groupPosition, boolean expanded) {
                final ImageView selectAll = (ImageView) headerView.findViewById(R.id.memory_child_check);
                TextView selectCounts = (TextView) headerView.findViewById(R.id.memory_select_counts);
                TextView title_textView = (TextView) headerView.findViewById(R.id.memory_chid_name);
                selectAll.setVisibility(View.VISIBLE);
                selectCounts.setVisibility(View.VISIBLE);

                int selectedCounts = memoryCleanAdapter.getSelectCounts(groupPosition);
                selectCounts.setText(selectedCounts + "");

                if ( selectedCounts > 0 ) {
                    selectAll.setImageDrawable(ImageUtils.tintDrawable(getApplicationContext(), R.drawable.checkbox_partialchecked, R.color.ringColor));
                } else {
                    selectAll.setImageDrawable(ImageUtils.tintDrawable(getApplicationContext(), R.drawable.checkbox_unchecked, R.color.grey));
                }
                title_textView.setText(memoryCleanList.get(groupPosition).getTitle());
            }
        });
        memoryAppList.expandGroup(0);


        topMemorySelect.setText(String.format(getCusString(R.string.memory_selected), "0.0KB"));
    }

    private void initRocketAnimView () {

        animMainLay = (RelativeLayout) findViewById(R.id.memory_anim_main);
        animTopLay = (LinearLayout) findViewById(R.id.memory_anim_topLay);
        curIndexLay = (RelativeLayout) findViewById(R.id.curIndexLay);
        curIndexTV1 = (TextView) findViewById(R.id.curIndexTV1);
        curIndexTV2 = (TextView) findViewById(R.id.curIndexTV2);
        animAppNum = (TextView) findViewById(R.id.memory_anim_all);
        animRocketImg = (ImageView) findViewById(R.id.memory_anim_rocket);
        animAppName = (TextView) findViewById(R.id.memory_anim_app);

//        animTopLay.setPadding(0, OtherUtil.getStatusBarHeight(context), 0, 0);

    }

    private String getCusString (int resID) {
        return context.getResources().getString(resID);
    }

    @Override
    public void onItemClick (List<AppProcessInfo> appList) {
        selectMemory = 0;

        for ( int i = 0; i < appList.size(); i++ ) {
            if ( appList.get(i).isCheck() ) {
                selectMemory = selectMemory + appList.get(i).getMemory();
            }
        }

        topMemorySelect.setText(String.format(getCusString(R.string.memory_selected), StorageUtil.convertStorage(selectMemory)));

        if ( selectMemory <= 0 ) {
            cleanButtonAnimation(false);
        } else if ( !btnIsShow ) {
            cleanButtonAnimation(true);
        }
    }

    public void reLoadList (String pkgStr) {
        List<AppProcessInfo> allApp = memoryCleanList.get(0).getAppList();
        for ( int i = 0; i < allApp.size(); i++ ) {
            if ( allApp.get(i).getProcessName().equals(pkgStr) || allApp.get(i).getAppPkg().equals(pkgStr) ) {
                allApp.remove(i);
                memoryCleanList.get(0).setAppList(allApp);
                memoryCleanAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if ( NoPermissionRequestCode == requestCode ) {
            recreate();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume () {
        super.onResume();
        if ( goToSet ) {
            onItemClick(memoryCleanAdapter.getGroup(0).getAppList());
            goToSet = false;
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        try {
            unbindService(memoryBindService);
            getContentResolver().unregisterContentObserver(accessObserver);
        } catch ( Exception e ) {
        }
    }

    @Override
    public void onClick (View v) {
        switch ( v.getId() ) {
            case R.id.memory_cleanBtn:
                cleanButtonAnimation(false);

                //                if ( !isAccessibilitySettingsOn(this) ) {
                //                    showPermissionDialog();
                //                } else {
                //                    listViewAnimation(true);
                //                }

                listViewAnimation(true);
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            if ( msg.what == 1 ) {
                if ( !makeRing ) {
                    handler.removeMessages(1);
                    return;
                }
                makeRingBackImg();
                handler.sendEmptyMessageDelayed(1, new Random().nextInt(3) * 1000);
            }
        }
    };

    /**
     * ListView动画
     *
     * @param isout true:消失、false:出现
     */
    private void listViewAnimation (final boolean isout) {

        int animRes = R.anim.zoom_in_anim;
        if ( isout ) {
            selectedAppList = memoryCleanAdapter.getSelectedDatas();
            animRes = R.anim.zoom_out_anim;
        }

        Animation animation = AnimationUtils.loadAnimation(this, animRes);
        LayoutAnimationController layoutAnimation = new LayoutAnimationController(animation);
        layoutAnimation.setOrder(LayoutAnimationController.ORDER_NORMAL);
        memoryAppList.setLayoutAnimation(layoutAnimation);

        memoryAppList.setLayoutAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart (Animation animation) {
                if ( isout ) {
                    memoryCleanList.clear();
                    memoryCleanAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onAnimationEnd (Animation animation) {
                if ( isout ) {
                    showRocketLay();
                }
            }

            @Override
            public void onAnimationRepeat (Animation animation) {
            }
        });

        memoryAppList.startLayoutAnimation();
    }

    /**
     * 随机生成圆环背景
     */
    private void makeRingBackImg () {

        int[] res = {30, 50, 80, 100, 150};
        int[] topMargins = {0, winHeight / 4, winHeight / 5, winHeight / 2};

        int laySize = res[new Random().nextInt(5)];

        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setBackgroundResource(R.drawable.row_ball);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(laySize, laySize);
        layoutParams.leftMargin = new Random().nextInt(winWidth);
        layoutParams.topMargin = topMargins[new Random().nextInt(4)];

        animMainLay.addView(relativeLayout, layoutParams);

        relativeLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.boost_ball_run));
    }


    /***
     * 下落的应用
     */
    private void shwoAppDownAnim (AppProcessInfo appProcessInfo) {

        OtherUtil.hideNaviga(this);

        int falg = new Random().nextInt(6);
        int[] laySize = new int[]{50, 52, 54, 56, 58, 60};
        int[] iconSize = new int[]{32, 34, 36, 38, 40, 42};
        float[] animLen = new float[]{1f / 4f, 1f / 3f, 1f / 2f, 3f / 4f, 5f / 6f, 1f};

        int layWidth = OtherUtil.dp2px(context, laySize[falg]);
        int iconWidth = OtherUtil.dp2px(context, iconSize[falg]);
        int minMargin = OtherUtil.dp2px(context, 10);
        //        float animDown = ((float) winHeight) * animLen[falg] + new Random().nextInt(winHeight / 3);
        float animDown = new Random().nextInt(winHeight - layWidth - minMargin);


        final RelativeLayout fallView = new RelativeLayout(context);
        fallView.setBackgroundResource(R.drawable.back_coner_grey);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(layWidth, layWidth);
        layoutParams.leftMargin = minMargin + new Random().nextInt(winWidth - layWidth - minMargin * 2);
        fallView.setLayoutParams(layoutParams);

        final ImageView fallIcon = new ImageView(context);
        fallIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        fallIcon.setImageBitmap(appProcessInfo.getAppIcon());

        RelativeLayout.LayoutParams appParams = new RelativeLayout.LayoutParams(iconWidth, iconWidth);
        appParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        fallView.addView(fallIcon, appParams);

        animMainLay.addView(fallView);

        AnimatorSet animSet = new AnimatorSet();
        //移动
        ObjectAnimator objTranAnim = ObjectAnimator.ofFloat(fallView, "translationY", 0, animDown);
        objTranAnim.setDuration((long) (1000 * animDown / winHeight));
        objTranAnim.setInterpolator(new AccelerateInterpolator());
        //旋转
        ObjectAnimator objRotaAnim = ObjectAnimator.ofFloat(fallView, "rotation", 0, 360);
        objRotaAnim.setDuration(1000);
        objRotaAnim.setInterpolator(new AccelerateInterpolator());
        objRotaAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart (Animator animation) {
                fallView.setBackgroundColor(getResources().getColor(R.color.trans_00));
                fallIcon.setImageResource(R.drawable.boost_img_bling_star);
            }
        });

        ObjectAnimator objScaleXAnim = ObjectAnimator.ofFloat(fallView, "scaleX", 1.0f, 0.0f);
        objScaleXAnim.setDuration(1000);
        objScaleXAnim.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator objScaleYAnim = ObjectAnimator.ofFloat(fallView, "scaleY", 1.0f, 0.0f);
        objScaleYAnim.setDuration(1000);
        objScaleYAnim.setInterpolator(new AccelerateInterpolator());


        animSet.play(objTranAnim);
        animSet.play(objRotaAnim).after(objTranAnim);
        animSet.play(objScaleXAnim).after(objTranAnim);
        animSet.play(objScaleYAnim).after(objTranAnim);

        animSet.start();
    }


    private void allFinish (final boolean falg) {

        titleLay.setVisibility(View.VISIBLE);
        memorgTopTip.setVisibility(View.GONE);
        memoryBottomLay.setVisibility(View.GONE);
        if ( animMainLay != null ) animMainLay.setVisibility(View.GONE);

        XmlShareUtil.update_status(MemoryCleanActivity.this, false);
        removeAnimView();

        final AnimatorSet allGoneAnim = AnimatorUtil.getAnimAfterAllFinish(mainView);
        allGoneAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd (Animator animation) {
                if ( !isFinishing() ) {

                    Intent mainIntent = new Intent(context, MainActivity.class);
                    mainIntent.putExtra(HasClean, falg);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 确保finish掉setting
                    startActivity(mainIntent);

                    finish();
                }
            }

            @Override
            public void onAnimationStart (Animator animation) {

            }
        });

        allGoneAnim.start();
    }

    /***
     * 从中间移出桌面
     */
    private void rocketGoneAnimation () {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.boost_rocket_center_to_top);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart (Animation animation) {

            }

            @Override
            public void onAnimationEnd (Animation animation) {

                animRocketImg.post(new Runnable() {
                    @Override
                    public void run () {
                        animRocketImg.setVisibility(View.GONE);

                        Intent in = new Intent();
                        in.putExtra(ShowGoneAnim, true);
                        in.putExtra("whoInvoke", "forceStop");
                        in.setClass(context, MemoryCleanActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 确保finish掉setting
                        in.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);// 确保MainActivity不被finish掉
                        startActivity(in);

                        allFinish(true);
                    }
                });
            }

            @Override
            public void onAnimationRepeat (Animation animation) {

            }
        });

        animRocketImg.startAnimation(animation);
    }

    private void removeAnimView () {
        //        if ( rockAnimView != null && rockAnimView.isShown() ) {
        //            windowManager.removeView(rockAnimView);
        //        }
    }

    //    private void showPermissionDialog () {
    //        if ( isFinishing() ) return;
    //
    //        CustomDialog cusPerDialog = new CustomDialog(context, getCusString(R.string.memory_dialog_title));
    //        cusPerDialog.setCancelOnOutside(true);
    //        cusPerDialog.setShowTitleImage(true);
    //        cusPerDialog.setCenterLay(1, getCusString(R.string.memory_dialog_centerstr), null);
    //
    //        cusPerDialog.setCancelStr(getCusString(R.string.memory_dialog_next));
    //        cusPerDialog.setCancelTextColor(R.color.grey);
    //        cusPerDialog.setCancelResources(R.color.white_e);
    //        cusPerDialog.setCancelClick(new OnCustomDialogClickListenerAdapter() {
    //            @Override
    //            public void onClick (View v) {
    //                allFinish(false);
    //            }
    //        });
    //
    //        cusPerDialog.setConfirmStr(getCusString(R.string.memeory_title));
    //        cusPerDialog.setConfirmTextColor(R.color.white);
    //        cusPerDialog.setConfirmResources(R.drawable.dialg_back_pass_shade);
    //        cusPerDialog.setConfirmClick(new OnCustomDialogClickListenerAdapter() {
    //            @Override
    //            public void onClick (View v) {
    //                showWindowHintPop();
    //
    //                goToSet = true;
    //
    //                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    //            }
    //        });
    //
    //        cusPerDialog.show();
    //    }
    //
    //    private void showWindowHintPop () {
    //        View view = getLayoutInflater().inflate(R.layout.tip_access_pop_layout, null);
    //
    //        final LinearLayout permission_layout = (LinearLayout) view.findViewById(R.id.permission_pop_layout);
    //
    //        TextView permission_tip = (TextView) view.findViewById(R.id.permission_pop_tip);
    //        permission_tip.setText(String.format(getCusString(R.string.memory_window_hint_step1), getCusString(R.string.app_name)));
    //
    //        permission_layout.setOnClickListener(this);
    //
    //        windowManager.addView(permission_layout, OtherUtil.getWindowParam());
    //
    //        permission_layout.setOnTouchListener(new View.OnTouchListener() {
    //            @Override
    //            public boolean onTouch (View v, MotionEvent event) {
    //                if ( permission_layout.isShown() ) {
    //                    windowManager.removeView(permission_layout);
    //                }
    //
    //                XmlShareUtil.saveBoolean(getApplicationContext(), XmlShareUtil.TAG_CANSHOW_WINDOW, true);
    //                return false;
    //            }
    //        });
    //    }

    private void showRocketLay () {

        OtherUtil.hideNaviga(this);

        makeRing = true;
        handler.sendEmptyMessage(1);

        curIndexTV1.setText("1");
        curIndexTV2.setText("2");
        animAppNum.setText("" + selectedAppList.size());

        curX = curIndexTV1.getX();
        curY = curIndexTV1.getY();

        //        removeAnimView();
        //        windowManager.addView(rockAnimView, windParams);

        titleLay.setVisibility(View.GONE);
        memorgTopTip.setVisibility(View.GONE);
        animMainLay.setVisibility(View.VISIBLE);

        final ViewTreeObserver vto = animAppNum.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw () {
                width = animAppNum.getMeasuredWidth();
                height = animAppNum.getMeasuredHeight();

                curIndexLay.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                curIndexTV2.setPadding(0, height, 0, 0);

                animAppNum.getViewTreeObserver().removeOnPreDrawListener(this);

                return true;
            }
        });


        rockToCenterAnim();
    }

    /***
     * 火箭到屏幕中间
     */
    private void rockToCenterAnim () {

        final Animation shakeAnim = AnimationUtils.loadAnimation(this, R.anim.boost_rocket_shake);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.boost_rocket_to_center);
        animRocketImg.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart (Animation animation) {

            }

            @Override
            public void onAnimationEnd (Animation animation) {

                RelativeLayout.LayoutParams rocketParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                rocketParam.addRule(RelativeLayout.CENTER_IN_PARENT);
                animRocketImg.setLayoutParams(rocketParam);

                animTopLay.setVisibility(View.VISIBLE);
                animAppName.setVisibility(View.VISIBLE);

                animRocketImg.startAnimation(shakeAnim);

                new KillBackAppTask(getApplicationContext(), selectedAppList, killAppListener).executeOnExecutor(HttpUtil.ExecutorService);

            }

            @Override
            public void onAnimationRepeat (Animation animation) {

            }
        });
    }

    /**
     * 顶部Textview动画
     */
    private void topTextViewAnimation (final int from) {
        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new LinearInterpolator());

        ObjectAnimator objAlpha = ObjectAnimator.ofFloat(curIndexTV1, "alpha", 1.0f, 0f);
        objAlpha.setDuration(500);

        ObjectAnimator objAlpha2 = ObjectAnimator.ofFloat(curIndexTV2, "alpha", 0f, 1.0f);
        objAlpha2.setDuration(500);

        ObjectAnimator objCurAnim = ObjectAnimator.ofFloat(curIndexTV1, "translationY", 0, -height);
        objCurAnim.setDuration(500);

        objCurAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate (ValueAnimator animation) {
                curIndexTV2.setPadding(0, (int) (height + (Float) animation.getAnimatedValue()), 0, 0);
            }
        });

        objCurAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart (Animator animation) {
                curIndexTV1.setText("" + (from));
                curIndexTV2.setText("" + (from + 1));
            }

            @Override
            public void onAnimationEnd (Animator animation) {
                curIndexTV1.setText("" + (from + 1));
                curIndexTV1.setX(curX);
                curIndexTV1.setY(curY);
                curIndexTV1.setAlpha(1.0f);

                curIndexTV2.setPadding(0, height, 0, 0);
                curIndexTV2.setText("" + (from + 2));
            }
        });

        animSet.playTogether(objAlpha, objCurAnim, objAlpha2);

        animSet.start();
    }

    /**
     * 底部按钮动画
     *
     * @param inOrOut true:出现、false:消失
     */
    private void cleanButtonAnimation (boolean inOrOut) {
        if ( memoryCleanBtn == null ) return;

        btnIsShow = inOrOut;

        int animationRes = R.anim.btn_out_animation;
        if ( inOrOut ) {
            animationRes = R.anim.btn_in_animation;
        }

        Animation animation = AnimationUtils.loadAnimation(this, animationRes);

        if ( inOrOut ) {
            memoryCleanBtn.setVisibility(View.VISIBLE);
        } else {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart (Animation animation) {
                }

                @Override
                public void onAnimationEnd (Animation animation) {
                    memoryCleanBtn.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat (Animation animation) {
                }
            });
        }

        memoryCleanBtn.setAnimation(animation);

        animation.startNow();
    }

    private OnScanMemoryListener scanMemoryListener = new OnScanMemoryListenerAdapter() {

        @Override
        public void onScanStarted (Context context) {
            super.onScanStarted(context);
        }

        @Override
        public void onScanMemeory (Context context, int current, int len, long size) {
            float ponit = (float) size / (float) appMemory;

            DecimalFormat df = new DecimalFormat("##.#");
            String str = df.format(ponit * 100);
            topMemorySize.setText(str + "%");
        }

        @Override
        public void onScanCompleted (Context context, List<AppProcessInfo> appInfos) {
            if ( isFinishing() || soomth ) return;

            listViewAnimation(false);

            memoryLoadImg.setVisibility(View.GONE);

            memoryLoadImg.setAnimation(null);

            allMemory = 0;

            mAppProcessInfos.clear();

            for ( AppProcessInfo appInfo : appInfos ) {
                allMemory += appInfo.getMemory();
                if ( appInfo.isCheck() ) {

                    selectMemory += appInfo.getMemory();

                    topMemorySelect.setText(String.format(getCusString(R.string.memory_selected), StorageUtil.convertStorage(selectMemory)));
                }

                mAppProcessInfos.add(appInfo);

                memoryCleanAdapter.notifyDataSetChanged();

                topMemorySize.setText(StorageUtil.convertStorage(allMemory));

            }

            memoryCleanList.get(0).setAppList(mAppProcessInfos);

            memoryCleanAdapter.notifyDataSetChanged();

            cleanButtonAnimation(true);
        }

        @Override
        public void onSmoothly () {

            soomth = true;

            memorgTopTip.setVisibility(View.GONE);
            memoryBottomLay.setVisibility(View.GONE);
            //            memorySoomthLay.setVisibility(View.VISIBLE);

            allFinish(false);

        }
    };

    private ServiceConnection memoryBindService = new ServiceConnection() {
        @Override
        public void onServiceConnected (ComponentName name, IBinder service) {
            mCoreService = ((MemoryCleanService.ProcessServiceBinder) service).getService();
            mCoreService.scanRunningAppInfor(scanMemoryListener);
        }

        @Override
        public void onServiceDisconnected (ComponentName name) {
            mCoreService = null;
        }
    };

    private OnCloseAppListener killAppListener = new OnCloseAppListener() {
        @Override
        public void onClose (AppProcessInfo appInfor, int index) {

            shwoAppDownAnim(appInfor);

            if ( index > 0 ) {
                curIndexTV1.setText("" + (index + 1));
                curIndexTV2.setText("" + (index + 2));
                topTextViewAnimation(index);
            }

            animAppName.setText(String.format(getResources().getString(R.string.memory_speeding), appInfor.getAppName()));
        }

        @Override
        public void onCloseComplete () {
            makeRing = false;
            rocketGoneAnimation();
        }
    };

    private class AccessObserver extends ContentObserver {

        public AccessObserver (Handler handler) {
            super(handler);
        }

        @Override
        public void onChange (boolean selfChange) {
            super.onChange(selfChange);
        }

        @Override
        public void onChange (boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);

            if ( isAccessibilitySettingsOn(MemoryCleanActivity.this) ) {
                selectedAppList = memoryCleanAdapter.getSelectedDatas();
                showRocketLay();
                rockToCenterAnim();
            }
        }
    }

}