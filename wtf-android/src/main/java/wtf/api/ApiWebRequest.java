package wtf.api;

import android.util.Log;

import wtf.sdk.JSO;
import wtf.sdk.WtfApi;
import wtf.sdk.WtfCallback;
import wtf.sdk.WtfHandler;
import wtf.sdk.WtfTools;

public class ApiWebRequest extends WtfApi {
    //    final private static String LOGTAG = (((new Throwable()).getStackTrace())[0]).getClassName();

    @Override
    public WtfHandler getHandler() {
        return new WtfHandler() {
            @Override
            public void onCall(JSO jso, WtfCallback responseCallback) {

                JSO rt = new JSO();

                JSO urlJSO = jso.getChild("url");

                String url = urlJSO.toString();
                if (!WtfTools.isEmptyString(url)) {
                    String rt_s = WtfTools.webPost(url, "");
                    Log.v("ApiWebRequest", url + " => " + rt_s);
                    rt.setChild("STS", "OK");
                    rt.setChild("len", "HybridTools.getStrLen(rt_s)");
                    rt.setChild("s", rt_s);
                    responseCallback.onCall(rt);
                    return;
                } else {
                    rt.setChild("STS", "KO");
                    responseCallback.onCall(rt);
                }
            }
        };
    }
}
