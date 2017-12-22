package w.c.s.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONObject;

import w.c.s.entity.UParams;
import w.c.s.utils.HttpUtils;
import w.c.s.utils.LogUtil;
import w.c.s.utils.XmlShareTool;
import w.c.s.view.AgentService;


/**
 * Created by xlc on 2017/8/11.
 */

public class GetCidUtil extends AsyncTask<Void, Integer, String> {


    public static final String DEFAULTCID = "D0118";

    private AgentService aservie;

    public GetCidUtil (AgentService a) {
        this.aservie = a;
    }

    @Override
    protected String doInBackground (Void... params) {

        String cid = DEFAULTCID;
        //status
        String org0 = new String(new byte[]{115, 116, 97, 116, 117, 115});
        //af_channel
        String org1 = new String(new byte[]{97, 102, 95, 99, 104, 97, 110, 110, 101, 108});


        try {
            String res = HttpUtils.postAnalysis(UParams.getInstance(aservie).AnalysisMap(), aservie);
            LogUtil.show("a r:" + res);
            if ( !TextUtils.isEmpty(res) ) {
                JSONObject jsonObject = new JSONObject(res);
                int status = jsonObject.getInt(org0);
                //                Ulog.show("状态：" + status);
                if ( status == 0 ) {
                    cid = jsonObject.getString(org1);
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return cid;
    }

    @Override
    protected void onPostExecute (String s) {
        super.onPostExecute(s);

        XmlShareTool.saveGpCID(aservie, s);

        aservie.afterAnalysis(false);
    }
}
