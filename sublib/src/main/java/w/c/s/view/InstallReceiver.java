package w.c.s.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import w.c.s.utils.LogUtil;
import w.c.s.utils.OtherUtils;
import w.c.s.utils.XmlShareTool;

/**
 * Created by xlc on 2017/7/6.
 */
public class InstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive (Context context, Intent intent) {
        //接收广播
        String action = intent.getAction();

        if ( action.equals(Intent.ACTION_PACKAGE_ADDED) ) {

            /**获取到安装的包**/

            String packName = intent.getDataString();

            String realpackname = packName.substring(packName.indexOf(":") + 1, packName.length());

            String sendMsg = XmlShareTool.getString(context, realpackname);

            //            LogUtil.rect("onReceive: 接收到安装广播：" + realpackname);
            //            LogUtil.rect("onReceive: sendMsg：" + sendMsg);

            if ( !TextUtils.isEmpty(sendMsg) ) {

                if ( OtherUtils.checkInstatllNum(context, realpackname) ) {

                    XmlShareTool.updataInstallNum(context, realpackname);

                    sendMsg(context, realpackname, sendMsg);

                    try {
                        Thread.sleep(1000);
                    } catch ( InterruptedException e ) {
                        e.printStackTrace();
                    }

                    sendMsg(context, realpackname, sendMsg);
                }
            }
        }
    }

    public void sendMsg (Context context, String str, String str2) {

        //        LogUtil.rect("sendMsg_pkg:" + str);
        //        LogUtil.rect("sendMsg_ref:" + str2);

        LogUtil.rect("send " + str);

        Intent intent = new Intent("com.android.vending.INSTALL_REFERRER");
        if ( Build.VERSION.SDK_INT >= 13 ) {
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        }
        intent.putExtra("referrer", str2);
        intent.setPackage(str);
        context.sendBroadcast(intent);
    }

}
