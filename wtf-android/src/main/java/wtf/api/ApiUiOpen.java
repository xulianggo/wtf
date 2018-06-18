package wtf.api;

//@doc https://szu-bdi.gitbooks.io/app-hybrid/content/

import wtf.sdk.JSO;
import wtf.sdk.WtfApi;
import wtf.sdk.WtfCallback;
import wtf.sdk.WtfEventHandler;
import wtf.sdk.WtfHandler;
import wtf.sdk.WtfTools;
import wtf.sdk.WtfUi;
import wtf.sdk.WtfUiCallback;

public class ApiUiOpen extends WtfApi {
    final private static String LOGTAG = new Throwable().getStackTrace()[0].getClassName();

    @Override
    public WtfHandler getHandler() {
        return new WtfHandler() {

            @Override
            public void onCall(JSO jso, final WtfCallback responseCallback) {

                String t = jso.getChild("name").toString();
                String uiName = (!WtfTools.isEmptyString(t)) ? t : "UiContent";//default to UiContent

                WtfTools.startUi(uiName, jso.toString(true), getCallerUi(), new WtfUiCallback() {
                    @Override
                    public void onCall(final WtfUi ui) {

                        //listen "close" event
                        //TODO make it constant as WtfEventWhenClose
                        ui.on(WtfTools.WtfEventWhenClose, new WtfEventHandler() {

                            @Override
                            public void onCall(String eventName, JSO jsoCallback) {

                                //manually close
                                //ui.finish();

                                //api callback
                                if (null != responseCallback)
                                    responseCallback.onCall(jsoCallback);
                            }
                        });
                    }
                });
            }
        };
    }
}
