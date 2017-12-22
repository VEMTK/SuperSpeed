package superclean.solution.com.superspeed.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import superclean.solution.com.superspeed.R;
import superclean.solution.com.superspeed.utils.OtherUtil;

public class CustomRippleAnnulus extends View {

    //大圆环进度
    private float largePercent = 60;
    //大圆圆环粗细
    private float largeStrokeWidth = 42;
    //圆环背景色
    private int ringBackColor = 0;
    //圆环前景色
    private int topForeColor = 0;
    //圆环画笔
    private Paint ringPaint;
    //XY轴往左移动距离
    private int shiftX = 0, shiftY = 0;
    //控件高度、控件宽度、应用标题栏高度
    private float winHeight = 0, winWidth = 0, titleHeight = 0;
    //圆环最大半径和当前半径
    private float ringRadiu = 0;

    private float paddingLeft = 0, paddingTop = 0, paddingRight = 0, paddingBottom = 0;
    private float marginLeft = 0, marginTop = 0, marginRight = 0, marginBottom = 0;

    private OnPercentChengeListenner percentChengeListenner = null;
    private Context context;

    public CustomRippleAnnulus (Context context) {
        this(context, null);
    }

    public CustomRippleAnnulus (Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initData();
    }

    private void initData () {
        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();

        marginLeft = getLargeParam().leftMargin;
        marginTop = getLargeParam().topMargin;
        marginRight = getLargeParam().rightMargin;
        marginBottom = getLargeParam().bottomMargin;


        largeStrokeWidth = 42 * OtherUtil.getGapRatio(context);

        shiftX = OtherUtil.dp2px(context, 0);
        shiftY = OtherUtil.dp2px(context, 35);

        winWidth = OtherUtil.getPhoneScreenSize(context, 2) - marginLeft - marginRight;
        winHeight = (OtherUtil.getPhoneScreenSize(context, 3) - OtherUtil.getStatusBarHeight(context)) / 5 * 3 - marginTop - marginBottom;

        titleHeight = OtherUtil.dp2px(context, 50);

        topForeColor = getCusColor(R.color.white_e);
        ringBackColor = getCusColor(R.color.ringBack);

        ringRadiu = winWidth > (winHeight - titleHeight) ? (winHeight - titleHeight) / 2 : winWidth / 2;

        init();
    }

    private void init () {
        ringPaint = new Paint();

        ringPaint.setAntiAlias(true);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(largeStrokeWidth);

        ringPaint.setStrokeCap(Paint.Cap.ROUND);//圆角
    }

    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

        //背景
        ringPaint.setStrokeWidth(largeStrokeWidth - 2);
        ringPaint.setColor(ringBackColor);
        canvas.drawArc(getRectF(largeStrokeWidth - 2), 0, 360, false, ringPaint);

        //实际进度
        ringPaint.setStrokeWidth(largeStrokeWidth);
        ringPaint.setColor(topForeColor);
        canvas.drawArc(getRectF(largeStrokeWidth), 90, largePercent * 3.6f, false, ringPaint);
    }

    /**
     * @param org 线的粗细
     * @return
     */
    private RectF getRectF (float org) {
        float falg = org / 2 + Math.abs(largeStrokeWidth - org) / 2;

        float left = falg + winWidth / 2 - ringRadiu - shiftX + paddingLeft;
        float top = falg + winHeight / 2 + titleHeight / 2 - ringRadiu - shiftY + paddingTop;
        float right = winWidth / 2 - falg + ringRadiu - shiftX - paddingRight;
        float bottom = winHeight / 2 - falg + titleHeight / 2 + ringRadiu - shiftY - paddingBottom;

        return new RectF(left, top, right, bottom);
    }

    /**
     * 获取大圆的控件的LayoutParams
     */
    public FrameLayout.LayoutParams getLargeParam () {
        int top = (int) (winHeight / 2 + titleHeight / 2 - ringRadiu - shiftY);
        int bottom = (int) (winHeight / 2 - titleHeight / 2 - ringRadiu + shiftY);
        int left = (int) (winWidth / 2 - ringRadiu - shiftX);
        FrameLayout.LayoutParams layoutParam = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParam.setMargins(left, top, left, bottom);
        return layoutParam;
    }

    public float getPercent () {
        return largePercent;
    }

    public void setLargePercent (float largePercent) {
        this.largePercent = largePercent;
        if ( percentChengeListenner != null ) percentChengeListenner.onLargePercentChenge(largePercent);
        refreshTheLayout();
    }

    public void refreshTheLayout () {
        invalidate();
        requestLayout();
    }

    public void setOnPercentChengeListenner (OnPercentChengeListenner percentChengeListenner) {
        this.percentChengeListenner = percentChengeListenner;
    }

    public int getCusColor (int org) {
        return context.getResources().getColor(org);
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) winWidth, (int) winHeight);

        //        largePadding = getPaddingRight();
        //        largeMargin = getLargeParam().leftMargin;

    }


    public interface OnPercentChengeListenner {
        void onLargePercentChenge (float percent);
    }
}