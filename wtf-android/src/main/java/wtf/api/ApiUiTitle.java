package wtf.api;

//@doc https://szu-bdi.gitbooks.io/app-hybrid/content/

import java.util.Date;

import wtf.sdk.JSO;
import wtf.sdk.WtfApi;
import wtf.sdk.WtfCallback;
import wtf.sdk.WtfHandler;

public class ApiUiTitle extends WtfApi {
    final private static String LOGTAG = (((new Throwable()).getStackTrace())[0]).getClassName();

    @Override
    public WtfHandler getHandler() {
        return new WtfHandler() {
            @Override
            public void onCall(JSO jso, WtfCallback responseCallback) {
                jso.setChild("STS", JSO.s2o("TODO"));
                jso.setChild("pong", JSO.s2o("" + (new Date()).getTime()));
                responseCallback.onCall(jso);
                //handler(JSO.o2s(jso), cbFunc);
            }
        };
    }
}
