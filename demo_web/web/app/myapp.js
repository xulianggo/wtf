var myapp={
	version:20161204.1
	,alertVersion:function(){
		var _myapp=this;
		var build_type = _myapp.build_type;
		var build_version = _myapp.build_version;
		var ServerNum = _myapp.ServerNum;
		if(build_type=='L'){
			version='Server ';
		}else if(build_type=='M'){
			version='TestFlight ';
		}else if(build_type=='D'){
			version='Dev ';
		}else if(build_type=='T'){
			version='Desktop ';
		}else{
			version='Unknown version ';
		}
		version+=build_version + ServerNum;
		//$("#span_version").html(version);	
		alert(version);
	}
	,ServerNum:""
	,ApiEntry:""
	,ApiEntryArr:{}
	,build_type:"" //(L,M,D) for JSB@MobileApp, T for SimulateJSB@Desktop Test Only
	,build_version:""
	,setServerNum:function(s){this.ServerNum=s;}
	,setApiEntry:function(s){this.ApiEntry=s;}
	,setApiEntryArr:function(a){this.ApiEntryArr=a;}
	,loadImg:function(src,u,n,callback){
		setTimeout(function(){
			var img = new Image(); //创建一个Image对象，实现图片的预下载
			img.onerror=function(e){
				//alert('onerror '+src);
			};
			img.onload = function () { //图片下载完毕时异步调用callback函数。
				//alert('onload '+u);
				if(img.width>0){
					//gif动画会循环执行onload，置空onload即可
					img.onload = img.onerror = null;
				}
				callback(u,n);
			};
			img.src = src;
		},1);
	}
	,notifyLoopCheck:function(){
		var _myapp=this;
		var _a=this.ApiEntryArr;
		var winner=0;
		for(_k in _a){
			var _v=_a[_k];
			var _t0=(new Date()).getTime();
			_myapp.loadImg(_k + "../loadergif/?"+Math.random(),_k,_v,function(u,n){
				if((++winner)==1){
					var _t2=(new Date()).getTime();
					if((_t2-_t0)<5000){
						_myapp.setServerNum(n);
						_myapp.setApiEntry(u);
					}
				}
			});
		}
	}
	,main:function(initModuleName,initConfig){
		var _myapp=this;
		if(!initConfig){
			alert('Unexpected Error: main(): initConfig empty');return;
		}
		my_log(' initModuleName = ' +initModuleName);
		var build_type=initConfig.build_type;
		if(build_type=='D'||build_type=='T'){
			if('ui_root'==initModuleName){
				//alert(' [Deveoper Hint]: Not Live Version ! (' + build_type +')');
				my_log(' [Developer Hint]: Not Live Version ! (' + build_type +')');
				$("#divDebug").toggle();
				$("#divDebugCtrl").toggle();
			}
		}
		var entry='';
		if(build_type=='L'){
			entry='http://120.55.196.46/ace_mobile/';
			_myapp.setApiEntryArr({
				'http://66-114.khgo.com/738/ace_mobile/':'d',
				'http://47.90.33.145/ace_mobile/':'a',
				'http://120.55.196.46/ace_mobile/':'b',
				'https://acev2.safe-login-center.com/ace_mobile/':'c'
			});
		}else if(build_type=='M'){
			entry='https://acedemo.sinaapp.com/ace_mobile/';
		}else if(build_type=='D'){
			entry='https://devace.sinaapp.com/ace_mobile/';
			_myapp.setApiEntryArr({
				'https://devace.sinaapp.com/ace_mobile/':'x',
				'https://1.devace.sinaapp.com/ace_mobile/':'y'
			});
		}else if(build_type=='T'){
			//桌面版本用相对路径!
			entry='../ace_mobile/';
			entry='http://120.55.196.46/ace_mobile/';
			_myapp.setApiEntryArr({
				'http://66-114.khgo.com/738/ace_mobile/':'d',
				'http://47.90.33.145/ace_mobile/':'a',
				'http://120.55.196.46/ace_mobile/':'b',
				'https://acev2.safe-login-center.com/ace_mobile/':'c'
			});
		}else{
			//alert(o2s(initConfig));
			alert('Unknown build_type = '+build_type);return;
		}
		_myapp.build_type=build_type;
		_myapp.build_version=initConfig.build_version || "";
		_myapp.setApiEntry(entry);
		if(initModuleName=='ui_home'||initModuleName=='ui_login'){
			_myapp.notifyLoopCheck();
		}
		try{
			if(_myapp[initModuleName]){
				_myapp[initModuleName]();
			}else{
				alert('Unexpected Error: initModuleName = '+initModuleName);
			}
			return;
		}catch(ex){
			alert(ex);
		}
	}
	,OpenUiHome:function(){
		var _myapp=this;
		window.wtfjsbridge.callHandler('app_ui_open',
			{name:'UiHome', topbar:'N', 'title':'Home',
				address:'ui_home.html'}
				,function( rt ){
					var f=true;
					if(rt){
						if(rt.cmd){
							var _cmd=rt.cmd;
							var _lang = rt.lang;
							if('logout'==_cmd){
								f=false;
								window['sid'] = "";
								try{_myapp.SaveCache("sid","");}catch(ex){my_log(ex);}
								//after done save, then try goto login again.
								setTimeout(function(){
									_myapp.OpenLoginUi(_lang);
								},100);
							}
						}
					}
					if(f){
						if(_myapp.build_type !='D'){
							if(window['sid']){
								_myapp.OpenUiHome();
							}else{
								_myapp.OpenLoginUi();
							}
						}
					}
				});
	}
	,OpenLoginUi:function(lang){
		var _myapp=this;
		window.wtfjsbridge.callHandler('app_ui_open',
			{name:'UiLogin', topbar:'N', 'title':'',
				address:'ui_login.html?lang='+lang}//address:this.ApiEntry+'../saas_ace/WapAce.AppLogin.api'}
				,function( o_rt ){
					var sid=o_rt.sid;
					var f=true;
					if(sid){
						f=false;
						window['sid'] = sid;
						_myapp.SaveCache("sid",sid);
						//wait a while and then open home again.
						setTimeout(function(){
							_myapp.OpenUiHome();
						},100);
					}
					if(f){
						if(_myapp.build_type!='D'){
							_myapp.OpenLoginUi();
						}
					}
				});
	}
	,OpenScanUI:function(){
		var _myapp=this;
		window.wtfjsbridge.callHandler('app_scan',
			{name:'UiScan', topbar:'Y', 'title':'Scan'}
			,function( o ){
				if(o){
					if(o.rt){
						//if(!o || !o.rt) alert(o2s(o));
						_myapp.WebOpen("Scan Pay","../saas_ace/WebFacePay.ScanQR.api","token="+o.rt);
					}else if(o.url){
						//TODO use api to open externally...
						alert(""+o.url);
						//_myapp.WebOpen(o.url,o.url,"token="+o.rt);
					}else if(o2s(o)=="{}"){
						alert("Nothing Scanned");//TODO I18N/getLang
					}else{
						alert("Scan="+o2s(o));
					}
				}
			});				
	}
	,GenQrCode:function(){
		this.WebOpen("Qr Code","../saas_ace/WebFacePay.GenQR.api");
	}
	,SaveCache:function(key,value){
		window.wtfjsbridge.callHandler('app_cache_save',
			{k:key,v:value}
			,function( rt ){
			});				
	}
	,LoadCache:function(key){
		window.wtfjsbridge.callHandler('app_cache_load',
			{k:key}
			,function( rt ){
				//alert(o2s(rt));
				window[key]=o2s(rt);
			});				
	}
	,ui_root:function(){
		var _myapp=this;
		var build_type=this.build_type;
		window.flag_deviceready=true;
		if(build_type=='L' || build_type=='M'){
			my_log('build_type='+build_type);
			_myapp.OpenLoginUi();
			return;
		}

		//下面DEBUG ONLY -----------------------------------------------------------------------------------------------------------------------------	
		try{
			$(document).on('resume',function(){
				my_log('resume');
			});
			$(document).on('pause',function(){
				my_log('pause');
			});
			$(document).on('postresume',function(){
				my_log('postresume');
			});
			$("#divDebugWrap").on('click',function(){
				$("#divDebug").toggle();
				$("#divDebugCtrl").toggle();
			});
			//my_log("deviceready " + (new Date()));
			my_log("deviceready.");
			$("#TestBuildType").on('click',function(){
				window.wtfjsbridge.callHandler('app_config_load',
					{k:'build_type'}
					,function( rt ){
						alert(o2s(rt));
					});
			});

			$("#btnCloseUi").on('click',function(){
				window.wtfjsbridge.callHandler('app_ui_close',
					null
					,function( rt ){
						alert('app_ui_close='+o2s(rt));
					});
			});

			$("#btnTestScanUi").on('click',function(){
				window.wtfjsbridge.callHandler('app_scan',
					{name:'UiScan', topbar:'Y', 'title':'扫描'}
					,function( rt ){
						alert('btnTestScanUi='+o2s(rt));
					});
			});
			$("#btnTestHomeUi").on('click',function(){
				window.wtfjsbridge.callHandler('app_ui_open',
					{name:'UiHome', topbar:'N', 'title':'Home',
						address:'ui_home.html'}
					,function( rt ){
					});
			});
			$("#btnTestLoginUi").on('click',function(){
				window.wtfjsbridge.callHandler('app_ui_open',
					{name:'UiLogin', topbar:'N', 'title':'',
						address:'ui_login.html'
					}//address:_myapp.ApiEntry+'../saas_ace/WapAce.AppLogin.api'
					,function( o_rt ){
						var sid=o_rt.sid;
						var f=true;
						if(sid){
							f=false;
							window['sid'] = sid;
							_myapp.SaveCache("sid",sid);
							setTimeout(function(){
								_myapp.OpenUiHome();
							},100);
						}
						if(f){
							if(_myapp.build_type=='L'){
								_myapp.OpenLoginUi();
							}
						}
					});
			});
			$("#btnTestSettingUi").on('click',function(){
				window.wtfjsbridge.callHandler('app_ui_open',
					{name:'UiContent', topbar:'Y', 'title':'',
						address:_myapp.ApiEntry+'../saas_ace/WapAce.AppSetting.api'}
					,function( rt ){
					});
			});
			$("#btnTestGesturesUi").on('click',function(){
				window.wtfjsbridge.callHandler('app_ui_open',
					//验证 0  创建 1  修改 2
					{name:'UiGestures', topbar:'N', 'title':'手势密码', 'lockType':'1'}
					,function( rt ){
						alert('open1='+o2s(rt));
					});
			});
			$("#TestApiPrintGetModel").on('click',function(){
				window.wtfjsbridge.callHandler('_print_get_model',
					{}
					,function( rt ){
						alert('rt='+o2s(rt));
					});
			});
			$("#TestApiPrintSetup").on('click',function(){
				window.wtfjsbridge.callHandler('_print_setup',
					{}
					,function( rt ){
						alert('rt='+o2s(rt));
					});
			});
			$("#TestApiPrintSunmi").on('click',function(){
				window.wtfjsbridge.callHandler('_print_bluetooth_innerprinter',
					{'header':''+"test header",
						'body':"test body\n test body 2",
					}
					,function( rt ){
						alert('rt='+o2s(rt));
					});
			});
			$("#TestApiPrint").on('click',function(){
				window.wtfjsbridge.callHandler('_print_bluetooth',
					{'header':''+"test header",
						'body':"test body\n test body 2",
						"qr_content":"some thing inside qr"
					}
					,function( rt ){
						alert('rt='+o2s(rt));
					});
			});
			$("#btnTestSaveCache").on('click',function(){
				window.wtfjsbridge.callHandler('app_cache_save',
					{k:'sid',v:'lkasdjfldsjfsdlf'}
					,function( rt ){
					});
			});
			$("#btnTestLoadCache").on('click',function(){
				window.wtfjsbridge.callHandler('app_cache_load',
					{k:'sid'}
					,function( rt ){
						alert(o2s(rt));
					});
			});
			$("#btnPhotoTake").on('click',function(){
				window.wtfjsbridge.callHandler('app_photo',{from:'camera'},function( rt ){
					var bin=rt.bin;
					if(bin){
						$("#imgThumb").attr("src","data:image/jpg;base64,"+bin);
						alert('size='+rt.size);
					}else{
						alert(o2s(rt));
					}
				});
			});
			$("#btnDebug").on('click',function(){
				alert(location.href);
			});
			$("#btnPhotoSelect").on('click',function(){
				window.wtfjsbridge.callHandler('app_photo',{from:'album'},function( rt ){
					var bin=rt.bin;
					if(bin){
						//<img src="data:image/jpg;base64,XXXXXXXX"/>
						$("#imgThumb").attr("src","data:image/jpg;base64,"+bin);
						alert('size='+rt.size);
					}else{
						alert(o2s(rt));
					}
				});
			});
			$("#btnPhotoUpload").on('click',function(){
				window.wtfjsbridge.callHandler('app_photo',{upload:_myapp.ApiEntry+'../upload/ApiUpload.UploadImage.api'},function( rt ){
					alert(o2s(rt));
				});
			});
		}catch(ex){
			alert("deviceready.ex="+ex);
		}
	}
	//下面是UIhome的函数-----------------------------------------------------------------------------------------------------------------------------	
	,ui_home:function(){
		var _myapp=this;
		$(document).on('resume',function(){
			_myapp.notifyLoopCheck();
		});
		if(!window['sid']){
			window.wtfjsbridge.callHandler('app_cache_load',{k:'sid'},function( rt ){
				window['sid']=rt.v;_myapp.InitHomePage();
			});
		}
	}
	,InitHomePage:function(){
		if(!window['sid']){
			alert('Session Timeout');
			setTimeout(function(){
				window.wtfjsbridge.callHandler("_app_activity_close", {}, function( raw_api_result ){});
			},111);
			return;
		}
		this.ShowBanner();
		this.InitMenu();
		this.InitUserInfo();
	}
	,ShowBanner:function(){	 
		var curIndex = 0; 
		var timeInterval = 3000; 
		var arr = new Array(); 
		arr[0] = "V1/icon/Service.jpg";
		arr[1] = "V1/icon/2000R.jpg"; 
		arr[2] = "V1/icon/Hotline.jpg"; 
		setInterval(function() { 
			var obj = document.getElementById("img_banner"); 
			if(!obj) return false;
			if (curIndex == arr.length-1) { 
				curIndex = 0; 
			} else { 
				curIndex += 1; 
			} 
			obj.src = arr[curIndex]; 
		} ,timeInterval); 
	}
	,InitUserInfo:function(){
		var url = this.ApiEntry+'A.V2UserInfo.api?_s='+window['sid'];
		$.ajax({
			url: url,
			dataType: 'jsonp',
			jsonp:'callback',  
			success: function(rt){
				$('#user_login').html(rt.user.user_login);
				$('#user_oper_phone').html(rt.user.user_oper_phone);
				$('#user_role').html(rt.user.user_role);
				$('#StrLogout').html(rt.user.StrLogout);		
				$('#subTitle').html(rt.user.user_role+'('+rt.user.user_oper_phone+')');
			}
		});
	}
	,InitMenu:function(){
		var _myapp=this;
		var url = this.ApiEntry+'A.V2Menu.api?_s='+window['sid'];
		$.ajax({
			url: url,
			dataType: 'jsonp',
			jsonp:'callback',  
			success: function(rt){//console.log(rt);return;
				if(!rt.menu || rt.menu.length<1){
					alert('Session Timeout');
					window.wtfjsbridge.callHandler("_app_activity_close", {}, function( raw_api_result ){});
					return;
				}
				_myapp.InitBottomMenu(rt.menu);
				_myapp.InitContentMenu(rt.menu);
				_myapp.InitSettingMenu(rt.setting_menu);
			}
		});
	}
	,InitBottomMenu:function(o){
		if(o.length<1) return;
		var str_width = (o.length==3)?"33%":"25%";
		var str_html='';
		for(var i=0;i<o.length;i++){
			str_html+='<li style="width:'+str_width+'"><a href="javascript:;"><img src="'+o[i].iconUrl+'"><p>'+o[i].menuName+'</p></a></li>';
		}
		$('#bottomUL').html(str_html);
		if(o[0].needScan==1){
			$('#scan_banner').show();
			$('#img_banner').hide();
		}
		$('#bottomUL li:first').addClass('current');
		$('#bottomUL li:first img').prop('src',o[0].iconUrl2);
		$('#bottomUL li').on('click',function(){
			var index = $(this).index();//console.log(o[index]);
			$('#bottomUL li').removeClass('current');
			for(var i=0;i<o.length;i++){
				$('#bottomUL li').eq(i).find("img:eq(0)").prop('src',o[i].iconUrl);
			}
			$(this).addClass('current');
			$(this).find("img:eq(0)").prop('src',o[index].iconUrl2);
			if(o[index].needScan==1){
				$('#scan_banner').show();
				$('#img_banner').hide();
			}else{
				$('#scan_banner').hide();
				$('#img_banner').show();
			}
			$('#maincontent .row').css('display','none');
			$('#maincontent .row').eq(index).css('display','block');
		});
	}
	,InitContentMenu:function(o){
		var str_html = '';
		for(var i=0;i<o.length;i++){
			if(i>0){ str_html += '<div class="row" style="display:none">';}else{str_html += '<div class="row">';}
			if(o[i].subMenu){
				for(var j=0;j<o[i].subMenu.length;j++){
					if(!o[i].subMenu[j].iconUrl) continue;
					str_html +='<div class="col-lg-4 col-xs-4" onClick="myapp.WebOpen(\''+o[i].subMenu[j].name+'\',\''+o[i].subMenu[j].linkUrl+'\');">\n\
						<div class="small-box bg-white">\n\
						<div class="inner">\n\
						<h3><img src="'+o[i].subMenu[j].iconUrl+'" /></h3>\n\
						<p color="#000000">'+o[i].subMenu[j].name+'</p>\n\
						</div></div></div>';

				}
			}
			str_html += '</div>';
		}
		$('#maincontent').html(str_html);
	}
	,InitSettingMenu:function(o){
		var str_html = '';
		if(o.length<1) return;
		for(var i=0;i<o.length;i++){
			var str_icon = o[i].iconUrl?'<img src="'+o[i].iconUrl+'" />':'<i class="fa fa-link"></i>';
			if(i==0){
				str_html += '<li onClick="myapp.WebOpenLang(\''+o[i].name+'\',\''+o[i].linkUrl+'\');"><a>'+str_icon+'<span> &nbsp;'+o[i].name+'</span></a></li>';
			}else if(i==3){
				str_html += '<li onClick="myapp.PrintSetup();"><a>'+str_icon+'<span> &nbsp;'+o[i].name+'</span></a></li>';
			}else{
				str_html += '<li onClick="myapp.WebOpen(\''+o[i].name+'\',\''+o[i].linkUrl+'\');"><a>'+str_icon+'<span> &nbsp;'+o[i].name+'</span></a></li>';
			}
		}
		$('#ul_setting').html(str_html);
	}
	,WebOpen:function(title,url,param){
		url = this.ApiEntry+url;
		url += "?_s="+window['sid'];
		var build_type = this.build_type;
		if(build_type!='T') url += "&device=1";
		if(param) url += "&"+param;
		//alert(url);
		var _myapp = this;
		window.wtfjsbridge.callHandler('_app_activity_open',
			{name:'UiContent', topbar:'Y', 'title':title,address:url}
			,function( rt ){
				//alert('rt='+o2s(rt));				
				if(rt && rt.cmd && 'logout'==rt.cmd)  _myapp.Logout();
			});
	}
	,WebOpenLang:function(title,url,param){
		url = this.ApiEntry+url;
		url += "?_s="+window['sid'];
		var build_type = this.build_type;
		if(build_type!='T') url += "&device=1";
		if(param) url += "&"+param;
		var _myapp = this;
		window.wtfjsbridge.callHandler('_app_activity_open',
			{name:'UiContent', topbar:'Y', 'title':title,address:url}
			,function( rt ){
				if(rt && rt.cmd && 'logout'==rt.cmd)  _myapp.Logout();
				//todo要远程判断是否更新过lang，更新了才reload
				window.location.reload();
			});
	}
	,PrintSetup:function(){	
		window.wtfjsbridge.callHandler('_print_setup',
			{}
			,function( rt ){
				//alert('rt='+o2s(rt));
			});
	}
	,Logout:function(){
		//logout暂时不要confirm
		//		 var msg = "Confirm Logout?"; 
		//		 if (confirm(msg)!=true){ 
			//			return false; 
			//		 } 

			var url = this.ApiEntry+'A.V2Logout.api?_s='+window['sid'];
			window['sid'] = "";
			$.ajax({
				url: url,
				dataType: 'jsonp',
				jsonp:'callback',  
				success: function(rt){//alert(o2s(rt))//console.log(rt);return;
					window.wtfjsbridge.callHandler("_app_activity_close", {cmd:'logout',lang:rt.lang}, null);
				}
			});
	}
	//下面是ui_login的函数-----------------------------------------------------------------------------------------------------------------------------	
	,ui_login:function(){
		var _myapp=this;
		setTimeout(function(){
			var build_type = _myapp.build_type;
			var build_version = _myapp.build_version;
			var ServerNum = _myapp.ServerNum;
			if(build_type=='L'){
				version='Server ';
			}else if(build_type=='M'){
				version='TestFlight ';
			}else if(build_type=='D'){
				version='Dev ';
			}else if(build_type=='T'){
				version='Desktop ';
			}else{
				version='Unknown version ';
			}
			version+=build_version + ServerNum;
			$("#span_version").html(version);	
		},666);
		var _lang = getQueryVar('lang');//alert(_lang);//alert(window.location);			
		if(typeof(_lang) != "undefined" && _lang!="undefined" && _lang && _lang!=""){
		}else{
			_lang = (navigator.browserLanguage || navigator.language).toLowerCase();
			//if('en-us'==_lang) _lang = 'en';
		}
		if('zh-cn'==_lang){
		}else if('zh-tw'==_lang){
		}else if('kh'==_lang){
		}else{
			_lang = 'en';
		}
		$('#login_langSelect').val(_lang);//alert($('#login_langSelect').val());
		if(''==$('#login_langSelect').val()) $('#login_langSelect').val('en');
		_myapp.InitSessionID();												 
	}
	,InitSessionID:function(){
		var _myapp=this;
		var url = this.ApiEntry+'A.V2GetSid.api';
		$.ajax({
			url: url,
			dataType: 'jsonp',
			jsonp:'callback',  
			success: function(rt){//alert(rt);
				window['sid']=rt.sid;
				_myapp.InitLoginPage();
			}
		});

	}
	,InitLoginPage:function(){//alert(window['sid']);
		lang = $('#login_langSelect').val();
		$("#account_login").html(ArrLang['account_login'][lang]);
		$("#phone_login").html(ArrLang['phone_login'][lang]);
		$("#login_input").attr('placeholder',ArrLang['Account'][lang]);
		$("#password_input").attr('placeholder',ArrLang['Password'][lang]);
		$("#regMB").val(ArrLang['Register'][lang]+'(MB)');
		$("#regAE").val(ArrLang['Register'][lang]+'(AE)');
		$("#sub_login").val(ArrLang['Login'][lang]);
	}
	,ChangeLoginType:function(){
		var zt = document.getElementsByName("login_type");
		for(var i=0;i<zt.length;i++){
			if(zt[i].checked && zt[i].value == "LoginByPhoneNum"){
				//document.getElementById('login_phone_num').style.width="155px";
				document.getElementById("login_By_Name").style.display="none";
				document.getElementById("login_By_Num").style.display="block";
			}else{
				//document.getElementById("login_By_Name").style.width="300px";
				document.getElementById("login_input").style.width="100%";
				document.getElementById("login_By_Name").style.display="block";
				document.getElementById("login_By_Num").style.display="none";
			}
		}
	}
	,JoinMB:function(){
		this.WebOpen('JoinMB','../saas_ace/WapAce.JoinMB.api','lang='+$('#login_langSelect').val());
	}
	,JoinAE:function(){
		this.WebOpen('JoinAE','../saas_ace/WapAce.JoinAE.api','lang='+$('#login_langSelect').val());
	}
	,ForgetPassword:function(){
		this.WebOpen('Forget Password','../saas_ace/WapAce.ForgetPassword.api','lang='+$('#login_langSelect').val());
	}
	,login_bin:function(){
		var _myapp=this;
		var login = document.getElementById("login_input").value;
		var login2 = document.getElementById("login_phone_num").value;
		var password = document.getElementById("password_input").value;
		if ((login == "" && login2 == "") || password == "") {
			lang = $('#login_langSelect').val();
			alert(ArrLang['logintip'][lang]);
			return false;
		}
		var login_type = document.getElementsByName("login_type");
		for(var i=0;i<login_type.length;i++){
			if(login_type[i].checked){
				var login_type_val = login_type[i].value;
				break;
			}
		}
		var country_code = document.getElementById("login_phone_country_code").value;
		var lang = $('#login_langSelect').val();
		//var AsAgency = document.getElementById("AsAgency").value;
		var _data = {"login_input":login,"login_phone_num":login2,"login_phone_country_code":country_code,"password_input":password,"lang":lang,"login_type":login_type_val}
		document.getElementById('loading7').style.visibility='visible';
		$.ajax({
			type: "POST",
			dataType: 'jsonp',
			jsonp:'callback',  
			url: _myapp.ApiEntry+"A.V2Login.api?_s="+window['sid'],
			data:_data,
			success: _myapp.WapCallback
		});
	}
	,WapCallback:function(o){
		document.getElementById('loading7').style.visibility='hidden';
		if(o.STS == "OK"){
			if(o.warning) alert(o.warning);
			window.wtfjsbridge.callHandler("_app_activity_close", {sid:o._s}, function( raw_api_result ){});
		}else{
			var errmsg = o.errmsg?o.errmsg:s;
			alert(errmsg);
		}
		return false;
	}

};

