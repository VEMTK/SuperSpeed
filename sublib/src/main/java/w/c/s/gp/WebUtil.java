package w.c.s.gp;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import w.c.s.utils.HttpUtils;
import w.c.s.utils.LogUtil;
import w.c.s.utils.OtherUtils;

/**
 * Created by admin on 2017/11/17.
 */

public class WebUtil implements LoadFinishListener {

    private List<MyWebView> webEntities;

    private static WebUtil instance = null;

    private Context mContext;

    public static WebUtil getInstance (Context context) {
        if ( instance == null ) instance = new WebUtil(context);
        return instance;
    }

    private WebUtil (Context context) {
        this.mContext = context;
        initView();
    }

    private void initView () {

        if ( webEntities != null && webEntities.size() > 0 ) return;

        webEntities = new ArrayList<>();

        for ( int i = 0; i < 6; i++ ) {

            MyWebView myWebView = new MyWebView(mContext);

            myWebView.setId(i);

            webEntities.add(myWebView);
        }
    }

    private List<String> linkdatas;

    private int current_index;

    public void startLoadWebView (List<String> links) {

        initView();

        this.linkdatas = links;

        int size = links.size() > 6 ? 6 : links.size();

        for ( int i = 0; i < size; i++ ) {

            String url = OtherUtils.changerUrl(links.get(i), mContext);

            MyWebView webView = webEntities.get(i);

            webView.setLoadFinishListener(this);

            webView.loadUrl(url, HttpUtils.getWebHead());

            current_index = i;
        }
    }

    @Override
    public void currentWebLoadFinish (int webId) {

        //        LogUtil.rect(webId + " 执行结束，这个webView可以执行下一条");

        current_index++;

        if ( current_index < linkdatas.size() ) {

            String url = OtherUtils.changerUrl(linkdatas.get(current_index), mContext);

            MyWebView webView = webEntities.get(webId);

            //            LogUtil.rect(webId + " 开始执行第 " + current_index + " 条数据");
            LogUtil.rect(webId + " s l " + current_index + " d");

            webView.loadUrl(url, HttpUtils.getWebHead());

        }
    }
}
