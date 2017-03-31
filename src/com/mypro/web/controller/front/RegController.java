package com.mypro.web.controller.front;

import com.jfinal.aop.Clear;
import com.mypro.annotation.Controller;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.model.front.QtNav;
import com.mypro.service.front.QtNavService;
import com.mypro.web.admin.BaseController;
/**
 * 注册
 * @author ibett
 *
 */
@Controller(controllerKey = "/reg")
public class RegController extends BaseController {
	private final static String basePath = "/WEB-INF/view/front";
	@Clear(FrontContextInterceptor.class)
	public void index(){
			setAttr("redirectUrl", getRefererURI());
			setAttr("navs", QtNav.getHeaderNavHtml(QtNavService.service.queryNavs(), getCtx(), ""));
			setAttr("type", "reg");
			renderFreeMarker(basePath+"/login.html");
	}
}
