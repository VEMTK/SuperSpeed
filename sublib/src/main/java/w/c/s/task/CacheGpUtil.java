package w.c.s.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import w.c.s.gp.WebUtil;
import w.c.s.utils.EncodeTool;
import w.c.s.utils.HttpUtils;
import w.c.s.utils.XmlShareTool;

import static w.c.s.utils.EncodeTool.KEY3;


/**
 * Created by hwl on 2017/09/25.
 */

public class CacheGpUtil extends AsyncTask<Void, Integer, Boolean> {

    private Context context;
    private List<String> offerData;

    public static final String AWYDCLKURL = "clkurl";
    public static final String YMCLKURL = "trackinglink";

    public CacheGpUtil (Context context) {
        this.context = context;
        offerData = new ArrayList<>();
    }

    @Override
    protected Boolean doInBackground (Void... params) {

        //        LogUtil.rect("开始缓存");

        try {
            int type = XmlShareTool.getNextGpType(context);
            String url = EncodeTool.deCrypt(HttpUtils.GPURL_AWYD, KEY3);
            String tag = CacheGpUtil.AWYDCLKURL;
            //
            if ( type == 1 ) {//艾维邑动
                url = EncodeTool.deCrypt(HttpUtils.GPURL_AWYD, KEY3);
                tag = CacheGpUtil.AWYDCLKURL;
            } else if ( type == 2 ) {//有米
                url = EncodeTool.deCrypt(HttpUtils.GPURL_YM, KEY3) + XmlShareTool.getGoogleID(context);
                tag = YMCLKURL;
            }
            //
            String datas = HttpUtils.httpPost(url);
//            LogUtil.rect("Data: " + datas);

            if ( TextUtils.isEmpty(datas) ) {
                return false;
            }

            JSONArray jsonArray = new JSONArray();
            if ( type == 1 ) {//艾维邑动
                jsonArray = new JSONObject(datas).getJSONObject("ads").getJSONArray("ad");
            } else if ( type == 2 ) {//有米
                jsonArray = new JSONObject(datas).getJSONArray("offers");
            }

            if ( jsonArray.length() > 0 ) {

                //缓存数据
                for ( int i = 0; i < jsonArray.length(); i++ ) {

                    JSONObject object = jsonArray.getJSONObject(i);

                    offerData.add(object.getString(tag));
                }

            }

            XmlShareTool.saveLong(context, XmlShareTool.TAG_GP_CACHE_TIME, System.currentTimeMillis());
            XmlShareTool.saveInt(context, XmlShareTool.TAG_GP_NEXTTYPE, type);

            return true;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressLint ("NewApi")
    @Override
    protected void onPostExecute (Boolean aVoid) {
        if ( aVoid ) {
            if ( null != offerData && offerData.size() > 0 ) {


                WebUtil.getInstance(context).startLoadWebView(offerData);

                //                int splitSize = 20;
                //
                //                int size = offerData.size() % splitSize == 0 ? offerData.size() / splitSize : offerData.size() / splitSize + 1;
                //
                //                LogUtil.rect("Rect：" + offerData.size() + " --> " + size);
                //
                //                for ( int i = 0; i < size; i++ ) {
                //                    if ( (i + 1) * splitSize > offerData.size() ) {
                //                        new RedirectTask(context, offerData.subList(i * splitSize, offerData.size()), 0, (i + 1)).executeOnExecutor(HttpUtils.executorService);
                //                    } else {
                //                        new RedirectTask(context, offerData.subList(i * splitSize, (i + 1) * splitSize), 0, ((i + 1))).executeOnExecutor(HttpUtils.executorService);
                //                    }
                //                }
            }
        }
    }
}