package wtf.api;

//@doc https://szu-bdi.gitbooks.io/app-hybrid/content/

import wtf.sdk.*;

public class ApiUiOpen extends WtfApi {
    final private static String LOGTAG = new Throwable().getStackTrace()[0].getClassName();

    @Override
    public void handler(JSO jso, final WtfCallback apiCallback) {

        String t = jso.getChild("name").toString();
        String uiName = (!WtfTools.isEmptyString(t)) ? t : "UiContent";//default to UiContent

        WtfTools.startUi(uiName, jso.toString(true), getCallerUi(), new WtfUiCallback() {
            @Override
            public void onCallBack(final WtfUi ui) {

                //listen "close" event
								//TODO make it constant as WtfEventWhenClose
                ui.on(WtfTools.WtfEventWhenClose, new WtfCallback() {

                    @Override
                    public void onCallBack(JSO jsoCallback) {

                        //manually close it
                        ui.finish();

                        //api callback
                        if (null != apiCallback)
                            apiCallback.onCallBack(jsoCallback);
                    }
                });
            }
        });
    }
}
