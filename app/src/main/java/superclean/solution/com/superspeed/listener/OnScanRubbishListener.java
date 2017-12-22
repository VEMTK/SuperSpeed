package superclean.solution.com.superspeed.listener;

import java.util.List;

import superclean.solution.com.superspeed.bean.AppProcessInfo;

public interface OnScanRubbishListener {

    void onScannig (List<AppProcessInfo> cacheList, List<AppProcessInfo> logList, List<AppProcessInfo> tempList, List<AppProcessInfo> appList, List<AppProcessInfo> fileList, String filePath);

    void onScanFinish ();

    void onProgress (long current, long total);
}