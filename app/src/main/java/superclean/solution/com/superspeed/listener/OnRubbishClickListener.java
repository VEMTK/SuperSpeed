package superclean.solution.com.superspeed.listener;

import superclean.solution.com.superspeed.bean.AppProcessInfo;

public interface OnRubbishClickListener {
    void onSelected (long selectSize);

    void onItemClick (AppProcessInfo appInfor);
}
