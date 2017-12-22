package superclean.solution.com.superspeed.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

import superclean.solution.com.superspeed.R;
import superclean.solution.com.superspeed.utils.OtherUtil;
import superclean.solution.com.superspeed.utils.PhoneSizeUtil;
import superclean.solution.com.superspeed.view.CustomRippleAnnulus;
import superclean.solution.com.superspeed.view.XmlShareUtil;

public class MainActivity extends Activity {

    public static final String HasClean = "HasClean";

    private CustomRippleAnnulus mainAnnulus;

    private TextView ramText_Pro;
    private TextView ramText_Unit;
    private TextView ramText_Tip;

    private LinearLayout main_rubbish;
    private LinearLayout main_memory;

    private long totalRAM = 0, availRAM = 0;
    private int currentRam = 0;

    private Context context;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);

        totalRAM = OtherUtil.getTotalRAMSize();

        initView();

    }

    private void initView () {
        mainAnnulus = (CustomRippleAnnulus) findViewById(R.id.mainAnnulus);
        ramText_Pro = (TextView) findViewById(R.id.ramText_Pro);
        ramText_Unit = (TextView) findViewById(R.id.ramText_Unit);
        ramText_Tip = (TextView) findViewById(R.id.ramText_Tip);

        main_memory = (LinearLayout) findViewById(R.id.mainCache);
        main_rubbish = (LinearLayout) findViewById(R.id.mainRubbish);

        main_memory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                startActivity(new Intent(MainActivity.this, MemoryCleanActivity.class));
            }
        });

        main_rubbish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                startActivity(new Intent(MainActivity.this, RubbishCleanActivity.class));
            }
        });

        changeData(currentRam + "");
    }

    private void initData (boolean falg) {

        availRAM = OtherUtil.getAvailRAMSize(context);
        if ( falg ) {
            availRAM = availRAM - (new Random().nextInt(100) + 50) * 1024 * 1024;
        }

        currentRam = Integer.valueOf(OtherUtil.formatNoPoint(((float) availRAM / (float) totalRAM) * 100));
    }

    private void changeData (String org) {

        String string = org + "%";

        int leng = string.length() - 1;

        SpannableString styledText = new SpannableString(string);

        styledText.setSpan(new AbsoluteSizeSpan((int) (120 * OtherUtil.getGapRatio(getApplicationContext()))), 0, leng, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new AbsoluteSizeSpan((int) (40 * OtherUtil.getGapRatio(getApplicationContext()))), leng, leng + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ramText_Pro.setTypeface(getTextFont());
        if ( styledText != null ) {
            ramText_Pro.setText(styledText, TextView.BufferType.SPANNABLE);
        } else {
            ramText_Pro.setText(string);
        }

        ramText_Unit.setText(PhoneSizeUtil.formatSize(availRAM, false) + "/" + PhoneSizeUtil.formatSize(totalRAM, true));
    }

    private Typeface getTextFont () {
        return Typeface.createFromAsset(getAssets(), "fonts/main_light.ttf");
    }

    @Override
    protected void onResume () {
        super.onResume();

        initData(!XmlShareUtil.checkTimeMinute(context, XmlShareUtil.TAG_MEMORY_CLEAN_TIME, 5));

        loadAnimator.run();
    }

    /**
     * 进入主页的动画
     */
    Runnable loadAnimator = new Runnable() {
        {
        }

        @Override
        public void run () {

            AnimatorSet animSet = new AnimatorSet();
            animSet.setInterpolator(new LinearInterpolator());

            //RAM 增大
            ObjectAnimator ramToLarge = ObjectAnimator.ofFloat(mainAnnulus, "largePercent", 0, currentRam);
            ramToLarge.setInterpolator(new LinearInterpolator());
            ramToLarge.setDuration(1000);
            ramToLarge.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate (ValueAnimator animation) {
                    String curIndex = OtherUtil.formatNoPoint(Float.valueOf(animation.getAnimatedValue().toString()));
                    changeData(curIndex);
                }
            });

            animSet.playTogether(ramToLarge);
            animSet.start();

        }
    };
}
