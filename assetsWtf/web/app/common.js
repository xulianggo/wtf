Date.prototype.format=function(fmt){
	//YmdHis
	var d=this.getDate();
	var dd=(d<10)?('0'+d):d;
	var i=this.getMinutes();
	var ii=(i<10)?('0'+i):i;
	var h=this.getHours();
	var hh=(h<10)?('0'+h):h;
	var s=this.getSeconds();
	var ss=(s<10)?('0'+s):s;
	var m=this.getMonth()+1;//special...js :(
	var mm=(m<10)?('0'+m):mm;
	var o = {
		//"M+" : this.getMonth()+1, //月份
		"m+" : m,
		"d+" : d,
		"H+" : h, //小时
		"i+" : i, //分
		"s+" : s, //秒
		//"q+" : Math.floor((this.getMonth()+3)/3), //季度
		"S" : this.getMilliseconds() //毫秒
	};
	var week = {
		"0" : "\u65e5",
		"1" : "\u4e00",
		"2" : "\u4e8c",
		"3" : "\u4e09",
		"4" : "\u56db",
		"5" : "\u4e94",
		"6" : "\u516d"
	};
	if(/(y+)/.test(fmt)){
		fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
	}
	if(/(E+)/.test(fmt)){
		fmt=fmt.replace(RegExp.$1, ((RegExp.$1.length>1) ? (RegExp.$1.length>2 ? "\u661f\u671f" : "\u5468") : "")+week[this.getDay()+""]);
	}
	for(var k in o){
		if(new RegExp("("+ k +")").test(fmt)){
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
		}
	}
	return fmt;
};

function my_log(s){
	if(typeof(divDebug)!="undefined"){
		var ss=divDebug.innerHTML;
		divDebug.innerHTML=((new Date()).format("mmdd HHiiss") + "> "+s+"<br/>"+ss).substr(0,1024);
	}
	if(typeof(console)!="undefined"){
		console.log(s);
	}
}

function s2o(s){try{return(new Function('return '+s))();}catch(ex){}};function o2s(o){return JSON.stringify(o);};

function getQueryVar(sVar){
	return unescape(window.location.search.replace(new RegExp("^(?:.*[&\\?]" + escape(sVar).replace(/[\.\+\*]/g, "\\$&") + "(?:\\=([^&]*))?)?.*$", "i"), "$1"));
}

function main(initModule){
	//setTimeout(function(){
		window.wtfjsbridge.callHandler('app_init',{},function( rt ){
		//console.log("DBG TMP !!!!!!! app_init=>")
			window.myapp=(new Function(rt.myapp_s+';return myapp;'))();
			if(!window.myapp){
				alert('init failed');//理论上不应该看到，万一有要debug的
			}else if(!window.myapp.version){
				alert('init failed for myapp.version');//理论上不应该看到，万一有要debug的
			}else{
				myapp.main(initModule,rt);
			}
		});
	//},100);
}
