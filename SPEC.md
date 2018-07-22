# WtfTools Specification

## WtfTools (Common)
```
+	(WtfTools) shareInstance()
+	(JSO) wholeAppConfig() /* get config from config.js */
+	(JSO) getAppConfig(keyName)
+	(JSO) I18N(key) //TODO not finish yet.
+	jswv as JsEngineWebView
+	startUi(uiName) //launch ui as defined in config.json
+	quickShowMsg() /* tool tip */
+	quickShowMsgMain() /* tool tip at global level */
+	appAlert()
+	appConfirm()
+	appPrompt() // TODO
+	KillAppSelf
+	readAssetInStr
+	(boolean)isEmptyString(s) //regard ""/null/nil as empty string.
+	saveUserConfig
+	loadUserConfig
+	quickRegExpMatch(regex_s, txt)
+	quickRegExpReplace(regex_s, src, tgt)
+ md5(s)

+(instancetype) on:(NSString*)eventName :(HybridEventHandler)handler;
+(instancetype) on:(NSString*)eventName :(HybridEventHandler)handler :(JSO *)initData;
+(instancetype) off :(NSString *)eventName :(HybridEventHandler) handler;
+(instancetype) off:(NSString*)eventName;
+(instancetype) trigger :(NSString *)eventName :(JSO *)triggerData;
+(instancetype) trigger :(NSString *)eventName;

- (void) MemorySave(key,val);
- (id) MemoryLoad(key);

+webPost //TODO iOS not yet start...

- on/off/trigger //TODO timeout structure not yet done
```

## WtfTools	(iOS Special)
```
+	countDown
+	suspendApp
+	findTopRootView
+	os_compare
+	is_simulator
+	call_c_do_m
+	base64encode //TODO need port to android later
+	base64decode //TODO to port to android
+	(NSString) getBuildType //TODO android needs BuildConfig after built, not yet merge logic
+ (void) notifyPause;
+ (void) notifyResume;

```

```
# JSO
# config.js 


# WtfUi
```
-(void) initUi;//do init
-(void) closeUi;// trigger event close => finishUi
-(void) closeUi :(JSO*)resultJSO;
-(void) finishUi;//really finishUi
```

# Callback Mechanism on Full Duplex

## Protocol Specification

```
.callHandler(handlerName, callData, function(responseData){});
.registerHandler(handlerName, function(callData){});
```

### Data Structure
```
callMsg:{
	callbackId
	callTime //for housekeeping & benchmark
	callData
}

callbackMsg:{
	responseId
	responseData
}
```
