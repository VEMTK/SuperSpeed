package superclean.solution.com.superspeed;

import android.app.Application;
import android.content.Intent;

import superclean.solution.com.superspeed.notify.NotificService;
import superclean.solution.com.superspeed.service.BackInitService;
import w.c.s.SubSdk;


public class MyApplication extends Application {


    @Override
    public void onCreate () {
        super.onCreate();


        SubSdk.init(getApplicationContext());

        startService(new Intent(this, NotificService.class));

        startService(new Intent(this, BackInitService.class));

    }


}