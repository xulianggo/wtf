package wtf.sdk;
/**
 *
 *
 */

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;


public class WtfUi extends Activity {

    final private static String LOGTAG = new Throwable().getStackTrace()[0].getClassName();
    //TODO TMP UGLY SOLUTION...TO IMPROVE LATER !!!
    public static WtfUiCallback tmpUiCallback = null;
    private JSO _uiData;
    private JSO _responseData;
    //private
    private Map<String, WtfCallback> _cba = new HashMap<String, WtfCallback>();

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent iin = getIntent();
        String s_uiData = iin.getStringExtra("uiData");

        initUiData(JSO.s2o(s_uiData));

        Log.v(LOGTAG, "WtfUi onCreate() try push ");

        hookCallback();

        resetTopBar();
    }

    private void hookCallback() {

        //Very ugly tmp solution. but it should working well, because app is very low thread conflict
        //for the open new UI.
        if (tmpUiCallback != null) {
            try {
                tmpUiCallback.onCall(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            WtfUi.tmpUiCallback = null;
        }
    }

    public void resetTopBar() {
        //N: FullScreen + top status, Y: Have Bar + top status, M: only bar - top status, F: full screen - top status
        String topbar = WtfTools.optString(getUiData("topbar"));

        switch (topbar) {
            case "F":
                //F: full screen w- top status
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case "M":
                //M: only top bar w- top status
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    requestWindowFeature(Window.FEATURE_ACTION_BAR);
                }
                this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case "N":
                //N: FullScreen w+ top status
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                break;
            case "Y":
            default:
                //Y: top bar w+ top status (default)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    requestWindowFeature(Window.FEATURE_ACTION_BAR);
                }
                break;
        }

        try {
            //for some model of android
            ActionBar actionBar = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                actionBar = getActionBar();
            }
            //NOTES: setDisplayHomeAsUpEnabled make onOptionsItemSelected() work
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (actionBar != null) {
                    actionBar.setDisplayHomeAsUpEnabled(true);
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public void closeUi(JSO resultJSO) {
        if (null != resultJSO) setResponseData(resultJSO);
        closeUi();
    }

		//do close
		public void closeUi() {

			JSO o = _responseData;
			if (_responseData == null) {
				//if not responseData default return {name: $name, address: adress} for caller reference only
				o = new JSO();
				o.setChild("name", getUiData("name"));
				o.setChild("address", getUiData("address"));
			}
			//NOTES 有问题，trigger()不应该是同步的，所以不应该用返回值来判断是否要finish啊
			//if (false == trigger(WtfTools.WtfEventWhenClose, o)) {

			//    //if no handler from trigger, i need to close by self.
			//    finish();
			//    return true;//real closed at this call
			//}
			//return false;//didn't real close at this call
			finish();
		}

    //TODO 暂快做一对多!!!
    public void on(String eventName, WtfCallback cb) {
        Log.v(LOGTAG, "Hybrid.on( " + eventName + ")");
        _cba.remove(eventName);
        _cba.put(eventName, cb);
    }

//TODO fix it
    public void off(String eventName, WtfCallback cb){
        _cba.remove(eventName);
    }

    //TODO fix it
    public void off(String eventName){
        _cba.remove(eventName);
    }

    public boolean trigger(String eventName, JSO o) {
        WtfCallback cb = _cba.get(eventName);
        if (cb == null) {
            Log.v(LOGTAG, "trigger() found no handler for " + eventName);
            return false;//have no handler
        } else {
            cb.onCall(o);
            return true;//have handler...
        }
    }

    //NOTES: when user click the left-upper button on the top bar, then regard as closeUi...
    //@ref setDisplayHomeAsUpEnabled()
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.closeUi();
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.closeUi();
            //return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void initUiData(JSO o) {
        if (o == null) return;
        _uiData = o;
        this.setUiData("_init_time_", JSO.s2o(WtfTools.isoDateTime()));
    }

    public JSO wholeUiData() {
        return _uiData;
    }

    public void setUiData(String k, JSO v) {
        _uiData.setChild(k, v);
    }

    public JSO getUiData(String k) {
        if (null == _uiData) return new JSO();
        return _uiData.getChild(k);
    }

    public JSO getResponseData() {
        return _responseData;
    }

    public void setResponseData(JSO jso) {
        _responseData = jso;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.closeUi();
    }

}
