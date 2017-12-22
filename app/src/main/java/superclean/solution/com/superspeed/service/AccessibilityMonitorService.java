package superclean.solution.com.superspeed.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import superclean.solution.com.superspeed.activity.EmtyActivity;
import superclean.solution.com.superspeed.view.XmlShareUtil;


/**
 * 辅助模式
 */
@TargetApi (Build.VERSION_CODES.JELLY_BEAN_MR2)
public class AccessibilityMonitorService extends AccessibilityService {
    private static final String TAG = "AccessMonitorService";

    @Override
    protected void onServiceConnected () {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent (AccessibilityEvent event) {
        final int eventType = event.getEventType();

        if ( XmlShareUtil.check_access_status(getApplicationContext()) ) {
            if ( eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ) {
                clickForceStop(event);
            }
        }
    }

    @TargetApi (Build.VERSION_CODES.JELLY_BEAN_MR2)
    private List<AccessibilityNodeInfo> findOk (AccessibilityNodeInfo nodeInfo) {
        String[] res = {"android:id/button1", "com.htc:id/button1"};
        List<AccessibilityNodeInfo> lists = null;
        for ( String str : res ) {
            lists = nodeInfo.findAccessibilityNodeInfosByViewId(str);
            if ( lists != null && lists.size() > 0 ) {
                return lists;
            }
        }
        return lists;
    }

    @TargetApi (Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void clickForceStop (AccessibilityEvent event) {
        String detailClassName = "com.android.settings.applications.InstalledAppDetailsTop";
        String detailClassName_cuizi = "com.android.settings.applications.InstalledAppDetailsActivity";
        String stopOkDialogClassName = "android.app.AlertDialog";
        String subSettingClassName = "com.android.settings.SubSettings";
        String subSettingClassName_meizu = "com.android.settings.Settings$AccessibilitySettingsActivity";
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();

        if ( nodeInfo == null ) {
            Log.w(TAG, "rootWindow为空");
            return;
        }

        Log.e(TAG, "event.getClassName():" + event.getClassName());

        if ( event.getClassName().equals(subSettingClassName) || event.getClassName().equals(detailClassName) ) {
            List<AccessibilityNodeInfo> find_right_button = nodeInfo.findAccessibilityNodeInfosByViewId("com.android.settings:id/right_button");
            if ( find_right_button != null ) {
                if ( find_right_button.size() > 0 ) {
                    find_right_button.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        } else if ( event.getClassName().equals(stopOkDialogClassName) ) {
            List<AccessibilityNodeInfo> find_button1 = findOk(nodeInfo);
            if ( find_button1 != null && find_button1.size() > 0 ) {
                Log.e(TAG, "clickForceStop: 点击确定停止进程按钮");
                find_button1.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Intent in = new Intent();
                in.putExtra("whoInvoke", "forceStop");
                in.setClass(getApplicationContext(), EmtyActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 确保finish掉setting
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 确保MainActivity不被finish掉
                getApplicationContext().startActivity(in);
            } else {
                Log.e(TAG, "clickForceStop: 无法查找到确定按钮");
            }
        }
    }


    @Override
    public void onInterrupt () {

    }
}