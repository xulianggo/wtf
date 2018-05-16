package wtf.api;

import java.util.Date;
import wtf.sdk.*;

public class ApiPingPong extends WtfApi {
    @Override
    public void handler(JSO jso, WtfCallback cbFunc) {

        jso.setChild("STS", JSO.s2o("TODO"));
        jso.setChild("pong", JSO.s2o("" + (new Date()).getTime()));
        cbFunc.onCallBack(jso);
    }

}
