# WtfTools Specification

## WtfTools (Common)
```
+	(WtfTools) shareInstance()
+	checkAppConfig()
+	(JSO) wholeAppConfig() /* get config from config.js */
+	(JSO) getAppConfig(keyName)
+	(JSO) I18N(key)
+	jswv as JsEngineWebView
+	startUi(uiName) launch ui as defined in config.json
+	quickShowMsg() /* tool tip */
+	quickShowMsgMain() /* tool tip at global level */
+	appAlert()
+	appConfirm()
+	appPrompt()
+	KillAppSelf
+	readAssetInStr
+	(boolean)isEmptyString(s) //regard ""/null/nil as empty string.
+	saveUserConfig
+	loadUserConfig

- (void) MemorySave(key,val);
- (id) MemoryLoad(key);

TODO on/off/trigger of android
```

## WtfTools	(Android)
```
+ readAssetInStrWithoutComments
+ isoDateTime

```

## WtfTools	(iOS)
```
+	countDown
+	suspendApp
+	findTopRootView
+	fullPathOfAsset
+	+(NSString *)readAssetInStr :(NSString *)filename :(BOOL)removeComments;
+	quickRegExpMatch(regex_s, txt)
+	quickRegExpReplace(regex_s, src, tgt)
+	os_compare
+	is_simulator
+	call_c_do_m
+	base64encode
+	base64decode
+	I18N
+	(NSString) getBuildType
+ (void) notifyPause;
+ (void) notifyResume;

+(instancetype) on:(NSString*)eventName :(HybridEventHandler)handler;
+(instancetype) on:(NSString*)eventName :(HybridEventHandler)handler :(JSO *)initData;
+(instancetype) off :(NSString *)eventName :(HybridEventHandler) handler;
+(instancetype) off:(NSString*)eventName;
+(instancetype) trigger :(NSString *)eventName :(JSO *)triggerData;
+(instancetype) trigger :(NSString *)eventName;
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
