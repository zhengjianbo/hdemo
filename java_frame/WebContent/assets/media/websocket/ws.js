 	window.onbeforeunload = function(event) { 
	     WS.onclose =function(){};
	     WS.close();
	 }
	window.onunload = function(event) { 
	    WS.onclose =function(){};
	    WS.close();
	}
	function  initWs(wsurl){
		
		var websocket = null;
		//判断当前浏览器是否支持WebSocket
		if ('WebSocket' in window) {
			websocket = new WebSocket(wsurl);
			return websocket;
		}
		else {
		    alert('当前浏览器 Not support websocket')
		    return null;
		} 
	}
	function loadWsAction(webSocket,onOpen,onMessage,onError){
		
	    webSocket.onerror = function(event) {
	     	 onError(event);
	    }; 
	    webSocket.onopen = function(event) {
	      onOpen(event);
	    };
	 
	    webSocket.onmessage = function(event) {
	      onMessage(event);
	    }; 
	} 