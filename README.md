# wtf
a tiny framework.  u can check wtf-tiny-app for reference and play ground

# spec
```
# JSO
# config.js 

# WtfTools
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
+	(boolean)isEmptyString(s)
+	saveUserConfig
+	loadUserConfig
+	MemorySave(k,v)
+	(JSO) MemoryLoad(k)

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


# WtfUi
```
-(void) initUi;//do init
-(void) closeUi;// trigger event close => finishUi
-(void) closeUi :(JSO*)resultJSO;
-(void) finishUi;//really finishUi
```


## NOTES TODO

android shareInstance use as iOS...
