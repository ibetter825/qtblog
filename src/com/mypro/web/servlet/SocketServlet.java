package com.mypro.web.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import com.alibaba.fastjson.JSONArray;
import com.mypro.bean.Endpoints;
import com.mypro.common.DictKeys;
import com.mypro.web.tools.ToolWeb;

public final class SocketServlet extends  WebSocketServlet{
	private static final long serialVersionUID = 1L;
	private final Endpoints endpoints = new Endpoints();  
	private final Random random = new Random();  
    private final Thread generator = new Thread("Event generator") {  
        @Override  
        public void run() {  
            while (!Thread.currentThread().isInterrupted()) {  
                try {  
                    Thread.sleep(random.nextInt(5000));  
                    endpoints.broadcast(JSONArray.toJSONString("At " + new Date()));  
                } catch (InterruptedException e) {  
                    Thread.currentThread().interrupt();  
                }  
            }  
        }  
    };  
   
    @Override  
    public void init() throws ServletException {  
        super.init();  
        generator.start();  
    }  
   
    @Override  
    public void destroy() {  
        generator.interrupt();  
        super.destroy();  
    }  
       
    @Override  
    public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) { 
    	String real_name = request.getParameter("real_name");
    	Cookie cookie = ToolWeb.getCookieByName(request, DictKeys.current_cookie_user_admin);
    	String cookie_user_info = cookie.getValue();
		String[] cookie_values = cookie_user_info.split(":");//通过Cookie来获取当前登录的用户，并设置clientId
        return endpoints.newEndpoint(cookie_values[0], real_name);  
    }  
   
    @Override  
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)  
            throws ServletException, IOException {
        //resp.getWriter().write("11111");  
    } 
}
