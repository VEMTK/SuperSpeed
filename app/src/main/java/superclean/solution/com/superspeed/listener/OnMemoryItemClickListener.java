package superclean.solution.com.superspeed.listener;

import java.util.List;

import superclean.solution.com.superspeed.bean.AppProcessInfo;


/**
 * Created by hwl on 2017/11/18.
 */

public interface OnMemoryItemClickListener {

    void onItemClick (List<AppProcessInfo> applist);

    void reLoadList (String pkgStr);
}
