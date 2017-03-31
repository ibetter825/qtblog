package com.mypro.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfinal.handler.Handler;

/**
 * 全局Handler，设置一些通用功能
 * @author ibett
 * 描述：主要是一些全局变量的设置，再就是日志记录开始和结束操作
 */
public class GlobalHandler extends Handler {

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		/*String method = request.getMethod();
		if(!method.equalsIgnoreCase("GET")){
			//设置 web 根路径
			String cxt = ToolWeb.getContextPath(request);
			request.setAttribute(DictKeys.context_path_name, cxt);
		}*/
		
		next.handle(target, request, response, isHandled);
	}

}
