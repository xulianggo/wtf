window.wtfjsbridge=new (function(){
	this.browser = {
		versions: function() {
			var u = navigator.userAgent, app = navigator.appVersion; 
			return {
				android: u.indexOf('Android') > -1,
				iphone: u.indexOf('iPhone') > -1,
				ipad: u.indexOf('iPad') > -1,
				webApp: u.indexOf('Safari') == -1 
			};
		}()
	}; 
	var app_type='Desktop';
	var _browser=this.browser;
	if (_browser.versions.iphone) app_type='iOS';
	if (_browser.versions.ipad) app_type='iOS';
	if (_browser.versions.android) app_type='Android';
	this.app_type=app_type;

	if('Desktop'==this.app_type){
		window.addEventListener('message',function(evt){
			jsb_desktop._app2js(evt.data || {});
		});
	}
	this.callHandler=function(handlerName,data,cb){
		if (window.WebViewJavascriptBridge){
			window.WebViewJavascriptBridge.callHandler(handlerName,data,cb);
		}else{
			if('Desktop'==this.app_type){
				if(typeof(jsb_desktop)!='undefined'){
					jsb_desktop.callHandler(handlerName,data,cb);
				}else{
					alert("[jsb_desktop] api not ready?");
				}
			}else{
				var _this=this;
				setTimeout(function(){
					_this.callHandler(handlerName,data,cb);
				},200);
			}
		}
	};
});//wtfjsbridge
