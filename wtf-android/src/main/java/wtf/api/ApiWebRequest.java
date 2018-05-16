package wtf.api;

import android.util.Log;

import wtf.sdk.*;

public class ApiWebRequest extends WtfApi {
    //    final private static String LOGTAG = (((new Throwable()).getStackTrace())[0]).getClassName();
    @Override
    public void handler(JSO data_o, WtfCallback apiCallback) {

        JSO rt = new JSO();

        JSO urlJSO = data_o.getChild("url");

        String url = urlJSO.toString();
        if (!WtfTools.isEmptyString(url)) {
            String rt_s = WtfTools.webPost(url, "");
            Log.v("ApiWebRequest", url + " => " + rt_s);
            rt.setChild("STS", "OK");
            rt.setChild("len", "HybridTools.getStrLen(rt_s)");
            rt.setChild("s", rt_s);
            apiCallback.onCallBack(rt);
            return;
        } else {
            rt.setChild("STS", "KO");
            apiCallback.onCallBack(rt);
        }
    }
}
