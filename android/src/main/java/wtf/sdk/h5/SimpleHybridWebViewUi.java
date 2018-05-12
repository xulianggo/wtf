package wtf.sdk.h5;

//deprecated, 因为这种能直接访问api的是之前的尝试，并不安全（对全是自己的页面可以接受）。
// 对其它高可扩展性的 混编，需要重构设计

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import wtf.sdk.HybridUi;
import wtf.sdk.platform;

public class SimpleHybridWebViewUi extends HybridUi {
    //final private static String LOGTAG = new Throwable().getStackTrace()[0].getClassName();
    private JsBridgeWebView _wv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String title = platform.optString(this.getUiData("title"));
        if (!platform.isEmptyString(title)) {
            setTitle(title);
        }

        final Context _ctx = this;

        _wv = new JsBridgeWebView(_ctx);
        _wv.setBackgroundColor(Color.TRANSPARENT);
        setContentView(_wv);

        String address = platform.optString(this.getUiData("address"));

        String url = "";
        if (address == null || "".equals(address)) {
            url = "file://" + platform.getLocalWebRoot() + "error.htm";
        } else {
            if (address.matches("^\\w+://.*$")) {
                //if have schema already
                url = address;
            } else {
                //assume local...
                url = "file://" + platform.getLocalWebRoot() + address;
            }
        }

        //pre-register api handlers base on config.json:
        platform.preRegisterApiHandlers(_wv, this);

//        String ind = HybridTools.optString(this.getUiData("ind"));
//        if (!HybridTools.isEmptyString(ind)) {
//            _wv.setInd(false);
//        }
//
        _wv.loadUrl(url);

        //fix the problem about the background for API(11-18)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT
                ) {
            _wv.setLayerType(_wv.LAYER_TYPE_SOFTWARE, null);
        }
    }

    protected void onPostResume() {
        super.onPostResume();
        if (null != _wv) {
            _wv.loadUrl("javascript:try{$(document).trigger('postresume');}catch(ex){}");
        }
    }

    protected void onResume() {
        super.onResume();
        if (null != _wv) {
            _wv.loadUrl("javascript:try{$(document).trigger('resume');}catch(ex){}");
        }
    }

    protected void onPause() {
        super.onPause();
        if (null != _wv) {
            _wv.loadUrl("javascript:try{$(document).trigger('pause');}catch(ex){}");
        }
    }
}
