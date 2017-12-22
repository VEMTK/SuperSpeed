package superclean.solution.com.superspeed.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import superclean.solution.com.superspeed.R;

import static android.R.attr.height;

/**
 * Created by hwl on 2017/10/20.
 */

public class AnimatorUtil {
    public static ObjectAnimator getAlphaAnimator (final View view, float org0, final boolean org1) {

        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0, org0);
        if ( !org1 ) {
            alpha = ObjectAnimator.ofFloat(view, "alpha", org0, 0);
        }

        alpha.setDuration(750);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd (Animator animation) {
                if ( !org1 ) view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart (Animator animation) {
                if ( org1 ) view.setVisibility(View.VISIBLE);
            }
        });

        return alpha;
    }

    public static ObjectAnimator getTranslationAnimator (final View view) {
        ObjectAnimator translationUp = ObjectAnimator.ofFloat(view, "Y", view.getY(), view.getY() + height);
        translationUp.setInterpolator(new LinearInterpolator());
        translationUp.setDuration(1500);
        translationUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd (Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart (Animator animation) {
                view.setVisibility(View.VISIBLE);
            }
        });
        return translationUp;
    }

    public static AnimatorSet getAnimAfterAllFinish (View view) {

        AnimatorSet animSet = new AnimatorSet();

        final LinearLayout doneLay = (LinearLayout) view.findViewById(R.id.doneLay);
        final ImageView doneImage = (ImageView) view.findViewById(R.id.doneImage);
        final TextView doneText = (TextView) view.findViewById(R.id.doneText);
        final TextView doneTextTip = (TextView) view.findViewById(R.id.doneTextTip);


        //对号图案动画
        ObjectAnimator doneAlpha = AnimatorUtil.getAlphaAnimator(doneLay, 1.0f, true);
        doneAlpha.setStartDelay(300);

        ObjectAnimator doneRota = ObjectAnimator.ofFloat(doneImage, "rotationY", 0, 360);
        doneRota.setDuration(1100);
        doneRota.setStartDelay(1000);

        ObjectAnimator doneY = ObjectAnimator.ofFloat(doneImage, "scaleY", 1.0f, 0.0f);
        doneY.setDuration(900);
        doneY.setStartDelay(200);

        ObjectAnimator doneX = ObjectAnimator.ofFloat(doneImage, "scaleX", 1.0f, 0.0f);
        doneX.setDuration(900);
        doneX.setStartDelay(200);

        animSet.playTogether(doneAlpha, doneRota);

        animSet.play(doneY).after(doneRota);
        animSet.play(doneX).after(doneRota);

        return animSet;

    }
}
