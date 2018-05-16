package wtf.api;

import wtf.sdk.*;

public class ApiUiClose extends WtfApi {
    @Override
    public void handler(JSO jso, WtfCallback cbFunc) {
        WtfUi ui = getCallerUi();

        ui.closeUi(jso);

        if (null != cbFunc) cbFunc.onCallBack(jso);
    }
}
