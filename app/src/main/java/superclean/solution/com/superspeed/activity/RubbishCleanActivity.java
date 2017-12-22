package superclean.solution.com.superspeed.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import superclean.solution.com.superspeed.R;
import superclean.solution.com.superspeed.adapter.RubbishCleanExpandAdapter;
import superclean.solution.com.superspeed.bean.AppProcessInfo;
import superclean.solution.com.superspeed.bean.RubbishItemInfor;
import superclean.solution.com.superspeed.controller.IDockingHeaderUpdateListener;
import superclean.solution.com.superspeed.listener.OnCleanRubbishListener;
import superclean.solution.com.superspeed.listener.OnRubbishClickListener;
import superclean.solution.com.superspeed.listener.OnScanRubbishListener;
import superclean.solution.com.superspeed.service.RubbishService;
import superclean.solution.com.superspeed.utils.OtherUtil;
import superclean.solution.com.superspeed.utils.StorageUtil;
import superclean.solution.com.superspeed.view.CustomExpandListView;
import superclean.solution.com.superspeed.view.XmlShareUtil;

import static android.animation.ObjectAnimator.ofFloat;

/**
 * Created by admin on 2017/10/17.
 */

public class RubbishCleanActivity extends Activity {

    private static final int WHAT_CLEANIMG = 105;
    private static final int WHAT_FALL_APP = 104;
    private static final int WHAT_ADD_ROUND = 103;
    private static final int WHAT_START_ANIM = 102;

    private Context context;

    private TextView titleText;
    private LinearLayout titleReturn;
    private ImageView titleReturnImage;

    private LinearLayout rubbishMainLay;
    private LinearLayout rubbishTopLay;
    private TextView rubbishTotalTV;
    private TextView rubbishSelectTV;
    private TextView rubbishFileBack;
    private FrameLayout rubbishCleanLay;
    private CustomExpandListView rubbishListView;
    private LinearLayout rubbishCleanBtn;
    private RelativeLayout rubbishCleanAnimLay;
    private RelativeLayout rubbishDrapBallLay;
    private ImageView rubbishAnimImg;
    private TextView rubbishAnimTV;
    private TextView rubbishAnimTip;

    private int animEndY, animEndX;
    private int winWidth, winHeight, topMagin = 0;
    private boolean lastSelectStatus = true;

    private List<RubbishItemInfor> groupListData = new ArrayList<RubbishItemInfor>();

    private RubbishCleanExpandAdapter rubbishCleanAdapter;

    private RubbishService rubbishService;

    private boolean scanStatus = false, canFinish = false;

    private long firstTime = 0;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected (ComponentName name, IBinder service) {
            rubbishService = ((RubbishService.ProcessServiceBinder) service).getService();
            rubbishService.scanRubbish(scanListener);
        }

        @Override
        public void onServiceDisconnected (ComponentName name) {
            rubbishService = null;
        }
    };


    private Handler rubHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            if ( msg.what == WHAT_START_ANIM ) {
                if ( !canFinish ) {
                    if ( animEndY > 0 ) {
                        rubHandler.sendEmptyMessage(WHAT_FALL_APP);
                        for ( int i = 0; i < 6; i++ ) {
                            Message mes = new Message();
                            mes.what = WHAT_ADD_ROUND;
                            mes.arg1 = i % 2;
                            rubHandler.sendMessageDelayed(mes, new Random().nextInt(50) + 50);
                        }
                    }
                    rubHandler.sendEmptyMessageDelayed(WHAT_START_ANIM, 800);
                }
            } else if ( msg.what == WHAT_ADD_ROUND ) {
                addCircleAnim(msg.arg1);
            } else if ( msg.what == WHAT_FALL_APP ) {
                fallDownIcon();
            } else if ( msg.what == WHAT_CLEANIMG ) {
                canFinish = true;
                //                cleanImgeViewAnim();
            }
        }
    };

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rubbish_clean);

        context = this;

        initViewAndData();


        if ( XmlShareUtil.checkCleanRubbishTime(this) ) {
            bindService(new Intent(this, RubbishService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            rubbishAnimTV.setText(getCusString(R.string.clean_rubbish_no));
            rubbishAnimTip.setVisibility(View.GONE);
            showCleanAnimView(true);
        }
    }

    private void initViewAndData () {

        titleReturn = (LinearLayout) findViewById(R.id.titleReturn);
        titleReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                finish();
            }
        });
        titleReturnImage = (ImageView) findViewById(R.id.titleReturnImage);
        titleReturnImage.setImageResource(R.drawable.icon_return_white);

        rubbishMainLay = (LinearLayout) findViewById(R.id.rubbish_MainLay);
        rubbishTopLay = (LinearLayout) findViewById(R.id.rubbish_TopLay);
        rubbishTotalTV = (TextView) findViewById(R.id.rubbish_TotalTV);
        rubbishSelectTV = (TextView) findViewById(R.id.rubbish_SelectTV);
        rubbishFileBack = (TextView) findViewById(R.id.rubbish_FileBack);

        rubbishCleanLay = (FrameLayout) findViewById(R.id.app_LockLay);
        rubbishListView = (CustomExpandListView) findViewById(R.id.rubbish_List);
        rubbishCleanBtn = (LinearLayout) findViewById(R.id.rubbish_CleanBtn);

        rubbishCleanAnimLay = (RelativeLayout) findViewById(R.id.rubbish_CleanAnimLay);
        rubbishDrapBallLay = (RelativeLayout) findViewById(R.id.rubbish_DrapBallLay);
        rubbishAnimTV = (TextView) findViewById(R.id.rubbish_AnimTV);
        rubbishAnimTip = (TextView) findViewById(R.id.rubbish_AnimTip);
        rubbishAnimImg = (ImageView) findViewById(R.id.rubbish_AnimImg);

        winWidth = (int) OtherUtil.getPhoneScreenSize(context, 2);
        winHeight = (int) OtherUtil.getPhoneScreenSize(context, 3);
        topMagin = OtherUtil.dp2px(context, 50);

        rubbishCleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                showCleanAnimView(false);
                XmlShareUtil.save_cleanRubbishTime(context);
            }
        });

        rubbishCleanAdapter = new RubbishCleanExpandAdapter(this, rubbishListView, groupListData, adapterListener);
        rubbishListView.setGroupIndicator(null);
        rubbishListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rubbishListView.setAdapter(rubbishCleanAdapter);

        View headerView = getLayoutInflater().inflate(R.layout.listview_rubbish_groud, rubbishListView, false);
        rubbishListView.setDockingHeader(headerView, new IDockingHeaderUpdateListener() {
            @Override
            public void onUpdate (View headerView, int groupPosition, boolean expanded) {

                ImageView imageView = (ImageView) headerView.findViewById(R.id.rubbish_group_direct);
                if ( rubbishListView.isGroupExpanded(groupPosition) ) {
                    imageView.setRotation(180);
                } else {
                    imageView.setRotation(0);
                }

                TextView titleView = (TextView) headerView.findViewById(R.id.rubbish_group_name);
                titleView.setText(rubbishCleanAdapter.getGroup(groupPosition).getName());

                ImageView radioButton = (ImageView) headerView.findViewById(R.id.rubbish_group_check);
                radioButton.setImageDrawable(rubbishCleanAdapter.getSelectCountRes(groupPosition));

                TextView textView = (TextView) headerView.findViewById(R.id.rubbish_group_size);
                textView.setText(StorageUtil.convertStorage(rubbishCleanAdapter.getGroup(groupPosition).getGroupSize(false)));

                ImageView icon = (ImageView) headerView.findViewById(R.id.rubbish_group_icon);
                icon.setImageResource(rubbishCleanAdapter.getGroup(groupPosition).getIconRes());
            }
        });
    }

    private String getCusString (int resID) {
        return context.getResources().getString(resID);
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        try {
            unbindService(mServiceConnection);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void showCleanAnimView (boolean noRubbish) {
        rubbishMainLay.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rubbishTopLay.setVisibility(View.GONE);
        rubbishCleanAnimLay.setVisibility(View.VISIBLE);

        ViewTreeObserver vto2 = rubbishAnimImg.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi (api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout () {
                animEndY = rubbishAnimImg.getHeight();
                animEndX = rubbishAnimImg.getWidth();
                rubbishAnimImg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        if ( noRubbish ) {
            cleanImgeViewAnim();
        } else {
            rubbishService.cleanRubbish(groupListData, cleanListener);
        }
    }

    //小圆运动动画
    private void addCircleAnim (int direc) {
        final float[] mCurrentPosition = new float[2];
        int imgSize = OtherUtil.dp2px(context, 10);
        final ImageView goods = new ImageView(this);
        goods.setImageResource(R.drawable.back_coner_white);
        RelativeLayout.LayoutParams imgParam = new RelativeLayout.LayoutParams(imgSize, imgSize);
        rubbishDrapBallLay.addView(goods, imgParam);

        //动画起始点坐标
        float fromX = new Random().nextInt(imgSize * 15) - imgSize * 5;
        if ( direc == 1 ) {
            fromX = new Random().nextInt(imgSize * 15) + winWidth - imgSize * 10;
        }
        float fromY = new Random().nextInt(imgSize * 5);
        if ( fromX > 0 && fromX < winWidth ) {
            fromY = -imgSize;
        }
        float toX = (winWidth - animEndX) / 2 + imgSize + new Random().nextInt(animEndX / 2 - imgSize * 2);
        if ( fromX > imgSize * 3 ) {
            toX = new Random().nextInt(animEndX / 2) + winWidth / 2;
        }
        float toY = (winHeight - animEndY - topMagin) / 2;

        Path path = new Path();
        path.moveTo(fromX, fromY);
        path.quadTo((fromX), (fromY + toY) / 2, toX, toY);

        final PathMeasure mPathMeasure = new PathMeasure(path, false);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new AccelerateInterpolator());

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate (ValueAnimator animation) {
                if ( canFinish ) {
                    rubbishDrapBallLay.setVisibility(View.GONE);
                } else {
                    float value = (Float) animation.getAnimatedValue();
                    mPathMeasure.getPosTan(value, mCurrentPosition, null);
                    goods.setTranslationX(mCurrentPosition[0]);
                    goods.setTranslationY(mCurrentPosition[1]);
                }
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd (Animator animation) {
                goods.setVisibility(View.GONE);
                if ( canFinish ) {
                    canFinish = false;

                    rubbishDrapBallLay.setVisibility(View.GONE);

                    cleanImgeViewAnim();
                }
            }
        });

        if ( !canFinish ) valueAnimator.start();
    }

    //下落的图标动画
    private void fallDownIcon () {

        int vsize = OtherUtil.dp2px(context, 50);
        int left = new Random().nextInt(winWidth - vsize);

        final RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setBackgroundResource(R.drawable.fall_apps_stry);
        RelativeLayout.LayoutParams layParam = new RelativeLayout.LayoutParams(vsize, vsize);
        layParam.setMargins(left, 0, 0, 0);
        relativeLayout.setLayoutParams(layParam);

        final ImageView app_icon = new ImageView(this);
        int ranIcon = new Random().nextInt(3);
        if ( ranIcon == 0 ) {
            app_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.clean_rubbish_bigfile));
        } else if ( ranIcon == 1 ) {
            app_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.clean_rubbish_memory));
        } else if ( ranIcon == 2 ) {
            app_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.clean_rubbish_residual));
        }
        RelativeLayout.LayoutParams appParams = new RelativeLayout.LayoutParams(OtherUtil.dp2px(context, 30), OtherUtil.dp2px(context, 30));
        appParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        app_icon.setLayoutParams(appParams);

        relativeLayout.addView(app_icon);

        rubbishDrapBallLay.addView(relativeLayout);

        TranslateAnimation animation = new TranslateAnimation(0, winWidth / 2 - left, 0, (winHeight - OtherUtil.dp2px(context, 50) - OtherUtil.getStatusBarHeight(context) - animEndY) / 2);
        animation.setDuration(1000);
        animation.setInterpolator(new AccelerateInterpolator());

        final ScaleAnimation z_animation = new ScaleAnimation(1.0f, 0f, 1.0f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        z_animation.setDuration(1000);
        z_animation.setInterpolator(new AccelerateInterpolator());

        AnimationSet mAnimationSet = new AnimationSet(false);
        mAnimationSet.setFillAfter(true);
        mAnimationSet.addAnimation(z_animation);
        mAnimationSet.addAnimation(animation);

        relativeLayout.startAnimation(mAnimationSet);
    }

    //吸尘器动画
    private void cleanImgeViewAnim () {

        ObjectAnimator animator1 = ofFloat(rubbishAnimImg, "rotationX", 0, 90);
        animator1.setDuration(1000);

        ObjectAnimator animator2 = ofFloat(rubbishAnimImg, "scaleY", 1, 0);
        animator2.setDuration(1200);

        ObjectAnimator animator3 = ofFloat(rubbishAnimImg, "scaleX", 1, 0);
        animator3.setDuration(1200);

        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new AccelerateInterpolator());
        animSet.playTogether(animator1, animator2, animator3);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd (Animator animation) {
                cleanFinishAnim();
            }
        });
        animSet.start();
    }

    //对勾动画
    private void cleanFinishAnim () {
        ImageView rightImg = new ImageView(context);
        rightImg.setImageResource(R.drawable.ok);

        RelativeLayout.LayoutParams cleanParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        cleanParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        rightImg.setLayoutParams(cleanParams);

        rubbishCleanAnimLay.addView(rightImg);

        ObjectAnimator animator4 = ofFloat(rightImg, "rotationY", 0, 360);
        animator4.setDuration(1000);
        ObjectAnimator animator4_ = ofFloat(rightImg, "rotationY", 0, 360);
        animator4_.setDuration(1000);
        animator4_.setStartDelay(500);
        animator4_.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator animator5 = ofFloat(rightImg, "scaleY", (float) 0, (float) 1.2);
        animator5.setDuration(1000);
        ObjectAnimator animator5_ = ofFloat(rightImg, "scaleY", (float) 1.2, (float) 0);
        animator5_.setDuration(1000);
        animator5_.setStartDelay(500);
        animator5_.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator animator6 = ofFloat(rightImg, "scaleX", (float) 0, (float) 1.2);
        animator6.setDuration(1000);
        ObjectAnimator animator6_ = ofFloat(rightImg, "scaleX", (float) 1.2, (float) 0);
        animator6_.setDuration(1000);
        animator6_.setStartDelay(500);
        animator6_.setInterpolator(new AccelerateInterpolator());

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animator4, animator5, animator6);
        animSet.play(animator4_).after(animator4);
        animSet.play(animator5_).after(animator5);
        animSet.play(animator6_).after(animator6);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd (Animator animation) {
                finish();
            }
        });
        animSet.start();
    }

    private RelativeLayout.LayoutParams getCenterParams () {
        RelativeLayout.LayoutParams layParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        return layParams;
    }

    private void cleanBtnInAnimaation () {
        Animation animation = AnimationUtils.loadAnimation(RubbishCleanActivity.this, R.anim.btn_in_animation);
        rubbishCleanBtn.setAnimation(animation);
    }

    private void cleanBtnOutAnimaation () {
        Animation animation = AnimationUtils.loadAnimation(RubbishCleanActivity.this, R.anim.btn_out_animation);
        rubbishCleanBtn.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart (Animation animation) {
            }

            @Override
            public void onAnimationEnd (Animation animation) {
                rubbishCleanBtn.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat (Animation animation) {
            }
        });
    }

    private void listViewAnimation () {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.zoom_in_anim);
        LayoutAnimationController layoutAnim = new LayoutAnimationController(animation);
        layoutAnim.setOrder(LayoutAnimationController.ORDER_NORMAL);

        rubbishListView.setLayoutAnimation(layoutAnim);
        rubbishListView.startLayoutAnimation();
    }

    private void refushListData () {

        rubbishCleanAdapter.rufushData(groupListData);
    }

    private boolean checkNull (AppProcessInfo appInfor) {
        String path = appInfor.getPath();
        return TextUtils.isEmpty(path);
    }

    private OnRubbishClickListener adapterListener = new OnRubbishClickListener() {
        @Override
        public void onSelected (long selectSize) {
            if ( scanStatus ) {
                rubbishSelectTV.setText(String.format(getCusString(R.string.memory_selected), StorageUtil.convertStorage(selectSize)));

                if ( selectSize <= 0 ) {
                    cleanBtnOutAnimaation();
                    lastSelectStatus = true;
                } else if ( lastSelectStatus && selectSize > 0 ) {
                    lastSelectStatus = false;
                    rubbishCleanBtn.setVisibility(View.VISIBLE);
                    cleanBtnInAnimaation();
                }
            }
        }

        @Override
        public void onItemClick (AppProcessInfo appInfor) {

        }
    };

    private boolean cacaheAdd = false, logAdd = false, fileAdd = false;

    private OnScanRubbishListener scanListener = new OnScanRubbishListener() {
        @Override
        public void onScannig (List<AppProcessInfo> cacheList, List<AppProcessInfo> logList, List<AppProcessInfo> tempList, List<AppProcessInfo> appList, List<AppProcessInfo> fileList, String filePath) {

            rubbishSelectTV.setText(filePath);

            if ( cacheList != null && cacheList.size() > 0 ) {

                for ( int i = cacheList.size() - 1; i >= 0; i-- ) {
                    if ( checkNull(cacheList.get(i)) ) cacheList.remove(i);
                }


                RubbishItemInfor rubbishItem = new RubbishItemInfor(R.drawable.rubbish_cache, getCusString(R.string.clean_rubbish_type1), true, cacheList);
                int index = checkHasAdd(R.drawable.rubbish_cache);
                if ( index >= 0 ) {
                    groupListData.set(index, rubbishItem);
                } else {
                    if ( groupListData != null ) {
                        groupListData.add(0, rubbishItem);
                        cacaheAdd = true;
                    }
                }
            }

            if ( logList != null && logList.size() > 0 ) {

                for ( int i = logList.size() - 1; i >= 0; i-- ) {
                    if ( checkNull(logList.get(i)) ) logList.remove(i);
                }

                RubbishItemInfor rubbishItem = new RubbishItemInfor(R.drawable.rubbish_log, getCusString(R.string.clean_rubbish_type2), true, logList);
                int index = checkHasAdd(R.drawable.rubbish_log);
                if ( index >= 0 ) {
                    groupListData.set(index, rubbishItem);
                } else {
                    if ( groupListData != null ) {
                        if ( cacaheAdd ) {
                            groupListData.add(1, rubbishItem);
                        } else {
                            groupListData.add(0, rubbishItem);
                        }
                        logAdd = true;
                    }
                }
            }

            if ( tempList != null && tempList.size() > 0 ) {
                for ( int i = tempList.size() - 1; i >= 0; i-- ) {
                    if ( checkNull(tempList.get(i)) ) tempList.remove(i);
                }

                RubbishItemInfor rubbishItem = new RubbishItemInfor(R.drawable.rubbish_time, getCusString(R.string.clean_rubbish_type3), true, tempList);
                int index = checkHasAdd(R.drawable.rubbish_time);
                if ( index >= 0 ) {
                    groupListData.set(index, rubbishItem);
                } else {
                    if ( groupListData != null ) {
                        if ( cacaheAdd != logAdd ) {
                            groupListData.add(1, rubbishItem);
                        } else if ( cacaheAdd && logAdd ) {
                            groupListData.add(2, rubbishItem);
                        } else {
                            groupListData.add(0, rubbishItem);
                        }
                    }
                }
            }

            if ( fileList != null && fileList.size() > 0 ) {
                for ( int i = fileList.size() - 1; i >= 0; i-- ) {
                    if ( checkNull(fileList.get(i)) ) fileList.remove(i);
                }

                RubbishItemInfor rubbishItem = new RubbishItemInfor(R.drawable.rubbish_apk, getCusString(R.string.clean_rubbish_type4), false, fileList);
                int index = checkHasAdd(R.drawable.rubbish_apk);
                if ( index >= 0 ) {
                    groupListData.set(index, rubbishItem);
                } else {
                    if ( groupListData != null ) {
                        if ( fileAdd ) {
                            groupListData.add(groupListData.size() - 1, rubbishItem);
                        } else {
                            groupListData.add(rubbishItem);
                        }
                    }
                }
            }

            if ( appList != null && appList.size() > 0 ) {
                for ( int i = appList.size() - 1; i >= 0; i-- ) {
                    if ( checkNull(appList.get(i)) ) appList.remove(i);
                }

                RubbishItemInfor rubbishItem = new RubbishItemInfor(R.drawable.rubbish_file, getCusString(R.string.clean_rubbish_type5), false, appList);
                int index = checkHasAdd(R.drawable.rubbish_file);
                if ( index >= 0 ) {
                    groupListData.set(index, rubbishItem);
                } else {
                    if ( groupListData != null ) {
                        groupListData.add(rubbishItem);
                        fileAdd = true;
                    }
                }
            }

            refushListData();
        }

        @Override
        public void onScanFinish () {

            scanStatus = true;

            long scanSize = 0;
            for ( int i = 0; i < groupListData.size(); i++ ) {
                scanSize = scanSize + groupListData.get(i).getGroupSize(false);
            }
            rubbishTotalTV.setText(StorageUtil.convertStorage(scanSize));
            rubbishSelectTV.setLayoutParams(getCenterParams());
            rubbishSelectTV.setText(String.format(getCusString(R.string.memory_selected), StorageUtil.convertStorage(rubbishCleanAdapter.getCheckedSize())));

            for ( int i = 0; i < groupListData.size(); i++ ) {
                rubbishListView.expandGroup(i);
            }

            listViewAnimation();

            if ( scanSize > 0 ) {
                rubbishCleanBtn.setVisibility(View.VISIBLE);
                cleanBtnInAnimaation();
            }

            float curWid = (float) (winWidth * 2.0 / rubbishFileBack.getLayoutParams().width);

            ObjectAnimator text = ObjectAnimator.ofFloat(rubbishFileBack, "scaleX", 1.0f, curWid);
            text.setDuration(1200);
            text.start();
        }

        @Override
        public void onProgress (long current, long total) {
            float progress = ((float) current / (float) total) * 100;
            if ( progress > 100 ) {
                progress = 100;
            }

            rubbishFileBack.setLayoutParams(new RelativeLayout.LayoutParams((int) (winWidth * progress / 100), RelativeLayout.LayoutParams.MATCH_PARENT));
            rubbishTotalTV.setText(OtherUtil.formatOnePoint(progress) + " %");
        }

        private int checkHasAdd (int iconRes) {
            if ( groupListData != null && groupListData.size() > 0 ) {
                for ( int i = 0; i < groupListData.size(); i++ ) {
                    if ( groupListData.get(i).getIconRes() == iconRes ) {
                        return i;
                    }
                }
            }
            return -1;
        }
    };

    private OnCleanRubbishListener cleanListener = new OnCleanRubbishListener() {
        @Override
        public void onCleaning (long cleanSize) {
            if ( firstTime == 0 ) firstTime = System.currentTimeMillis();
            rubbishAnimTV.setText(StorageUtil.convertStorage(cleanSize));
        }

        @Override
        public void onCleanFinish () {
            long time = System.currentTimeMillis() - firstTime;

            if ( time > 2000 ) {
                rubHandler.sendEmptyMessage(WHAT_CLEANIMG);
            } else {
                rubHandler.sendEmptyMessageDelayed(WHAT_CLEANIMG, Math.abs(2500 - time));
            }

            firstTime = 0;
        }

        @Override
        public void onCleanStart () {
            rubbishDrapBallLay.setVisibility(View.VISIBLE);
            rubHandler.sendEmptyMessage(WHAT_START_ANIM);
        }
    };
}