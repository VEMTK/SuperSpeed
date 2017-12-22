package superclean.solution.com.superspeed.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;

import superclean.solution.com.superspeed.utils.SettingDefaultList;
import superclean.solution.com.superspeed.view.XmlShareUtil;


/**
 * Created by hwl on 2017/12/15.
 */

public class BackTask extends AsyncTask<Void, Integer, Boolean> {

    private Context context;
    private boolean first = false;

    public BackTask (Context context, boolean first) {
        this.context = context;
        this.first = first;
    }

    @Override
    protected Boolean doInBackground (Void... params) {
        SettingDefaultList.getInstance(context);

        if ( first ) XmlShareUtil.saveInt(context, XmlShareUtil.TAG_FILE_NUM, getFilecounts(Environment.getExternalStorageDirectory()));

        return true;
    }

    private int getFilecounts (File root) {
        if ( root == null || !root.exists() ) {
            return 0;
        }
        int org = 0;
        File[] files = root.listFiles();
        if ( files != null ) {
            for ( File file : files ) {
                if ( !file.isFile() ) {
                    org += getFilecounts(file);
                } else {
                    org++;
                }
            }
        }
        return org;
    }


    @Override
    protected void onPostExecute (Boolean result) {
        super.onPostExecute(result);
    }
}