/**
 * js-mini-native framework alike weex/wx/DeviceOne/NativeScript/ReactNative by anonymous
 * <p>
 * WtfTools.js
 * => WtfTools and app manager
 * $appName/app.js
 * => app
 */

//TODO change WtfTools to WtfTool so that sync name with iOS....

package wtf.sdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import wtf.h5.SimpleHybridWebViewUi;

public class WtfTools {
    final private static String LOGTAG = new Throwable().getStackTrace()[0].getClassName();

    public final static String WtfEventWhenClose = "WtfEventWhenClose";

    public final static String NETWORK_STATUS = "_network_status_";
    //    public final static String NETWORK_STATUS = "_network_status_";
//    final static String ANDROID_APPLICATION = "_android_applicaton_";
    public final static String ANDROID_APPLICATION = "_android_applicaton_";
    public final static String UI_MAPPING = "ui_mapping";
    public final static String API_AUTH = "api_auth";
    public final static String API_MAPPING = "api_mapping";

    //    static {
    //        //native
    //        System.loadLibrary("wtf-lib");
    //    }


    private Map<String, Object> _memStore = new HashMap<String, Object>();

    //    private static Map<String, Object> _memStore = new HashMap<String, Object>();
    //private static JSO _jAppConfig = null;//new info.cmptech.JSO();
    private JSO _jAppConfig;
    private JSO _i18n;
    private String _lang;//TODO
    //TODO need
//    public  static JSO I18N(String key){
//        return sharedInstance()._i18n.getChild(key);
//    }
    private static String _localWebRoot = "";

//    private Context androidContext;

    private static JsEngineWebView _jswv = null;

//    private WtfTools(Context ctx) {
//        this.androidContext = ctx;
//    }


    //default Root JSWV....
    private static JsEngineWebView getJSWV() {
        Context _ctx = getAppContext();
        try {
            if (_jswv == null) {
                _jswv = new JsEngineWebView(_ctx);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        //inject the native object for WtfTools.js only !!!
        //_jswv.addJavascriptInterface(new nativewtf(_ctx), "native");

        //TODO 错了，不要在这里运行，应该另外弄....
        //init with the WtfTools.js
//        jswv.evaluateJavascript(readAssetInStr("platform.js", true), new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String json_string) {
//                Log.v(LOGTAG, " WtfTools.js => " + json_string);
//            }
//        });
        return _jswv;
    }

    public static void evalJs(String jsString) {
        getJSWV().evaluateJavascript(jsString);
    }

    private static WtfTools _sharedInstance;

    public static WtfTools sharedInstance() {
        if (_sharedInstance != null) return _sharedInstance;
        synchronized (WtfTools.class) {
            _sharedInstance = new WtfTools();
        }
        return _sharedInstance;
    }

    public Object MemoryLoad(String key) {
        return _memStore.get(key);
    }

    public Object MemorySave(String key, Object val) {
        return _memStore.put(key, val);
    }

//    public static Object MemoryLoad(String key) {
//        return sharedInstance().MemoryLoad(key);
//    }
//
//    public static Object MemorySave(String key, Object val) {
//        return sharedInstance().MemorySave(key, val);
//    }

    //need to call setApplication before invoke....
    public static Application getApplication() {
        Application _thisApp = null;
        try {
            _thisApp = (Application) sharedInstance().MemoryLoad(ANDROID_APPLICATION);
            if (null == _thisApp) {
                try {
                    _thisApp = (Application) Class.forName("android.app.ActivityThread")
                            .getMethod("currentApplication").invoke(null, (Object[]) null);
                    if (null != _thisApp) {
                        sharedInstance().MemorySave(ANDROID_APPLICATION, _thisApp);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            if (null == _thisApp) {
                Log.e(LOGTAG, "getApplication => null, seems forgot to setApplication and can't get from android.app.ActivityThread");
                KillAppSelf();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            KillAppSelf();
        }
        return _thisApp;
    }

    public static void setApplication(Application _thisApp) {
        if (null != _thisApp) {
            sharedInstance().MemorySave(ANDROID_APPLICATION, _thisApp);
        }
    }

    public static Context getAppContext() {
        return getApplication().getApplicationContext();
    }

    public static void quickShowMsgMain(String msg) {
        quickShowMsg(getAppContext(), msg);
    }

    public static void KillAppSelf() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static void quickShowMsg(Context mContext, String msg) {
        //@ref http://blog.csdn.net/droid_zhlu/article/details/7685084
        //A toast is a view containing a quick little message for the user.
        // The toast class helps you create and show those.
        try {
            Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    public static Object getCacheFromMem(String key) {
//        return _memStore.get(key);
//    }

//    public static Object setCacheToMem(String key, Object val) {
//        return _memStore.put(key, val);
//    }

//    public static Context getAppContext() {
//        return getApplication().getApplicationContext();
//    }

//    public static void quickShowMsgMain(String msg) {
//        quickShowMsg(getAppContext(), msg);
//    }

    //NOTES: for alert blocking, using appAlert/appConfirm
//    public static void quickShowMsg(Context mContext, String msg) {
//        //@ref http://blog.csdn.net/droid_zhlu/article/details/7685084
//        //A toast is a view containing a quick little message for the user.
//        // The toast class helps you create and show those.
//        try {
//            Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
//            toast.show();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    //persistent save/load
    //NOTES:  because getSetting will cause mis-understanding
    public static String loadUserConfig(Context mContext, String whichSp, String field) {
        SharedPreferences sp = mContext.getSharedPreferences(whichSp, Context.MODE_PRIVATE);
        String s = sp.getString(field, "");
        return s;
    }

//    public static String loadUserConfig(String field) {
//        SharedPreferences sp = getAppContext().getSharedPreferences("DEFAULT", Context.MODE_PRIVATE);
//        String s = sp.getString(field, "");
//        return s;
//    }

    public static JSO loadUserConfig(String field) {
        SharedPreferences sp = getAppContext().getSharedPreferences("DEFAULT", Context.MODE_PRIVATE);
        String s = sp.getString(field, "");
        if (s == null) return new JSO();
        return JSO.s2o(s);
    }

    public static void saveUserConfig(Context mContext, String whichSp, String field, String value) {
        SharedPreferences sp = (SharedPreferences) mContext.getSharedPreferences(whichSp, Context.MODE_PRIVATE);
        if (null == value) value = "";//I want to store sth not null

        sp.edit().putString(field, value).apply();
    }

    public static void saveUserConfig_s(String field, String value) {
        SharedPreferences sp = (SharedPreferences) getAppContext().getSharedPreferences("DEFAULT", Context.MODE_PRIVATE);
        if (null == value) value = "";//I want to store sth not null

        sp.edit().putString(field, value).apply();
    }

    public static void saveUserConfig(String field, JSO value) {
        saveUserConfig_s(field, value == null ? "null" : value.toString());
    }

    public static String webPost(String uu, String post_s) {
        String return_s = null;

        try {
            URL url = new URL(uu);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {

                conn.setDoOutput(true);
                conn.setRequestMethod("POST");

                OutputStream out = new BufferedOutputStream(conn.getOutputStream());

                //write to the stream
                out.write(post_s.getBytes("UTF-8"));

                InputStream in = new BufferedInputStream(conn.getInputStream());
                return_s = stream2string(in);
            } finally {
                try {
                    conn.disconnect();
                } catch (Throwable t) {
                }
            }
        } catch (Throwable ex) {
            //TODO 如果是 filenotfound的exception，多数是因为远程错误400之类的，待处理
            ex.printStackTrace();
            return_s = ex.getMessage();
            if (isEmptyString(return_s)) {
                return_s = "" + ex.getClass().getName();
            }
        }
        return return_s;
    }

    public static JSO fileUpload(String u, String localFile, WtfCallback progressUploadListener) {
        String mLineEnd = "\r\n";

        String mTwoHyphens = "--";

        String boundary = "*****";

        long length = 0;
        int mBytesRead, mbytesAvailable, mBufferSize;
        byte[] buffer;
        int maxBufferSize = 64 * 1024;
        String return_s = "";
        JSO rt = new JSO();
        try {

            File uploadFile = new File(getTempDirectoryPath() + localFile);//TODO TMP...
            long mTtotalSize = uploadFile.length();
            FileInputStream fileInputStream = new FileInputStream(uploadFile);

            URL url = new URL(u);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //如果有必要则可以设置Cookie
//                conn.setRequestProperty("Cookie","JSESSIONID="+cookie);

            // Set size of every block for post

            con.setChunkedStreamingMode(64 * 1024);

            // Allow Inputs & Outputs
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);

            // Enable POST method
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            DataOutputStream outputStream = null;
            outputStream = new DataOutputStream(con.getOutputStream());
            outputStream.writeBytes(mTwoHyphens + boundary + mLineEnd);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale("en_US")).format(new Date());

            outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + timeStamp + ".jpg\"" + mLineEnd);
            outputStream.writeBytes("Content-Type:application/octet-stream \r\n");
            outputStream.writeBytes(mLineEnd);

            mbytesAvailable = fileInputStream.available();
            mBufferSize = Math.min(mbytesAvailable, maxBufferSize);
            buffer = new byte[mBufferSize];

            // Read file
            mBytesRead = fileInputStream.read(buffer, 0, mBufferSize);

            while (mBytesRead > 0) {
                outputStream.write(buffer, 0, mBufferSize);
                length += mBufferSize;

                //progressUploadListener((int) ((length * 100) / mTtotalSize));
                int i = ((int) ((length * 100) / mTtotalSize));
                if (null != progressUploadListener)
                    progressUploadListener.onCall(JSO.s2o("{\"i\":" + i + "}"));
                Log.v(LOGTAG, "fileUpload ... " + i);

                mbytesAvailable = fileInputStream.available();

                mBufferSize = Math.min(mbytesAvailable, maxBufferSize);

                mBytesRead = fileInputStream.read(buffer, 0, mBufferSize);
            }
            outputStream.writeBytes(mLineEnd);
            outputStream.writeBytes(mTwoHyphens + boundary + mTwoHyphens + mLineEnd);
            if (null != progressUploadListener)
                progressUploadListener.onCall(JSO.s2o("{\"i\":" + 100 + "}"));
            Log.v(LOGTAG, "fileUpload ... " + (100));

            // Responses from the server (code and message)
            //int serverResponseCode = con.getResponseCode();
            //String serverResponseMessage = con.getResponseMessage();

            InputStream in = new BufferedInputStream(con.getInputStream());
            return_s = stream2string(in);

            rt = JSO.s2o(return_s);

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            return_s = ex.toString();
            Log.v(LOGTAG, "uploadError");
        }
        if (isEmptyString(rt.getChild("STS").toString())) {
            rt.setChild("STS", "KO");
            rt.setChild("s", return_s);
        }
        return rt;
    }

    //Wrap the raw webPost for cmp api call
    public static JSO apiPost(String url, JSO jo) {
        String return_s = null;
        try {
            String post_s = jo.toString();
            return_s = webPost(url, post_s);
        } catch (Exception ex) {
            return_s = ex.getMessage();
            if (isEmptyString(return_s)) {
                return_s = "" + ex.getClass().getName();
            }
            ex.printStackTrace();
        }
        try {
            if (return_s != null && return_s != "")
                return JSO.s2o(return_s);
        } catch (Exception ex) {
        }
        JSO rt = new JSO();
        rt.setChild("STS", JSO.s2o("KO"));
        rt.setChild("errmsg", JSO.s2o(return_s));

        return rt;
    }

//    public static String isoDateTime() {
//        //String time_s = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en_US"));
//        String time_s = df.format(new Date());
//        return time_s;
//    }

    //@deprecated and removed.
//    private static String readAssetInStrWithoutComments(String s) {
//        return readAssetInStrWithoutComments(getAppContext(), s);
//    }
//
//    private static String readAssetInStrWithoutComments(Context c, String urlStr) {
//        InputStream in = null;
//        try {
//            in = c.getAssets().open(urlStr);
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
//            String line = null;
//            StringBuilder sb = new StringBuilder();
//            do {
//                line = bufferedReader.readLine();
//                //TMP SOLUTION REMOVE COMMENTS OF LEADING //
//                if (line != null && !line.matches("^\\s*\\/\\/.*")) {
//                    sb.append(line);
//                }
//            } while (line != null);
//
//            bufferedReader.close();
//            in.close();
//
//            return sb.toString();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException e) {
//                }
//                //in = null;
//            }
//        }
//        return null;
//    }

    public static JSO wholeAppConfig() {
        WtfTools theWtfTool = sharedInstance();
        if (theWtfTool._jAppConfig == null) {
            final String sJsonConf = readAssetInStr("config.json", true);
            final JSO o = JSO.s2o(sJsonConf);
            theWtfTool._jAppConfig = o;
            theWtfTool._i18n = o.getChild("I18N");
        }
        return theWtfTool._jAppConfig;
    }

//    public static void setAppConfig(String K, JSO V) {
//        _jAppConfig.setChild(K, V);
//    }

    public static JSO getAppConfig(String k) {
        return wholeAppConfig().getChild(k);
    }

    public static boolean isEmptyString(String s) {
        if (s == null || "".equals(s)) return true;
        if ("null".equals(s)) return true;//tmp solution for json optString() return string "null"
        return false;
    }

    public static boolean isEmpty(Object o) {
        if (o == null) return true;
        return false;
    }

    public static String getString(Object o) {
        if (o == null) return null;
        return o.toString();
    }

    public static String optString(Object o) {
        if (o == null) return "";
        String rt = o.toString();
        if (rt == null) return "";
        return rt;
    }

    public static void appAlert(Context ctx, String msg, WtfDialogCallback clickListener) {
        AlertDialog.Builder b2;
        b2 = new AlertDialog.Builder(ctx);
        b2.setMessage(msg).setPositiveButton("Close", clickListener);
        b2.setCancelable(false);//click other place would cause cancel
        b2.create();
        b2.show();
    }

    public static void appConfirm(
            Context ctx, String msg,
            WtfDialogCallback okListener,
            WtfDialogCallback cancelListener) {
        if (null == cancelListener) {
            cancelListener = new WtfDialogCallback() {
                @Override
                public void onCall(DialogInterface dialog, int which) {
                    //dialog.cancel();
                    //Log.v(LOGTAG, ".appConfirm().click()");
                }
            };
        }
        if (null == ctx) ctx = getAppContext();
        AlertDialog.Builder b2;
        b2 = new AlertDialog.Builder(ctx);
        b2.setMessage(msg)
                .setPositiveButton("NO", cancelListener)
                .setNegativeButton("YES", okListener);
        b2.setCancelable(false);
        b2.create();
        b2.show();
    }

    public static void startJs(String name) {
        startJs(name, null, null, null);
    }

    public static void startJs(String name, String overrideParam_s, Activity caller, WtfUiCallback cb) {
        String js_str = readAssetInStr(name);
        evalJs(js_str);
    }

    public static void startUi(String name, String overrideParam_s, Activity caller) {
        startUi(name, overrideParam_s, caller, null);
    }

    //NOTES: the WtfUiCallback is for hooking events, not for close event, please NOTE !!!
    public static void startUi(String name, String overrideParam_s, Activity caller, WtfUiCallback cb) {

        JSO uia = getAppConfig(UI_MAPPING);
        if (uia == null || uia.isNull()) {
            WtfTools.quickShowMsgMain("config.json error!!!");
            //HybridTools.appAlert(getAppContext(),"config.json error !",null);
            return;
        }

        JSO defaultParam = uia.getChild(name);
        if (defaultParam == null || defaultParam.isNull()) {
            //quickShowMsg(caller.getApplication(), "config not found " + name + " !");
            quickShowMsgMain("config not found " + name + " !");
            return;
        }

        JSO overrideParam = JSO.s2o(overrideParam_s);
        JSO callParam = JSO.basicMerge(defaultParam, overrideParam);
        Log.v(LOGTAG, "param after merge=" + callParam);

        String mode = callParam.getChild("mode").toString();
        String clsName = callParam.getChild("class").toString();
        if (isEmptyString(clsName)) {
            if ("WebView".equalsIgnoreCase(mode)) {
                clsName = SimpleHybridWebViewUi.class.getName();
            } else {
                quickShowMsgMain("config.json error!!! config not found for name=" + name);
                return;
            }
        }

        //////////////////////////////////////////////
        //caller, calleeClass, uiDataJSO, cb
        Intent intent = null;
        try {
            if (caller == null) {
                intent = new Intent(getAppContext(), Class.forName(clsName));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } else
                intent = new Intent(caller, Class.forName(clsName));
        } catch (Exception ex) {
            quickShowMsgMain("not found " + clsName);
            return;
        }

        if (!isEmptyString(name)) {
            callParam.setChild("name", JSO.s2o(name));
        }

        String uiData_s = JSO.o2s(callParam);

        intent.putExtra("uiData", uiData_s);

        try {
            final Intent tmpIntent = intent;
            final Context tmpCaller = (caller == null) ? getAppContext() : caller;

            //NOTES: 跟iOS那里做法有所不同，原因是 iOS 呼唤了UI之后能马上拿到句柄，所以能操作 cb，
            // 安卓这里本来要弄到 extra 或者 放在某个能延迟堆栈的地方，暂时没时间弄，
            // 所以先弄一个类静态变量顶一下，但其实不科学有BUG的

//TODO
            WtfUi.tmpUiCallback = cb;//tmp ugly working solution, improve in future...

            tmpCaller.startActivity(tmpIntent);
        } catch (Throwable t) {
            Log.v(LOGTAG, "Throwable " + t.getMessage() + "  check manifest xml???");
            quickShowMsgMain("Error:" + t.getMessage());
        }
    }

    public static JSO findSubAuth(JSO jso, String nameOf) {
        JSO _found = null;
        Iterator it = jso.getChildKeys().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            try {
                if (Pattern.matches(key, nameOf)) {
                    _found = jso.getChild(key);
                    break;
                }
            } catch (PatternSyntaxException ex) {
                Log.v(LOGTAG, "wrong regexp=" + key);
                ex.printStackTrace();
            }
        }
        return _found;
    }

    public static boolean quickRegExpMatch(String pattern_str, String str) {
        Pattern p = Pattern.compile(pattern_str);
        Matcher m = p.matcher(str);
        if (m.matches()) return true;
        return false;
//        if (!m.matches()) return false;
//        return (m.toMatchResult().groupCount() > 0);
    }

    public static boolean copyAssetFolder(AssetManager assetManager,
                                          String fromAssetPath, String toPath) {
        try {
            Log.v(LOGTAG, "copyAssetFolder " + fromAssetPath + "=>" + toPath);
            String[] files = assetManager.list(fromAssetPath);
            new File(toPath).mkdirs();
            boolean res = true;
            for (String file : files)
                if (file.contains("."))
                    res &= copyAssetFile(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
                else
                    res &= copyAssetFolder(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyAssetFile(AssetManager assetManager, String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            Log.v(LOGTAG, "copyAsset " + fromAssetPath + "=>" + toPath);
            in = assetManager.open(fromAssetPath);
            new File(toPath).createNewFile();
            out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    //@ref http://stackoverflow.com/questions/10500775/parse-json-from-httpurlconnection-object
    private static String stream2string(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static int getStrLen(String rt_s) {
        if (rt_s == null) return -1;
        return rt_s.length();
    }

    static public String classNameOf(Object o) {
        if (o == null) return "null";
        return o.getClass().getName();
    }

    public static String getLocalWebRoot() {
        if (isEmptyString(_localWebRoot)) {
            _localWebRoot = "/android_asset/web/";
        }
        return _localWebRoot;
    }

    //@ref http://stackoverflow.com/questions/8258725/strict-mode-in-android-2-2
    //StrictMode.ThreadPolicy was introduced since API Level 9 and the default thread policy had been changed since API Level 11,
    // which in short, does not allow network operation (eg: HttpClient and HttpUrlConnection)
    // get executed on UI thread. If you do this, you get NetworkOnMainThreadException.
//    public static void uiNeedNetworkPolicyHack() {
//        int _sdk_int = android.os.Build.VERSION.SDK_INT;
//        if (_sdk_int > 8) {
//            try {
//                Log.d(LOGTAG, "setThreadPolicy for api level " + _sdk_int);
//                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                StrictMode.setThreadPolicy(policy);
//            } catch (Throwable t) {
//                t.printStackTrace();
//            }
//        }
//    }

    public static String readAssetInStr(String file_s) {
        return readAssetInStr(file_s, false);//default original
    }

//    public static int checkSelfPermission(WtfUi thisUi, String perm){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            return thisUi.getApplicationContext().checkSelfPermission(perm);
//        }
//    }

    //错误的，尽快重写 !!!!
    public static boolean checkPermission(WtfUi thisHybriUi, String perm) {
        int permissionCheck = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            permissionCheck = thisHybriUi.getApplicationContext().checkSelfPermission(perm);
            Log.v(LOGTAG, "permissionCheck(" + perm + ")=" + permissionCheck);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                //thisHybriUi.requestPermissions(new String[]{perm}, 1);
                return true;
            }
        } else {
            //抄过来用
        }
        return false;
    }

    private static File getTempDirectoryPath() {
        return getAppContext().getCacheDir();
    }

    public static String readAssetInStr(String file_s, boolean filterRowComments) {
        InputStream in = null;
        try {
            in = getAppContext().getAssets().open(file_s);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder sb = new StringBuilder();
            do {
                line = bufferedReader.readLine();
                if (filterRowComments) {
                    if (line != null && !line.matches("^\\s*\\/\\/.*")) {
                        sb.append(line + "\n");
                    }
                } else {
                    sb.append(line + "\n");
                }
            } while (line != null);

            bufferedReader.close();
            in.close();

            return sb.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
                //in = null;
            }
        }
        return null;
    }

    public static String md5(String str) {
        String rt = null;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            rt = new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return rt;
    }

    //简易全局事件机制，支持 TTL

    //TODO 跟 iOS 同步...
    private Map<String, WtfEventHandler> eventMap = new WtfCache<>();// new HashMap<>();//new WtfCache<String, WtfCallback>();

    //TODO wjc ttl => to fix the mem-leak issue
    //public void on(NSString *)eventName :(HybridEventHandler) handler :(JSO *)initData :(NSInteger)expire;//new 201806 for TTL
    public static WtfTools on(String eventName, WtfEventHandler handler, JSO extraData, int ttl) {
        //eventMap.put(eventName,)
        Log.v(LOGTAG, " on(ttl)" + ttl);
        WtfTools theWtfTool = WtfTools.sharedInstance();
        theWtfTool.eventMap.remove(eventName);
        theWtfTool.eventMap.put(eventName, handler);
        return theWtfTool;
    }

    public static WtfTools on(String eventName, WtfEventHandler handler) {
        Log.v(LOGTAG, " on()" + eventName);
        WtfTools theWtfTool = WtfTools.sharedInstance();
        theWtfTool.eventMap.remove(eventName);
        theWtfTool.eventMap.put(eventName, handler);
        return theWtfTool;
    }

    public static WtfTools off(String eventName) {
        Log.v(LOGTAG, " off()" + eventName);
        WtfTools theWtfTool = WtfTools.sharedInstance();
        theWtfTool.eventMap.remove(eventName);
        return theWtfTool;
    }

    //TODO just remove the noted cb....
    public static WtfTools off(String eventName, WtfEventHandler cb) {
        Log.v(LOGTAG, " off()" + eventName);
        WtfTools theWtfTool = WtfTools.sharedInstance();
        theWtfTool.eventMap.remove(eventName);
        return theWtfTool;
    }

    public static WtfTools trigger(String eventName, JSO extraData) {
        //TODO
        Log.v(LOGTAG, " trigger()" + eventName);
        WtfTools theWtfTool = WtfTools.sharedInstance();
        WtfEventHandler cb = theWtfTool.eventMap.get(eventName);
        if (cb != null) {
            cb.onCall(eventName, extraData);
            //return true;
        }
        //return false;
        return theWtfTool;
    }

    public static String quickRegExpReplace(String pattern_str, String str, String replacement) {
        Pattern p = Pattern.compile(pattern_str);
        Matcher m = p.matcher(str);
        return m.replaceAll(replacement);
    }

    //public native String stringFromJNI();


    public static PackageInfo getPackageInfo() {
        PackageInfo pinfo = null;
        try {
            Context _appContext = getAppContext();
            pinfo = _appContext.getPackageManager().getPackageInfo(_appContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pinfo;
    }
}
