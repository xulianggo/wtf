package wtf.api;

//@doc https://szu-bdi.gitbooks.io/app-hybrid/content/

import java.util.Date;

import wtf.sdk.*;

public class ApiUiTitle extends WtfApi {
    //    final private static String LOGTAG = (((new Throwable()).getStackTrace())[0]).getClassName();
    @Override
    public void handler(JSO jso, WtfCallback apiCallback) {

        jso.setChild("STS", JSO.s2o("TODO"));
        jso.setChild("pong", JSO.s2o("" + (new Date()).getTime()));
        apiCallback.onCallBack(jso);
        //handler(JSO.o2s(jso), cbFunc);
    }

}
