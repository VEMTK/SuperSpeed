package w.c.s.jni;

/**
 * Created by xlc on 2017/5/27.
 */

public class Ja {

    static
    {
        System.loadLibrary("zusg");
    }

    public static native String getPublicKey(Object obj);

}
