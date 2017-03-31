package com.mypro.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.eclipse.jetty.websocket.WebSocket;
import com.alibaba.fastjson.JSONArray;

public final class Endpoints {
	private final Queue<Endpoint> endpoints = new ConcurrentLinkedQueue<Endpoint>(); 
	/**
	 * 发送消息给某个用户
	 * @param toClientId
	 * @param data
	 */
    public void broadcast(String toClientId, String data) {
    	for (Endpoint endpoint : endpoints) {
    		if(endpoint.getClientId().equals(toClientId)){
	    		try {
					endpoint._connection.sendMessage(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
    /**
     * 发送消息给所有在线用户
     * @param data
     */
    public void broadcast(String data) {
    	for (Endpoint endpoint : endpoints) {
    		try {
				endpoint._connection.sendMessage(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}  
    }
    /**
     * 获取所有在线用户
     */
    public void onlineuser(String fromClientId) {
    	Map<String, Object> data = new HashMap<String, Object>();
    	data.put("type", "get_online_user");
    	data.put("from_client_id", fromClientId);
    	List<Map<String, String>> users = new ArrayList<Map<String,String>>();
    	Map<String, String> user = null;
    	String clientId = null, clientName = null;
    	Endpoint fromEndPoint = null;
    	for (Endpoint endpoint : endpoints) {
			clientId = endpoint.getClientId();
			clientName = endpoint.getClientName();
			if(!clientId.equals(fromClientId)){
				user = new HashMap<String, String>();
				user.put("online_user_id", clientId);
				user.put("online_user_name", clientName);
				users.add(user);
    		}else
    			fromEndPoint = endpoint;
    	}
    	data.put("online_users", users);
    	try {
			fromEndPoint._connection.sendMessage(JSONArray.toJSONString(data));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }  
    void offer(Endpoint endpoint) {  
        endpoints.offer(endpoint);  
    }  
   
    void remove(Endpoint endpoint) {  
        endpoints.remove(endpoint);  
    }  
   
    public WebSocket newEndpoint(String user_no, String real_name) {  
        return new Endpoint(this, user_no, real_name);  
    }  

}
