package wtf.api;

import java.util.Date;

import wtf.sdk.JSO;
import wtf.sdk.WtfApi;
import wtf.sdk.WtfCallback;
import wtf.sdk.WtfHandler;

public class ApiPingPong extends WtfApi {

    @Override
    public WtfHandler getHandler() {
        return new WtfHandler() {

            @Override
            public void onCall(JSO jso, final WtfCallback responseCallback) {

                jso.setChild("STS", JSO.s2o("TODO"));
                jso.setChild("pong", JSO.s2o("" + (new Date()).getTime()));
                responseCallback.onCallBack(jso);
            }

        };
    }
}
