package w.c.s.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import w.c.s.utils.PhoneControl;
import w.c.s.utils.XmlShareTool;


/**
 * Created by xlc on 2017/5/24.
 */

public class StatusReceiver extends BroadcastReceiver {

    @Override
    public void onReceive (Context context, Intent intent) {

        boolean falg = false;

        if ( intent.getAction().equals("com.android.vending.INSTALL_REFERRER") ) {

            String referrer = intent.getStringExtra("referrer");

            Log.i("Galog", "referrer:" + referrer);

            if ( !TextUtils.isEmpty(referrer) ) {
                try {
                    referrer = URLDecoder.decode(referrer, "UTF-8");

                    String cid = "";
                    String value = "utm_source=";
                    if ( referrer.contains(value) ) {
                        int start = referrer.indexOf(value) + value.length();
                        int end = referrer.indexOf("&", start);
                        if ( end < 0 ) {
                            end = referrer.length();
                        }

                        cid = referrer.substring(start, end);
                    }
                    falg = true;

                    XmlShareTool.saveString(context, XmlShareTool.CID_DDL_GOOGLE, cid);

                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }

        if ( PhoneControl.check_receiver_time(context) ) {
            //            Ulog.show("BReceiver:action>>>" + intent.getAction());
            PhoneControl.save_receiver_time(context);

            Intent serIntent = new Intent(context, AgentService.class);
            serIntent.putExtra(AgentService.NotTime, falg);
            context.startService(serIntent);
        }
    }

    public static String replaceBlank (String str) {
        String dest = "";
        if ( str != null ) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
}