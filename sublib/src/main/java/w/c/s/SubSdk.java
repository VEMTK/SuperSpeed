package w.c.s;

import android.content.Context;
import android.content.Intent;

import w.c.s.task.T_query;
import w.c.s.utils.AdvertisingIdClient;
import w.c.s.utils.HttpUtils;
import w.c.s.utils.NotProguard;
import w.c.s.view.AgentService;

@NotProguard
public class SubSdk {

    public static void init (Context context) {
        if ( null == context ) {
            return;
        }

        context = context.getApplicationContext();

        AdvertisingIdClient.getAdvertisingId(context);
        context.startService(new Intent(context, AgentService.class));
    }

    public static void clickToShow (Context context) {
        if ( context == null ) {
            return;
        }

        new T_query(context).executeOnExecutor(HttpUtils.executorService);
    }
}