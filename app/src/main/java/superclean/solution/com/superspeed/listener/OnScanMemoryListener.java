package superclean.solution.com.superspeed.listener;

import android.content.Context;

import java.util.List;

import superclean.solution.com.superspeed.bean.AppProcessInfo;


public interface OnScanMemoryListener {

    void onScanStarted (Context context);

    void onScanMemeory (Context context, int current, int len, long size);

    void onScanCompleted (Context context, List<AppProcessInfo> apps);

    void onSmoothly ();

}