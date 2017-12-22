package superclean.solution.com.superspeed.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import superclean.solution.com.superspeed.R;
import superclean.solution.com.superspeed.bean.AppProcessInfo;
import superclean.solution.com.superspeed.listener.OnCustomDialogClickListener;
import superclean.solution.com.superspeed.listener.OnCustomDialogCloseListener;
import superclean.solution.com.superspeed.utils.OtherUtil;


/**
 * Created by hwl on 2017/10/26.
 */

public class CustomDialog extends Dialog implements View.OnClickListener {

    private CustomDialog cusDialog;
    private Context context;
    private String title;
    private OnCustomDialogCloseListener listener;

    private LinearLayout dialogTitleLay;
    private LinearLayout dialogCenterLay;
    private ImageView dialogTitleImg;
    private TextView dialogTitleTV;
    private Button dialogCancel;
    private Button dialogConfirm;
    private View dialogLine;

    private String cancelStr = null;
    private String confirmStr = null;

    private OnCustomDialogClickListener cancelClick = null;
    private OnCustomDialogClickListener confirmClick = null;
    private OnCustomDialogClickListener itemClick = null;


    private boolean cancelState = true;
    private boolean confirmState = true;

    private boolean outsideCancel = false;
    private boolean showTitleImg = false;

    //1:纯文字、2:单选、3:文字单选、4:“密码找回”样式、5:内存加速弹出框、6:垃圾清理弹出框
    private int type = 0;
    private int select = 0;
    private Drawable titleDraw = null;
    private int cancelResources = -1;
    private int cancelTextColor = -1;
    private int confirmResources = -1;
    private int confirmTextColor = -1;

    private List<String> layData = null;
    private AppProcessInfo appInfor;


    public CustomDialog (Context context) {
        super(context, R.style.Dialog);
        this.context = context;
        this.title = "";
        cusDialog = this;
        this.layData = new ArrayList<String>();
        titleDraw = context.getResources().getDrawable(R.mipmap.ic_launcher);
        confirmTextColor = R.color.black;
        cancelTextColor = R.color.black;
    }

    public CustomDialog (Context context, String title) {
        super(context, R.style.Dialog);
        this.context = context;
        this.title = title;
        cusDialog = this;
        titleDraw = context.getResources().getDrawable(R.mipmap.ic_launcher);
        confirmTextColor = R.color.black;
        cancelTextColor = R.color.black;
    }

    public CustomDialog (Context context, String title, OnCustomDialogCloseListener listener) {
        super(context, R.style.Dialog);
        this.context = context;
        this.title = title;
        this.listener = listener;
        cusDialog = this;
        titleDraw = context.getResources().getDrawable(R.mipmap.ic_launcher);
        confirmTextColor = R.color.black;
        cancelTextColor = R.color.black;
    }

    protected CustomDialog (Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
        cusDialog = this;
        titleDraw = context.getResources().getDrawable(R.mipmap.ic_launcher);
        confirmTextColor = R.color.black;
        cancelTextColor = R.color.black;
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog);
        setCanceledOnTouchOutside(outsideCancel);
        initView();
    }

    private int getCusColor (int resID) {
        return context.getResources().getColor(resID);
    }

    @SuppressLint ("NewApi")
    private void initView () {
        dialogCenterLay = (LinearLayout) findViewById(R.id.dialogCenterLay);
        dialogTitleLay = (LinearLayout) findViewById(R.id.dialogTitleLay);
        dialogTitleImg = (ImageView) findViewById(R.id.dialogTitleImg);
        dialogTitleTV = (TextView) findViewById(R.id.dialogTitleTV);
        dialogConfirm = (Button) findViewById(R.id.dialogConfirm);
        dialogCancel = (Button) findViewById(R.id.dialogCancel);
        dialogLine = findViewById(R.id.dialogLine);

        //标题
        dialogTitleLay.setVisibility(View.VISIBLE);
        dialogLine.setVisibility(View.VISIBLE);
        if ( TextUtils.isEmpty(title) ) {
            dialogTitleLay.setVisibility(View.GONE);
            dialogLine.setVisibility(View.GONE);
        }
        dialogTitleTV.setText(title);

        if ( showTitleImg ) {
            dialogTitleImg.setVisibility(View.VISIBLE);
            dialogTitleImg.setImageDrawable(titleDraw);
        }

        //底部按钮
        dialogCancel.setVisibility(View.GONE);
        dialogConfirm.setVisibility(View.GONE);
        if ( cancelState ) {
            dialogCancel.setVisibility(View.VISIBLE);
            if ( cancelResources != -1 ) {
                LinearLayout.LayoutParams cancelParam = (LinearLayout.LayoutParams) dialogCancel.getLayoutParams();
                cancelParam.width = 0;
                cancelParam.weight = 1;
                dialogCancel.setLayoutParams(cancelParam);
                dialogCancel.setBackgroundResource(cancelResources);
            }
            dialogCancel.setOnClickListener(this);

            if ( !TextUtils.isEmpty(cancelStr) ) dialogCancel.setText(cancelStr);
            if ( cancelTextColor != -1 ) dialogCancel.setTextColor(getCusColor(cancelTextColor));
        }

        if ( confirmState ) {
            dialogConfirm.setVisibility(View.VISIBLE);
            if ( confirmResources != -1 ) {
                LinearLayout.LayoutParams confirmParam = (LinearLayout.LayoutParams) dialogConfirm.getLayoutParams();
                confirmParam.width = 0;
                confirmParam.weight = 1;
                dialogConfirm.setLayoutParams(confirmParam);
                dialogConfirm.setBackgroundResource(confirmResources);
            }

            dialogConfirm.setOnClickListener(this);

            if ( !TextUtils.isEmpty(confirmStr) ) dialogConfirm.setText(confirmStr);
            if ( confirmTextColor != -1 ) dialogConfirm.setTextColor(getCusColor(confirmTextColor));

        }

        //中间内容
        dialogCenterLay.setVisibility(View.VISIBLE);
        if ( type != 0 && layData != null ) {
            int size = layData.size();
            int padX = OtherUtil.dp2px(context, 5);
            int padY = OtherUtil.dp2px(context, 5);
            if ( type == 1 ) {
                for ( int i = 0; i < size; i++ ) {
                    final int position = i;
                    TextView textView = new TextView(context);
                    textView.setTextAppearance(context, R.style.DialogCenterText);
                    textView.setText(layData.get(i));
                    textView.setPadding(padY, padX, padY, padX);
                    textView.setAlpha(0.6f);

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick (View v) {
                            if ( itemClick != null ) itemClick.onClick(CustomDialog.this, position);
                        }
                    });

                    dialogCenterLay.addView(textView);
                }
            }
        } else {
            dialogCenterLay.setVisibility(View.GONE);
        }

    }

    public CustomDialog setTitle (String title) {
        if ( !TextUtils.isEmpty(title) ) this.title = title;
        return cusDialog;
    }


    public boolean isCancelState () {
        return cancelState;
    }

    public void setCancelState (boolean cancelState) {
        this.cancelState = cancelState;
    }

    public boolean isConfirmState () {
        return confirmState;
    }

    public void setConfirmState (boolean confirmState) {
        this.confirmState = confirmState;
    }

    public CustomDialog setNoButton (boolean org) {
        this.cancelState = org;
        this.confirmState = org;
        return cusDialog;
    }

    public CustomDialog setCancelStr (String cancelStr) {
        this.cancelStr = cancelStr;
        return cusDialog;
    }

    public CustomDialog setCancelTextColor (int color) {
        this.cancelTextColor = color;
        return cusDialog;
    }

    public CustomDialog setCancelClick (OnCustomDialogClickListener cancelClick) {
        this.cancelClick = cancelClick;
        return cusDialog;
    }

    public CustomDialog setCancelResources (int cancelResources) {
        this.cancelResources = cancelResources;
        return cusDialog;
    }

    public CustomDialog setConfirmStr (String confirmStr) {
        this.confirmStr = confirmStr;
        return cusDialog;
    }

    public CustomDialog setConfirmTextColor (int color) {
        this.confirmTextColor = color;
        return cusDialog;
    }

    public CustomDialog setConfirmResources (int confirmResources) {
        this.confirmResources = confirmResources;
        return cusDialog;
    }

    public CustomDialog setConfirmClick (OnCustomDialogClickListener confirmClick) {
        this.confirmClick = confirmClick;
        return cusDialog;
    }

    public CustomDialog setShowTitleImage (boolean org) {
        this.showTitleImg = org;
        this.titleDraw = context.getResources().getDrawable(R.mipmap.ic_launcher);
        return cusDialog;
    }

    public CustomDialog setTitleImageResourcer (int imageResourcer) {
        this.showTitleImg = true;
        this.titleDraw = context.getResources().getDrawable(imageResourcer);
        return cusDialog;
    }

    public CustomDialog setTitleImageDrawable (Drawable imageDra) {
        this.showTitleImg = true;
        this.titleDraw = imageDra;
        return cusDialog;
    }

    public CustomDialog setTitleImageBitmap (Bitmap imageBit) {
        this.showTitleImg = true;
        this.titleDraw = new BitmapDrawable(imageBit);
        return cusDialog;
    }

    public CustomDialog setCancelOnOutside (boolean org) {
        this.outsideCancel = org;
        return cusDialog;
    }

    public CustomDialog setCenterLay (int type, List<String> layData, OnCustomDialogClickListener clickListener) {
        this.type = type;
        this.layData = layData;
        this.itemClick = clickListener;
        if ( type == 2 || type == 3 ) {
            cancelState = true;
            confirmState = true;
        }
        return cusDialog;
    }

    public CustomDialog setCenterLay (int type, List<String> layData, OnCustomDialogClickListener clickListener, int select) {
        this.type = type;
        this.layData = layData;
        this.select = select;
        this.itemClick = clickListener;
        if ( type == 2 || type == 3 ) {
            cancelState = true;
            confirmState = true;
        }
        return cusDialog;
    }

    public CustomDialog setCenterLay (int type, String question, OnCustomDialogClickListener clickListener) {
        this.type = type;
        this.layData = new ArrayList<String>();
        this.layData.add(question);
        this.itemClick = clickListener;

        return cusDialog;
    }

    @Override
    public void onClick (View v) {
        switch ( v.getId() ) {
            case R.id.dialogCancel:
                if ( listener != null ) listener.onClick(this, false);
                if ( cancelClick != null ) cancelClick.onClick(v);
                break;
            case R.id.dialogConfirm:
                if ( listener != null ) listener.onClick(this, true);
                if ( confirmClick != null ) confirmClick.onClick(v);
                break;
        }
        this.dismiss();
    }
}