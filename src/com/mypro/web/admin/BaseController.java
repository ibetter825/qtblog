package com.mypro.web.admin;

import org.apache.commons.lang.StringUtils;
import com.jfinal.core.Controller;
import com.mypro.context.QtAccountContext;
import com.mypro.model.ResultModel;
import com.mypro.model.front.QtAccount;
import com.mypro.model.front.QtNav;
import com.mypro.service.front.QtNavService;

/**
 * 后台请求 BaseController
 * @author ibett
 *
 */
public class BaseController extends Controller {
	protected final static String basePath = "/WEB-INF/view/front";
	protected final static String baseAdminPath = "/WEB-INF/view/admin";
	public String getCtx(){
		return this.getRequest().getContextPath();
	}
	public String getRefererURI(){
		String redirctUrl = getRequest().getHeader("referer");  
        if(StringUtils.isBlank(redirctUrl)){  
            redirctUrl = getRequest().getRequestURI();  
        }
        return redirctUrl;
	}
	/**
	 * 获取请求的全链接
	 * @return
	 */
	public StringBuffer getRequestURL(){
		return getRequest().getRequestURL();
	}
	/**
	 * 获取请求链接中的域名
	 * @return
	 */
	public String getRequestDomain(){
		StringBuffer url = this.getRequestURL();
		String uri = this.getRequestURI();
		StringBuffer s = url.delete(url.length() - uri.length(), url.length()).append("/");
		return s.toString();
	}
	/**
	 * 获取请求的action地址
	 * @return
	 */
	public String getRequestURI(){
        return getRequest().getRequestURI();  
	}
	/**
	 * 判断是否为pjax请求
	 * @return
	 */
	public boolean isPjaxRequest(){
		String header = getRequest().getHeader("X-PJAX");
		return Boolean.valueOf(header);
	}
	public void success(ResultModel model){
		setAttr("model", model);
		renderFreeMarker("/WEB-INF/view/common/result/success.html");
	}
	public void fail(ResultModel model){
		setAttr("model", model);
		renderFreeMarker("/WEB-INF/view/common/result/fail.html");
	}
	/**
	 * 跳转页面的时候，包括已选中的导航栏目，以及登录信息
	 * @param view 模板
	 * @param nos 需要设置选中状态的栏目,导航菜单的编号
	 */
	public void renderWithHeader(String view, String... nos){
		setAttr("navs", QtNav.getHeaderNavHtml(QtNavService.service.queryNavs(), nos));
		render(view);
	}
	/**
	 * jsonp调用返回
	 * @param data
	 */
	public void renderJsonp(String data){
		String callback = getPara("callback");
		renderJavascript(callback + "("+data+");");
	}
	/**
	 * 跳转到错误页面,404或500
	 */
	public void renderError(int error){
		if(error == 404)
			renderFreeMarker("/WEB-INF/view/common/error/404.html");
		else if(error == 500)
			renderFreeMarker("/WEB-INF/view/common/error/500.html");
	}
	/**
	 * 获取当前登录的用户
	 * @return
	 */
	public QtAccount getCurrentAccount(){
		return QtAccountContext.getCurrentQtAccount();
	}
}
