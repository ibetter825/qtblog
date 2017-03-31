package com.mypro.web.controller.front;

import com.jfinal.aop.Clear;
import com.mypro.annotation.Controller;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.model.front.QtNav;
import com.mypro.service.front.QtNavService;
import com.mypro.web.admin.BaseController;

@Controller(controllerKey = "/nav")
@Clear(FrontContextInterceptor.class)
public class NavController extends BaseController {
	public void index(){
		renderHtml(QtNav.getHeaderNavHtml(QtNavService.service.queryNavs()));
	}
}
