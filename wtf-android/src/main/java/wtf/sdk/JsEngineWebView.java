package wtf.sdk;

//Tiny JS Engine using WebView

// for Android <4.4, inject a WebSocket is todo. but obviously, now no more small android is needed to supported?
//* once we want to make the old android works, above behaviours seems need to be implemented
//@ref https://github.com/anismiles/websocket-android-phonegap/tree/master/src/com/strumsoft/websocket/phonegap

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Base64;
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

    private void _init(Context context, String name) throws Throwable {

        int chk = Build.VERSION_CODES.KITKAT;
        int now = Build.VERSION.SDK_INT;
        if (now < chk) {
            String sWarning = "This App needs android API " + chk + " above";
            throw new Throwable(sWarning);
        }

        mWebView = new WebView(context);

        //mWebView.willNotDraw();
        mWebView.setWillNotDraw(true);//for performance

        mWebView.getSettings().setJavaScriptEnabled(true);

        //@NOTES: Important here, that some must be done to let come JavascriptInterface
        this._loadJavaScript("console.log('loaded JsEngineWebView " + name + "')");
    }

    private void _loadJavaScript(String jsstr) {
        byte[] data;
        try {
            jsstr = "<script>" + jsstr + "</script>";
            data = jsstr.getBytes("UTF-8");
            final String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            mWebView.loadUrl("data:text/html;charset=utf-8;base64," + base64);
        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    @SuppressLint({"JavascriptInterface", "AddJavascriptInterface"})
    public void addJavascriptInterface(Object obj, String name) {

        _jsia.add(name);// store the name for later remove!!!

        mWebView.addJavascriptInterface(obj, name);
    }

    //TODO ValueCallback<String> => WtfJsCallback ?
    //@NOTES: the evaluateJavascript() has bug for those js with comments....
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void evaluateJavascript(String js, ValueCallback<String> resultCallback) {

        mWebView.evaluateJavascript(js, resultCallback);
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

//    public WebView getWebView() {
//        return mWebView;
//    }
}

//stub TODO
//    private static final String[] mFilterMethods = {"getClass", "hashCode", "notify", "notifyAll", "equals", "toString", "wait",};
//
//    private boolean filterMethods(String methodName) {
//        for (String method : mFilterMethods) {
//            if (method.equals(methodName)) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//stub
//e.g.
//    evaluateJavascript("WebViewJavascriptBridge._app2js(" + s + ");", new ValueCallback<String>() {
//        @Override
//        public void onReceiveValue(String value) {
//            Log.v(LOGTAG, " onReceiveValue " + value);
//        }
//    });

//NOTES: for ui need runOnUiThread()....
//        ((Activity) _context).runOnUiThread(new Runnable() {
//                                                //@TargetApi(Build.VERSION_CODES.KITKAT)
//                                                @Override
//                                                public void run() {
//                                                }
//                                            }