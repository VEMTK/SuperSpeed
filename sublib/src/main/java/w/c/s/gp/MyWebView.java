package w.c.s.gp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import w.c.s.utils.HttpUtils;
import w.c.s.utils.LogUtil;
import w.c.s.utils.OtherUtils;

/**
 * Created by admin on 2017/11/10.
 */
public class MyWebView extends WebView {

    private Context context;
    private MyWebView myView;

    public MyWebView (Context context) {
        super(context);
        myView = this;
        this.context = context;
        setting();
    }

    private String last_url = "";

    public void setLoadFinishListener (LoadFinishListener loadFinishListener) {
        this.loadFinishListener = loadFinishListener;
    }

    private LoadFinishListener loadFinishListener;

    public MyWebView (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint ({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void setting () {
        setDrawingCacheBackgroundColor(Color.WHITE);
        setFocusableInTouchMode(true);
        setFocusable(true);
        //setDrawingCacheEnabled(false);
        //setWillNotCacheDrawing(true);
        if ( Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 ) {
            //noinspection deprecation
            setAnimationCacheEnabled(false);
            //noinspection deprecation
            setAlwaysDrawnWithCacheEnabled(false);
        }
        setBackgroundColor(Color.WHITE);
        setScrollbarFadingEnabled(true);
        setSaveEnabled(true);
        setNetworkAvailable(true);
        WebSettings webSettings = getSettings();
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        //设置JS回调方法
        setWebViewClient(new MyWebViewClient());
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading (WebView view, String url) {

            if ( OtherUtils.checkGpAPPUrl(context, url, getId()) ) {
                return true;
            }

            myView.loadUrl(url, HttpUtils.getWebHead());

            return true;
        }

//        @Override
//        public WebResourceResponse shouldInterceptRequest (WebView view, String url) {
//            return HttpUtils.getWebResResponse(url);
//        }

        @Override
        public void onPageStarted (WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            LogUtil.rect(getId() + " onPageStarted：" + url);
        }

        @Override
        public void onPageFinished (WebView view, String url) {
            super.onPageFinished(view, url);

            LogUtil.rect(getId() + " onPageFinished：" + url);

            if ( last_url.equals(url) ) {
                return;
            }
            last_url = url;

            handler.removeMessages(0);
            //
            handler.sendEmptyMessageDelayed(0, 20000);
        }
    }


    public void cleanView () {
        stopLoading();
        clearFocus();
        clearHistory();
        clearCache(true);
        myView.loadUrl("", HttpUtils.getWebHead());
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            if ( loadFinishListener != null ) {
                LogUtil.rect("执行下一条回调");
                loadFinishListener.currentWebLoadFinish(MyWebView.this.getId());
                // cleanView();
            }
        }
    };

}
