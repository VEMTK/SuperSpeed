package superclean.solution.com.superspeed.listener;


import superclean.solution.com.superspeed.bean.AppProcessInfo;

/**
 * Created by hwl on 2017/11/20.
 */

public interface OnCloseAppListener {

    void onClose (AppProcessInfo appInfor, int index);

    void onCloseComplete ();

}
