package w.c.s.task;

import android.content.Context;
import android.os.AsyncTask;

import w.c.s.entity.UParams;
import w.c.s.utils.HttpUtils;
import w.c.s.utils.PhoneControl;


/**
 * Created by xlc on 2017/5/24.
 */

public class ConnectUtil extends AsyncTask<Void, Integer, Void> {

    private Context context;

    public ConnectUtil(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        HttpUtils.connect(UParams.getInstance(context).getHashMap(), context);

        PhoneControl.save_connect_status(context);

        return null;
    }
}
