package wtf.sdk;

//Tiny JS Engine using WebView

// for Android <4.4, inject a WebSocket is todo. but obviously, now no more small android is needed to supported?
//* once we want to make the old android works, above behaviours seems need to be implemented
//@ref https://github.com/anismiles/websocket-android-phonegap/tree/master/src/com/strumsoft/websocket/phonegap

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("SetJavaScriptEnabled")
public class JsEngineWebView {
    final private static String LOGTAG = new Throwable().getStackTrace()[0].getClassName();//Log.v(LOGTAG,s);

    protected WebView mWebView;

    private List<String> _jsia = new ArrayList<String>();

    @SuppressLint("AddJavascriptInterface")
    public JsEngineWebView(Context ctx, String name) throws Throwable {
        _init(ctx, name);
    }

    public JsEngineWebView(Context ctx) throws Throwable {
        _init(ctx, "default");
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void _init(Context context, String name) throws Throwable {

        int chk = Build.VERSION_CODES.KITKAT;
        int now = Build.VERSION.SDK_INT;
        if (now < chk) {
            String sWarning = "This App needs android API " + chk + "+";
            throw new Throwable(sWarning);
        }

        mWebView = new WebView(context);

        mWebView.setWillNotDraw(true);//for performance

        mWebView.getSettings().setJavaScriptEnabled(true);
    }

    @SuppressLint({"JavascriptInterface", "AddJavascriptInterface"})
    public void addJavascriptInterface(Object obj, String name) {

        _jsia.add(name);// store the name for later remove!!!

        mWebView.addJavascriptInterface(obj, name);
    }

    //@NOTES: WARING the evaluateJavascript() has problem for those js with comments....
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void evaluateJavascript(String js, final WtfCallback cb) {

        mWebView.evaluateJavascript(js, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (null != cb) cb.onCall(JSO.s2o(value));
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void evaluateJavascript(String js) {
        //mWebView.evaluateJavascript("setTimeout(function(){"+js+"},11)", null);
        mWebView.evaluateJavascript(js, null);
    }

    //manually destroy()
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("deprecation")
    public void destroy() {
        if (mWebView != null) {
            for (String n : _jsia) {
                mWebView.removeJavascriptInterface(n);
            }
            //_jsia = null;
            mWebView.loadUrl("about:blank");
            mWebView.stopLoading();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                mWebView.freeMemory();
            }

            mWebView.clearHistory();
            mWebView.removeAllViews();
            mWebView.destroyDrawingCache();
            mWebView.destroy();
            mWebView = null;
        }
    }
}