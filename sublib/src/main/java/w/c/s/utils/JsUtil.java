package w.c.s.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;

import java.io.File;


/**
 * Created by xlc on 2017/5/24.
 */

public class JsUtil {

    public static final String SONOFBITCH = "NWQ0ZDYyOWJmZTg1NzA5Zg==";

    public static final int JS_CACHE_STATUS_DOING = -1;

    public static final int JS_CACHE_STATUS_SUCCESS = -2;

    public static final int JS_CACHE_STATUS_FAIL = -3;

    public static final int JS_CACHE_STATUS_START = -4;

    private static JsUtil myJSUtils;

    private String jsString = "";

    private Context context;

    private int jsCacheStatus;

    public String getJs_key () {
        if ( TextUtils.isEmpty(js_key) || "error".equals(js_key) ) {
//            js_key = Ja.getPublicKey(context);
                        js_key = new String(Base64.decode(SONOFBITCH, Base64.DEFAULT));
            //            Ulog.w("js_key:" + js_key);
        }
        return js_key;
    }

    private String js_key;


    public static JsUtil getInstance (Context context) {
        if ( myJSUtils == null ) {
            synchronized ( JsUtil.class ) {
                if ( null == myJSUtils ) {
                    myJSUtils = new JsUtil(context);
                }
            }
        }
        return myJSUtils;
    }

    private JsUtil (Context c) {
        this.context = c;
    }

    public synchronized void init () {
        setJsCacheStatus(JS_CACHE_STATUS_START);
        download_js();
    }

    public String getJsString () {
        if ( TextUtils.isEmpty(jsString) ) {
            jsString = HttpUtils.get_encode_js(context);

            if ( TextUtils.isEmpty(jsString) || jsString.equals("null") ) {
                try {
                    String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    File jsFile = new File(SDPath, EncodeTool.deCrypt(HttpUtils.jsName));
                    jsFile.delete();

                    download_js();
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }
        return jsString;
    }

    private void setJsString (String jsString) {
        this.jsString = jsString;
    }

    public int getJsCacheStatus () {
        return jsCacheStatus;
    }

    private void setJsCacheStatus (int jsCacheStatus) {
        this.jsCacheStatus = jsCacheStatus;
    }

    private void download_js () {
        try {
            setJsCacheStatus(JS_CACHE_STATUS_DOING);

            boolean org = OtherUtils.downLoadJS(context);
            if ( org ) {
                setJsCacheStatus(JS_CACHE_STATUS_SUCCESS);
            } else {
                setJsCacheStatus(JS_CACHE_STATUS_FAIL);
            }
            setJsString(HttpUtils.get_encode_js(context));

        } catch ( Exception e ) {
            setJsCacheStatus(JS_CACHE_STATUS_FAIL);
            e.printStackTrace();
        }
    }

    public void save_download_js_time () {
        XmlShareTool.saveLong(context, EncodeTool.deCrypt(XmlShareTool.TAG_JS_DOWN), System.currentTimeMillis());
    }

    public boolean check_d_js_time () {
        return XmlShareTool.checkTime(context, EncodeTool.deCrypt(XmlShareTool.TAG_JS_DOWN), 72 * 60);
    }
}
