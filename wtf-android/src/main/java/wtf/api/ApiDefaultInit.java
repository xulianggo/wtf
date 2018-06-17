package wtf.api;

import android.util.Log;

import wtf.sdk.JSO;
import wtf.sdk.WtfApi;
import wtf.sdk.WtfCallback;
import wtf.sdk.WtfHandler;
import wtf.sdk.WtfTools;
import wtf.sdk.WtfUi;

public class ApiDefaultInit extends WtfApi {
    final private static String LOGTAG = new Throwable().getStackTrace()[0].getClassName();

    @Override
    public WtfHandler getHandler() {
        return new WtfHandler() {

            @Override
            public void onCall(JSO jso, final WtfCallback responseCallback) {

                WtfUi callerUi = getCallerUi();
                JSO uiName = callerUi.getUiData("name");

                JSO rt = new JSO();
                rt.setChild("STS", "OK");
                rt.setChild("ui_name", uiName);

                //TMP...TODO !!! （第一阶段。。。）
//从 cache中读出最近缓存的 myapp.js的内容
                //如果缓存为空，先使用随包的，然后异步后台去抓取最新的（提交正确的参数？）然后覆盖写进缓存
                //如果缓存不为空，就使用缓存的，然后也异步查看有没有更新

                String build_type = "D";//WtfTools.getAppConfig("build_type").asString();//TODO

                //build_type = "L";//uncomment for TMP test live...

                Log.v(LOGTAG, "build_type=" + build_type);
                rt.setChild("build_type", build_type);

                //String build_version = WtfTools.getAppConfig("build_version").asString();//TODO
                //TODO 直接把 App初始化的相关代码移过来算了?
                String build_version = WtfTools.loadUserConfig("build_version").asString();
                rt.setChild("build_version", build_version);

                //JSO network_status = (JSO) HybridTools.getCacheFromMem(HybridTools.NETWORK_STATUS);
                //Log.v(LOGTAG, "network_status=" + network_status);
                //rt.setChild("network_status", network_status);

                String myapp_s = WtfTools.readAssetInStr("web/app/myapp.js");

                //Log.v(LOGTAG, "myapp_s=" + myapp_s);
                rt.setChild("myapp_s", myapp_s);

                //callerUi
                responseCallback.onCallBack(rt);
            }
        };

    }
}
