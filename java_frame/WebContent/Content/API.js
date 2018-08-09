 function New(aClass, aParams) { 
	function new_() {
		aClass.Create.apply(this, aParams);
	}
	;
	new_.prototype = aClass;
	return new new_();
};
var API = {
	xdebug : true, 
	log : function(msg) {
		if (this.debug) {
			console.log("%o", msg + "");
		}
	},debug : function() {
		return this.xdebug;
	}

};
   
var RequestUtils = {
	timeout : 5000,
	setTimeout : function(timeout) {
		this.timeout = timeout;
		return this;
	},
	extend : function(destination, source) {
		for ( var property in source)
			destination[property] = source[property];
		return destination;
	},
	requestQueryAjax : function(url, type, data, dataext, e) {
		var odata = data;
		if (dataext != null) {
			odata = RequestUtils.extend(odata, dataext);
		}
		$.ajax({
			async : true,
			type : type,
			url : url,
			data : odata,
			timeout : this.timeout,
			success : function(data, textStatus, returnData) {
				e(1, data);
				
			},
			error : function(msg, errorStatus, errorResponse) {
				try{
					e(-1, JSON.parse(msg.responseText));
				}catch(ex){
					e(0, msg.responseText);
				}
			}
		});
	},
	requestQueryJsonp : function(url, type, data, dataext, e) {
		var odata = data;
		if (dataext != null) {
			odata = RequestUtils.extend(odata, dataext);
		}
		$.ajax({
			async : true,
			url : url,
			type : 'post',
			dataType : 'jsonp',
			jsonp : 'jsoncallback',
			data : odata,
			timeout : this.timeout,
			beforeSend : function() {
				e(-1);
			},
			success : function(json) {
				e(1, json);
			},
			complete : function(XMLHttpRequest, textStatus) {
				e(2, XMLHttpRequest, textStatus);
			},
			error : function(xhr) {
				e(0, xhr);
			}
		});
	},
};

var CookieUtil = {
	clear : function() {
	},
	get : function(key) {
		return $.cookie(key);
	},
	save : function(key, value) {
		var expiresDate = new Date();
		// 有效期1天
		expiresDate.setTime(expiresDate.getTime() + (1440 * 60 * 1000));

		$.cookie(key, value, {
			expires : expiresDate,
			path : "/",
			domain : null,
			secure : null,
			encode : true
		});
	},
	del : function(key) {
		$.cookie(key, null, {
			expire : null,
			path : "/",
			domain : null,
			secure : null,
			encode : true
		});
	},

};

var ReqUtils = {
	timeout : 5000,//默认5秒
	param : {},
	url : '',
	e : null,
	make : function(url, param, acton,erraction) {
		this.e=function(result,p1, p2, p3) {
			if (result == 0) {
				erraction(result);	
				return;
			}
			if (result == 1) {  
				acton(p1);
				return;
			}
		}
		this.url = url;
		this.param = param;
		return this;
	},
	create : function(url, param, e) {
		this.e = e;
		this.url = url;
		this.param = param;
		return this;
	},
	setTimeout : function(timeout) {
		this.timeout = timeout;
		return this;
	},
	setCallBack : function(e) {
		this.e = e;
		return this;
	},
	setRequestSource : function(url) {
		this.url = url;
		return this;
	},
	get : function() {
		//这里加入默认参数 比如  cookie refer 
		var req = RequestUtils.setTimeout(this.timeout);
		if (this.url.indexOf('http:') == 0) {
			req.requestQueryJsonp(this.url, 'post', this.param, {}, this.e);
		} else {
			req.requestQueryAjax(this.url, 'post', this.param, {}, this.e);
		}

	}
};

var Const = {
	isNotEmpty : function(obj) { 
		return !this.isEmpty(obj);
	},
	isEmpty : function(obj) {
		
		if (obj == undefined) {
			return true;
		}
		if (obj == null) {
			return true;
		}
		if ((obj + '') == '') {
			return true;
		}
		if (obj == 'undefined') {
			return true;
		}
		return false;
	},
	show : function(obj) {
		if (obj == null) {
			return "";
		}
		return obj;
	},
	find : function(array,val){
		if(Const.isEmpty(array)){
			return false;
		}
		if(array.length==0){
			return false;
		}
		var back=false;
		$.each(array,function(index,obj){
			if(Const.isEmpty(obj)){
				return true;
			}
			if(obj["STEP"]==val){
				back=true;
				return false;
			}
		});
		
		return back;
	},
}
     

var AlertBox=function(msg){ 
	var type='auto';
	 layer.open({
        type: 1
        ,offset: type  
        ,id: 'layerDemo'+type 
        ,content: '<div style="padding: 20px 100px;">'+ msg +'</div>'
        ,btn: '关闭全部'
        ,btnAlign: 'c'  
        ,shade: 0 
        ,yes: function(){
          layer.closeAll();
        }
      });
}	 

var BASE_URL = "";

var alertClass=".alert_class";  
 
var APIRest = {
	MSG:"1",
	MAIL:"2",
	WEIXIN_CODE:"3", 
	UPLOAD:"/upload/", 
	//SET_ONE_VALUE:"SET_ONE_VALUE",
	// 
	 login: BASE_URL + "admin/jmodal/VALI_LOGIN_CHECK", 
	 getLoginAjax : function() { 
	 		return this.login; 
	 },
	console: BASE_URL + "index.html", 
	 getConsole : function() { 
	 		return this.console; 
	 }, 	 
	//
	//
	//layoutitsave: BASE_URL + "/admin/jmodal/savetemplate", 
	//saveLayoutTemplate : function() { 
	//		return this.layoutitsave; 
	//}, 
	
};
