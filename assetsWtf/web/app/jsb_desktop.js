/**
* 假设在 纯web 环境（即不存在JSB)，也尽量兼容，比如 open/close/print/scan/upload 等...
*/
(function(win,doc){
	if (win.jsb_desktop) {
		return;
	}
	var responseCallbacks={};
	var _tmp_win_cb=null;

	win.jsb_desktop={
		version:20161203.1
		,msgId:1
		,msgHandlerSet:{
			app_init:function(data,cb){
				try{
					$.get({url:'./app/myapp.js', success:function(s){
						cb({myapp_s:s,build_type:'T'});
					},dataType:'text'});
				}catch(ex){alert(ex);}
			}
			,app_ui_open:function(data,cb){
				if(data){
					var url=data.address || data.url || "about:blank";
					$("#divUi").show();
					$("#ifrmUi").attr("src",url);
					//window.open(url);
					_tmp_win_cb=function(responseData){
						$("#divUi").hide();
						$("#ifrmUi").attr("src","about:blank");
						cb(responseData);
					};//TMP solution
				}
			}
			,'_app_activity_open':function(data,cb){
				if(data){
					var url=data.address || data.url || "about:blank";
					$("#divUi").show();
					$("#ifrmUi").attr("src",url);
					//window.open(url);
					_tmp_win_cb=function(responseData){
						$("#divUi").hide();
						$("#ifrmUi").attr("src","about:blank");
						cb(responseData);
					};//TMP solution
				}
			}
			,app_ui_close:function(data,cb){
				//var _prev_cb=this._tmp_win_cb;
				//_prev_cb({cmd:''});
				//cb({cmd:''});
				//cb({cmd:''},'parent');
				parent.postMessage({handlerName:'iframe_close',responseData:data},location.href);
			}
			,'_app_activity_close':function(data,cb){
				//var _prev_cb=this._tmp_win_cb;
				//_prev_cb({cmd:''});
				//cb({cmd:''},'parent');
				parent.postMessage({handlerName:'iframe_close',responseData:data},location.href);
			}
			,app_cache_load:function(data,cb){
				if(data){
					var k=data.k;
					if(k){
						var v = $.jStorage.get(k);
						cb({v:v});
					}
				}
			}
			,app_cache_save:function(data,cb){
				if(data){
					var k=data.k;
					var v=data.v;
					if(k){
						$.jStorage.set(k,v);
						cb({STS:"OK"});
					}
				}
			}
		}
		,_js2app:function(msg, callbackFunc){
			var _this=this;
			var msgId=_this.msgId;
			var callbackFunc
			//msg.url=location.href;
			if (callbackFunc) {
				msgId=(msgId + 1) % 1000000;
				var callTime=new Date().getTime();
				var callbackId = 'cb_' + msgId + '_' + callTime;
				responseCallbacks[callbackId]= callbackFunc;
				msg.callbackId = callbackId;
				msg.time=callTime;
			}
			if(msg.target=='parent'){
				parent.postMessage(msg,location.href);
			}else{
				window.postMessage(msg,location.href);
			}
		}
		,_app2js:function(msg){
			var _this=this;
			var _js2app=this._js2app;
			if(msg.handlerName=='iframe_close'){
				//TMP SPECIAL...
				if('undefined'!=typeof _tmp_win_cb){
					_tmp_win_cb(msg.responseData);
				}
				return;
			}
				var callback=null;
				if (msg.responseId) {
					//this msg is a "Reply", so find the original callback
					callback = responseCallbacks[msg.responseId];
					if (null==callback) {
						alert("callback not found for "+msg.responseId);
						console.log('callback',callback,msg);
						return;
					}
					callback(msg.responseData);
					delete responseCallbacks[msg.responseId];
				} else {
					var handler = null;
					if (msg.handlerName) {
						handler = _this.msgHandlerSet[msg.handlerName];
						if(handler==null){
							console.log("jsb_desktop: not found handler name="+msg.handlerName);
						}else{
							try {
								if (msg.callbackId) {
									var callbackResponseId = msg.callbackId;
									callback = function(responseData,target) {
										_js2app({
											target:target,
											responseId: callbackResponseId,
											responseData: responseData
										});
									};
								}
								handler(msg.data, callback);
							} catch (ex) {
								console.log("jsb_desktop: WARNING: javascript handler threw.", msg, ex);
							}
						}
					}else{
						console.log("jsb_desktop: ERROR: unsupported msg from app!!!",msg);
						alert("unsupported msg from app");
					}
				}
			return {STS:"_app2js"};
		}
		,registerHandler:function(handlerName, handler) {
			this.msgHandlerSet[handlerName] = handler;
		}
		,callHandler:function(handlerName,data,cb){
			this._js2app({
				handlerName: handlerName,
				data: data
			}, function( rt ){
				if(cb){
					if (typeof(rt)=='string'){
						try{ rt=s2o(rt); } catch(ex){};
					}
					cb(rt);
				}
			});
		}
	};
})(window,document);
