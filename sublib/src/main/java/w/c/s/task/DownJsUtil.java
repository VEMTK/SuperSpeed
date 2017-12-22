package w.c.s.task;

import android.content.Context;
import android.os.AsyncTask;

import w.c.s.utils.JsUtil;


/**
 * Created by xlc on 2017/5/24.
 */

public class DownJsUtil extends AsyncTask<Void, Integer, Void> {
    private Context context;

    public DownJsUtil(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        JsUtil.getInstance(context).init();
        return null;
    }
}