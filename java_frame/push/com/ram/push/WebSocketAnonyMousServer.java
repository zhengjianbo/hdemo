package com.ram.push;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

//匿名服务
@ServerEndpoint("/anonywebsocket")
public class WebSocketAnonyMousServer {

	private static ConcurrentHashMap<String, WebSocketAnonyMousServer> webSocketMaps = new ConcurrentHashMap<String, WebSocketAnonyMousServer>();

	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;

	private String key=UUID.randomUUID().toString();//唯一UUID

	/**
	 * 连接建立成功调用的方法
	 * 
	 * @param session
	 *            可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
	 */
	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		// webSocketSet.add(this); //加入set中
		  webSocketMaps.put(key, this);
		// addOnlineCount(); // 在线数加1
		// System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose() {
		// webSocketSet.remove(this); // 从set中删除
		  webSocketMaps.remove(key);
		// subOnlineCount(); // 在线数减1
		System.out.println("有一连接关闭！当前在线人数为");
	}

	/**
	 * 收到客户端消息后调用的方法
	 * 
	 * @param message
	 *            客户端发送过来的消息
	 * @param session
	 *            可选的参数
	 * @throws IOException
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		// System.out.println("来自客户端的消息:" + message);
		// 群发消息
		// for (WebSocketServer item : webSocketSet) {
		try {
			sendMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
			// continue;
		}
		// }
	}

	/**
	 * 发生错误时调用
	 * 
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		// 移除
		  webSocketMaps.remove(key);
		System.out.println("发生错误");
		error.printStackTrace();
	}

	/**
	 * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String message) throws IOException {
		// this.session.getBasicRemote().sendText(message);
		//计算 然后进行处理
		this.session.getAsyncRemote().sendText(message);
	}

}
