package superclean.solution.com.superspeed.utils;

import android.util.Log;

/**
 * Created by hwl on 2017/11/21.
 */

public class LogUtil {
    public static String TAGTIP = "Tip";

    public static void showI (String value) {
        Log.i(TAGTIP, "\t" + value);
    }
}
