package wtf.h5;

//简单版 H5 Hybrid，后面还要加 Advanced 的咯

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;

import java.util.ArrayList;

import wtf.sdk.JSO;
import wtf.sdk.WtfApi;
import wtf.sdk.WtfCallback;
import wtf.sdk.WtfDialogCallback;
import wtf.sdk.WtfTools;
import wtf.sdk.WtfUi;

public class SimpleHybridWebViewUi extends WtfUi {
    final private static String LOGTAG = new Throwable().getStackTrace()[0].getClassName();
    private JsBridgeWebView _wv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String title = WtfTools.optString(this.getUiData("title"));
        if (!WtfTools.isEmptyString(title)) {
            setTitle(title);
        }

        final Context _ctx = this;

        _wv = new JsBridgeWebView(_ctx);
        _wv.setBackgroundColor(Color.TRANSPARENT);
        setContentView(_wv);

        String address = WtfTools.optString(this.getUiData("address"));

        String url = "";
        if (address == null || "".equals(address)) {
            url = "file://" + WtfTools.getLocalWebRoot() + "error.htm";
        } else {
            if (address.matches("^\\w+://.*$")) {
                //if have schema already
                url = address;
            } else {
                //assume local...
                url = "file://" + WtfTools.getLocalWebRoot() + address;
            }
        }

        //pre-register api handlers base on config.json:
        preRegisterApiHandlers(_wv, this);

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

    public static void preRegisterApiHandlers(JsBridgeWebView wv, final WtfUi callerAct) {
        String name = WtfTools.optString(callerAct.getUiData("name"));
        if (WtfTools.isEmptyString(name)) {
            WtfTools.quickShowMsgMain("ConfigError: caller act name empty?");
            return;
        }
        JSO uia = WtfTools.getAppConfig(WtfTools.API_AUTH);
        if (uia == null) {
            WtfTools.quickShowMsgMain("ConfigError: empty " + WtfTools.API_AUTH);
            return;
        }
        JSO apia = WtfTools.getAppConfig(WtfTools.API_MAPPING);
        if (apia == null) {
            WtfTools.quickShowMsgMain("ConfigError: empty " + WtfTools.API_MAPPING);
            return;
        }

        //JSONObject authObj = uia.optJSONObject(name);
        JSO authObj = uia.getChild(name);
        if (authObj == null || authObj.isNull()) {
            WtfTools.quickShowMsgMain("ConfigError: not found auth for " + name + " !!!");
            return;
        }
        Log.v(LOGTAG, " authObj=" + authObj);

        String address = WtfTools.optString(callerAct.getUiData("address"));
        JSO foundAuth = WtfTools.findSubAuth(authObj, address);
        if (foundAuth == null) {
            WtfTools.quickShowMsgMain("ConfigError: not found match auth for address (" + address + ") !!!");
            return;
        }
        Log.v(LOGTAG, " foundAuth=" + foundAuth);
        ArrayList<JSO> ja = foundAuth.asArrayList();
        for (int i = 0; i < ja.size(); i++) {
            String v = ja.get(i).asString();
            if (!WtfTools.isEmptyString(v)) {
                String clsName = apia.getChild(v).asString();
                Log.v(LOGTAG, "binding api " + v + " => " + clsName);
                if (WtfTools.isEmptyString(clsName)) {
                    WtfTools.quickShowMsgMain("ConfigError: config not found for api=" + v);
                    continue;
                }
                Class targetClass = null;
                try {
                    //reflection:
                    targetClass = Class.forName(clsName);
                    Log.v(LOGTAG, "class " + clsName + " found for name " + name);
                } catch (ClassNotFoundException e) {
                    WtfTools.quickShowMsgMain("ConfigError: class not found " + clsName);
                    continue;
                }
                try {
                    WtfApi api = (WtfApi) targetClass.newInstance();
                    api.setCallerUi(callerAct);
                    //wv.registerHandler(v, api);
                    callerAct.registerHandler(v, api);
                } catch (Throwable t) {
                    t.printStackTrace();
                    WtfTools.quickShowMsgMain("ConfigError: faile to create api of " + clsName);
                    continue;
                }
            }
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

    @SuppressLint("SetJavaScriptEnabled")
    public static class JsBridgeWebView extends WebView {
        final private static String LOGTAG = new Throwable().getStackTrace()[0].getClassName();

        //    Map<String, WtfApi> apiMap = new HashMap<String, WtfApi>();
        private nativejsb _nativejsb = null;

        public JsBridgeWebView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public JsBridgeWebView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init(context);
        }

        @SuppressLint("AddJavascriptInterface")
        public JsBridgeWebView(Context context) {
            super(context);
            init(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                this.addJavascriptInterface(new nativejsb(context), "nativejsb");
            } else {
                _nativejsb = new nativejsb(context);
            }
        }


        private void init(Context context) {
            this.setVerticalScrollBarEnabled(false);
            this.setHorizontalScrollBarEnabled(false);
            this.getSettings().setJavaScriptEnabled(true);
            //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //  WebView.setWebContentsDebuggingEnabled(true);
            //}
            this.setWebViewClient(new MyWebViewClient(context, this));
            this.setWebChromeClient(new MyWebChromeClient(context, this));
        }

        class nativejsb {
            private Context _context;

            public nativejsb(Context context) {
                _context = context;
            }

            @JavascriptInterface
            public String getVersion() {
                return "20161216";
            }

            //NOTES: for native object injected into the webview, the parameters must be primitive.
            @JavascriptInterface
            public String js2app(final String callbackId, String handlerName, final String param_s) {

                final WtfUi theWtfUi = (WtfUi) _context;
                final String uiName = theWtfUi.getUiData("name").toString();

                final WtfCallback responseFunction = new WtfCallback() {

                    @Override
                    public void onCall(final JSO jso) {
                        //这里 runOnUiThread 是经验，要在它的主线程上面来 evaluateJavascript、loadUrl
                        ((Activity) _context).runOnUiThread(new Runnable() {
                            //@TargetApi(Build.VERSION_CODES.KITKAT)
                            @Override
                            public void run() {
                                JSO msg = new JSO();
                                msg.setChild("responseId", callbackId);
                                msg.setChild("responseData", jso);
                                String s = msg.toString(true);
                                if ("".equals(s) || s == null) s = "null";
                                Log.v(LOGTAG, "js2app s ==> " + s);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    evaluateJavascript("WebViewJavascriptBridge._app2js(" + s + ");", new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {
                                            Log.v(LOGTAG, " onReceiveValue " + value);
                                        }
                                    });
                                } else {
                                    loadUrl("javascript:WebViewJavascriptBridge._app2js(" + s + ")");
                                }
                            }
                        });
                    }

                };

                final WtfApi api = theWtfUi.apiMap.get(handlerName);

                if (api != null) {
                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            //handler.onCall(JSO.s2o(param_s), responseFunction);
                            api.getHandler().onCall(JSO.s2o(param_s), responseFunction);
                        }
                    })).start();
                } else {
                    String msg = " api " + handlerName + " for uiName(" + uiName + ") not registered";
                    Log.v(LOGTAG, msg);
                    WtfTools.quickShowMsgMain(msg);
                }
                return "OK";
            }
        }

        class MyWebChromeClient extends WebChromeClient {
            Context _ctx = null;
            JsBridgeWebView wv = null;

            public MyWebChromeClient(Context context, JsBridgeWebView wv) {
                this._ctx = context;
                this.wv = wv;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                try {
                    WtfTools.appAlert(_ctx, message, new WtfDialogCallback() {
                        @Override
                        public void onCall(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            result.confirm();
                        }
                    });
                } catch (Throwable th) {
                    th.printStackTrace();
                    result.confirm();
                }
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult jsrst) {
                WtfTools.appConfirm(_ctx, message, new WtfDialogCallback() {
                    @Override
                    public void onCall(DialogInterface dialog, int which) {
                        jsrst.confirm();
                    }
                }, new WtfDialogCallback() {
                    @Override
                    public void onCall(DialogInterface dialog, int which) {
                        jsrst.cancel();
                    }
                });
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String origin, String message, String defaultValue, final JsPromptResult result) {

                if ("nativejsb:".equals(message)) { //for < API 17, we using prompt() to hack
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        try {
                            JSONArray array = new JSONArray(defaultValue);
                            final String callbackId = array.getString(0);
                            final String handlerName = array.getString(1);
                            final String data_s = array.getString(2);
                            if (_nativejsb != null) {
                                final Activity act = ((Activity) this._ctx);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        act.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                _nativejsb.js2app(callbackId, handlerName, data_s);
                                            }
                                        });
                                    }

                                }, 11);
                            }
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                        }
                        result.confirm();
                        return true;
                    }
                }
                return super.onJsPrompt(view, origin, message, defaultValue, result);
            }

            //        @Override
            //        public void onProgressChanged(WebView view, int progress) {
            //            super.onProgressChanged(view, progress);
            //            // Do something cool here
            //            if (null != this.wv) {
            //                try {
            //                    //Log.v(LOGTAG, "onProgressChanged " + progress);
            //                    this.wv.progressDialog.setProgress(progress);
            //                } catch (Throwable th) {
            //                    th.printStackTrace();
            //                }
            //            }
            //        }
        }

        class MyWebViewClient extends WebViewClient {
            Context _ctx;
            JsBridgeWebView wv;

            public MyWebViewClient(Context context, JsBridgeWebView wv) {
                this._ctx = context;
                this.wv = wv;
                //            if (null == this.wv.progressDialog) {
                //                this.wv.progressDialog = new ProgressDialog(this._ctx);
                //                this.wv.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                //                this.wv.progressDialog.setMax(100);
                //            }
                //            this.wv.progressDialog.setTitle("Loading...");
                //            this.wv.progressDialog.setCanceledOnTouchOutside(false);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //Log.v(LOGTAG, "onPageFinished " + url);

                notifyPollingInject(view, url);
                super.onPageFinished(view, url);
                //            try {
                //                this.wv.progressDialog.hide();
                //                this.wv.progressDialog.dismiss();
                //            } catch (Throwable th) {
                //                th.printStackTrace();
                //            }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url != null && url.startsWith("file:")) {
                    //for local no eed to show load indicator
                } else {
                    //                try {
                    //                    this.wv.progressDialog.show();
                    //                } catch (Throwable th) {
                    //                    th.printStackTrace();
                    //                }
                }

                notifyPollingInject(view, url);
                super.onPageStarted(view, url, favicon);
            }

            public void notifyPollingInject(WebView view, String url) {
                //inject
                String jsContent = WtfTools.readAssetInStr("WebViewJavascriptBridge.js", true);

                view.loadUrl("javascript:" + jsContent);

                //for < API 17
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    String nativejsb_s = "window.nativejsb=window.WebViewJavascriptBridge.nativejsb;";
                    view.loadUrl("javascript:" + nativejsb_s);
                }
            }

            //NOTES
            //for <input type=file/> we suggest to give it up. using api to invoke activity to handle it...
            //which means the page need to call the jsb for the api by yourself ;)
        }

    }
}
