# wtf

a tiny framework.  u can check wtf-tiny-app for reference and play ground

# spec


```

JSO

config.js (see example)

WtfTools
	=>
		.wholeAppConfig()
		.getAppConfig(keyName)
		  => ._jAppConfig as JSO
		.I18N(key)
		  => ._i18n as JSO
		.jswv as JsEngineWebView
		.startUi(uiName) launch ui as defined in config.json

    .quickShowMsgMain() /* tool tip */
    .quickAlert()
    .quickPrompt()
    
    .quickRegExpMatch(regex_s, txt)
    .quickRegExpReplace(regex_s, src, tgt)

```
