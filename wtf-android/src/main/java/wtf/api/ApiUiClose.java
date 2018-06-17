package wtf.api;

import wtf.sdk.JSO;
import wtf.sdk.WtfApi;
import wtf.sdk.WtfCallback;
import wtf.sdk.WtfHandler;
import wtf.sdk.WtfUi;

public class ApiUiClose extends WtfApi {

    @Override
    public WtfHandler getHandler() {
        return new WtfHandler() {

            @Override
            public void onCall(JSO jso, final WtfCallback responseCallback) {
                WtfUi ui = getCallerUi();

                ui.closeUi(jso);

                if (null != responseCallback) responseCallback.onCallBack(jso);
            }
        };
    }
}
