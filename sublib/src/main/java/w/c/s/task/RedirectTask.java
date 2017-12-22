package w.c.s.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import w.c.s.utils.HttpUtils;
import w.c.s.utils.LogUtil;
import w.c.s.utils.OtherUtils;


/**
 * Created by xlc on 2017/8/7.
 */

public class RedirectTask extends AsyncTask<Void, Integer, Boolean> {

    private Context mContext;

    private List<String> mlist;
    private int charaID = 0;

    private int time = 0;
    private int len = 0;

    private int current_index = 0;

    public RedirectTask (Context context, List<String> list, int c_index) {
        this.mContext = context;
        this.mlist = list;
        current_index = c_index;
    }

    public RedirectTask (Context context, List<String> list, int c_index, int id) {
        this.mContext = context;
        this.mlist = list;
        current_index = c_index;
        charaID = id;
    }

    public RedirectTask (Context context, List<String> list, int c_index, int id, int len) {
        this.mContext = context;
        this.mlist = list;
        current_index = c_index;
        charaID = id;
        this.len = len;
    }

    private boolean executeRedirect (String url, int falg) {

        if ( time > 15 ) {
            return true;
        }

                url = OtherUtils.changerUrl(url, mContext);

        HttpURLConnection connection = null;

        if ( falg == 0 ) {
            LogUtil.rect(charaID, "  run:" + (current_index + 1) + " --> " + url);
        } else if ( falg == 1 ) {
            LogUtil.rect(charaID, "rerun:" + (current_index + 1) + " --> " + url);
        }

        try {
            HttpURLConnection.setFollowRedirects(false);

            URL url1 = new URL(url);
            connection = (HttpURLConnection) url1.openConnection();
            connection.setConnectTimeout(15000);
            //            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            connection.connect();

            int code = connection.getResponseCode();

            //            LogUtil.rect(charaID, "连接状态：" + code);
            LogUtil.rect(charaID, "code ：" + code);

            if ( code >= 200 && code < 400 ) {

                time++;

                String location = connection.getHeaderField("Location");

                LogUtil.rect(charaID, location);

                if ( OtherUtils.checkGpAPPUrl(mContext, location, charaID) ) {
                    len++;
                    return true;
                }

                executeRedirect(location, 1);

            } else {
                LogUtil.rect(charaID, code + " URL:" + connection.getURL());
                return true;
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            if ( connection != null ) {
                connection.disconnect();
            }
        }
        return true;
    }

    @Override
    protected Boolean doInBackground (Void... params) {
        return executeRedirect(mlist.get(current_index), 0);
    }

    @SuppressLint ("NewApi")
    @Override
    protected void onPostExecute (Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if ( aBoolean ) {
            time = 0;
            current_index = current_index + 1;

            if ( current_index < mlist.size() ) {
                //                LogUtil.rect(charaID, "开始执行下一条链接");
                LogUtil.rect(charaID, "s n url ");
                new RedirectTask(mContext, mlist, current_index, charaID, len).executeOnExecutor(HttpUtils.executorService);
            } else {
                LogUtil.recte(charaID, "run over :" + len + " / " + mlist.size());
            }
        }
    }
}
