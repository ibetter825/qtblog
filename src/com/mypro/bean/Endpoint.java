package com.mypro.bean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.websocket.WebSocket;
import com.alibaba.fastjson.JSONArray;
import com.mypro.common.tools.DateUtils;

public class Endpoint implements WebSocket.OnTextMessage {
	private static String to_one = "one";//发给某个人
	private static String to_all = "all";//发给所有人
	private static String to_group = "group";//发给讨论组
	protected Connection _connection;
    private Endpoints endpoints;  
    private String clientId;//客户端编号，user_no
    private String clientName;//客户端使用者姓名，real_name
    
    public Endpoint(Endpoints endpoints, String user_no, String real_name) {
        this.setEndpoints(endpoints);
        this.setClientId(user_no);
        this.setClientName(real_name);
    }
       
    @Override  
    public void onClose(int code, String message) {
        System.out.println("Client disconnected"); 
        this.endpoints.remove(this);
        Map<String, String> data = new HashMap<String, String>();
        data.put("type", "user_leave");
        data.put("target", to_all);
        data.put("from_client_id", clientId);
        data.put("from_client_name", clientName);
    	//this._connection.sendMessage(JSONArray.toJSONString("ClientID = " + clientId));
    	this.endpoints.broadcast(JSONArray.toJSONString(data));
    }
   
    @Override  
    public void onOpen(Connection connection) {
        System.out.println("Client connected");
        _connection = connection;
        /*try {*/
        	System.err.println(clientId+"-------------");
            //this._connection.sendMessage(JSONArray.toJSONString("ClientID = " + clientId));
        	//发送用户上线的信息
        	Map<String, String> message = new HashMap<String, String>();
        	message.put("type", "user_join");
        	message.put("target", to_all);
        	message.put("from_client_id", clientId);
        	message.put("from_client_name", clientName);
        	//this._connection.sendMessage(JSONArray.toJSONString("ClientID = " + clientId));
        	this.endpoints.broadcast(JSONArray.toJSONString(message));
        /*} catch (IOException e) {
            e.printStackTrace();
        }*/
        endpoints.offer(this);
    }  
   
    @SuppressWarnings("unchecked")
	@Override
    public void onMessage(final String data) {
        System.out.println("Received data: " + data);
        Map<String, String> receive = JSONArray.parseObject(data, HashMap.class);
        String fromClientId = (String) receive.get("from_client_id");
        if(receive.get("type").equals("message")){
        	receive.put("time", DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
        	String message = JSONArray.toJSONString(receive);
        	if(receive.get("target").equals(to_one)){//发送给指定的用户
        		String toClientId = (String) receive.get("to_client_id");
        		try {
					this._connection.sendMessage(message);//将发送时间再发送给发送者
					this.endpoints.broadcast(toClientId, message);//发送给指定对象
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}else if(receive.get("target").equals(to_all))//发送给所有用户
        		this.endpoints.broadcast(message);
        }else if(receive.get("type").equals("get_online_user")){//获取在线用户
        	this.endpoints.onlineuser(fromClientId);
        }else
        	this.endpoints.broadcast(data); //发送给所有用户
    }
   
    public Endpoints getEndpoints() {
        return endpoints;
    }
   
    public void setEndpoints(Endpoints endpoints) {
        this.endpoints = endpoints;
    }

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

}
