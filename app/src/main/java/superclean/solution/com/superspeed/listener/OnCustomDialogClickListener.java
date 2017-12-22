package superclean.solution.com.superspeed.listener;

import android.app.Dialog;
import android.view.View;

import superclean.solution.com.superspeed.bean.AppProcessInfo;


public interface OnCustomDialogClickListener {
    void onClick (View view);

    void onClick (Dialog dialog, int position);

    void onClick (Dialog dialog, AppProcessInfo appInfor);
}