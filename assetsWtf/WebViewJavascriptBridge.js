(function(win){
	if (win.WebViewJavascriptBridge) return;
	function s2o(s){try{return(new Function('return '+s))();}catch(ex){}};
	function o2s(o){return JSON.stringify(o);};
	var responseCallbacks = {};
	var msgHandlerSet={};
	var msgId = 1;
	function _gc(){
		if(responseCallbacks){
			var nowTime=new Date().getTime();
			for(k in responseCallbacks){
				var cb=responseCallbacks[k];
				if( (nowTime-cb.time)>1800000 ){
					responseCallbacks[msg.responseId]=null;
					delete responseCallbacks[msg.responseId];
				}
			}
		}
	}
	win.WebViewJavascriptBridge = {
		//for < API 17
		nativejsb:{
			js2app:function(callbackId,handlerName,data_s){
				setTimeout(function(){
					prompt('nativejsb:',o2s([callbackId,handlerName,data_s]));
				},1);
			}
		},
		_js2app: function(msg, cb){
                 		if (cb) {
                 			msgId=(msgId + 1) % 1000000;
                 			var callTime=cb.time=(new Date()).getTime();
                 			var callbackId = msg.callbackId = 'cb_' + msgId + '_' + callTime;
                 			responseCallbacks[callbackId] = cb;
                 		}
                 		if("undefined"!=typeof nativejsb){
                 		//android-WebView or iOS-UIWebView injected
                 			return nativejsb.js2app(msg.callbackId,msg.handlerName,o2s(msg.data));
                 		}else if("undefined"!=typeof window.webkit.messageHandlers.nativejsb){
                 		//iOS-WKWebView
                 			window.webkit.messageHandlers.nativejsb.postMessage(msg);
                 		}else{
                 		    console.log("WebViewJavascriptBridge ERROR no nativejsb found")
                 		}
                 	},
		_app2js: function (msg){
                 		var _this=this;
                 		setTimeout(function(){
                 			var callback=null;
                 			if (msg.responseId) {
                 				//this msg is a "Reply", so find the original callback
                 				callback = responseCallbacks[msg.responseId];
                 				if (!callback) { return; }
                 				callback(msg.responseData);
                 				try{
                 					responseCallbacks[msg.responseId]=null;
                 					delete responseCallbacks[msg.responseId];
                 					_gc();
                 				}catch(ex){console.log(ex);}
                 			} else {
                 				var handler = null;
                 				if (msg.handlerName) {
                 					handler = msgHandlerSet[msg.handlerName];
                 					if(handler==null){
                 						console.log("WebViewJavascriptBridge: not found handler name="+msg.handlerName);
                 					}else{
                 						try {
                 							if (msg.callbackId) {
                 								var callbackResponseId = msg.callbackId;
                 								callback = function(responseData) {
                 									win.WebViewJavascriptBridge._js2app({
                 										responseId: callbackResponseId,
                 										responseData: responseData
                 									});
                 								};
                 							}
                 							handler(msg.data, callback);
                 						} catch (exception) {
                 							console.log("WebViewJavascriptBridge: WARNING: javascript handler threw.", message, exception);
                 						}
                 					}
                 				}else{
                 					console.log("WebViewJavascriptBridge ERROR: unsupported msg from app!!!",msg);
                 				}
                 			}
                 		},1);
                 		return {STS:"_app2js"};
                 	},
		//for old JSB...
		init:function(){console.log(' init() is called.')},
		registerHandler:function (handlerName, handler) { msgHandlerSet[handlerName] = handler },
		callHandler:function (handlerName, data, cb) {
                    		return win.WebViewJavascriptBridge._js2app({
                    			handlerName: handlerName,
                    			data: data
                    		}, function( rt ){
                    			if(cb){
                    				if ('string'==typeof rt){
                    					try{ rt=s2o(rt) } catch(ex){}
                    				}
                    				cb(rt);
                    			}
                    		});
                    	}
	};
})(window);