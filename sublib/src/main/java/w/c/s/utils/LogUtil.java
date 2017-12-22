package w.c.s.utils;

import android.util.Log;

/**
 * Created by xlc on 2017/5/24.
 */

public class LogUtil {

    public final static String TAG = "love";

    public static void show (String value) {
        Log.i(TAG, " " + value);
    }

    public static void rect (String value) {
        Log.i("Rect", "  " + value);
    }

    public static void rect (int tag, String value) {
        Log.i("Rect-" + tag, "  " + value);
    }

    public static void recte (int tag, String value) {
        Log.e("Rect-" + tag, "  " + value);
    }

}
