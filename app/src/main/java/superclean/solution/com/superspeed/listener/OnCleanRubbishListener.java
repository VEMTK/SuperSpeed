package superclean.solution.com.superspeed.listener;

/**
 * Created by hwl on 2017/11/24.
 */

public interface OnCleanRubbishListener {
    void onCleaning (long cleanSize);

    void onCleanFinish ();

    void onCleanStart ();
}
