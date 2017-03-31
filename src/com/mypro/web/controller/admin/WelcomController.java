package com.mypro.web.controller.admin;

import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.PropKit;
import com.mypro.admin.interceptor.AdminContextInterceptor;
import com.mypro.annotation.Controller;
import com.mypro.common.DictKeys;
import com.mypro.context.SysUserContext;
import com.mypro.model.admin.SysOperate;
import com.mypro.service.admin.CommonService;
import com.mypro.service.admin.SysOperateService;
import com.mypro.service.admin.SysRoleOptService;
import com.mypro.service.admin.SysUserService;
import com.mypro.web.admin.BaseController;
import com.mypro.web.tools.ToolStrYt;
import com.mypro.web.tools.ToolWeb;

@Controller(controllerKey = "/admin/login")
public class WelcomController extends BaseController {
	protected static String basePath = baseAdminPath + "/login";
	private static String admin = PropKit.get(DictKeys.config_default_admin_route);
	/**
	 * 登录页面 action
	 */
	@Clear(AdminContextInterceptor.class)
	public void index(){
		Integer login_info = getParaToInt(0);//dologin出错
		String msg = null;
		 if(login_info != null){
			if(login_info == DictKeys.login_info_4)//登录出错
				msg = "用户名或密码出错,请重新登录!";
			else if(login_info == DictKeys.login_info_0)
				msg = "用户不存在!";
			else
				msg = "登录出错,请重新登录!";
		}
		
		if(msg != null){
			setAttr("msg", msg);
			setAttr("status", DictKeys.msg_status_error);
		}
		String admin = PropKit.get(DictKeys.config_default_admin_route);//默认的后台根目录
		setAttr("admin", admin);
		renderFreeMarker(basePath+"/login.html");
	}
	
	/**
	 * 登录 action
	 */
	@Clear(AdminContextInterceptor.class)
	public void dologin(){
		String user_name = getPara("user_name");
		String user_pwd = getPara("user_pwd");
		int login_info = SysUserService.service.valiLogin(user_name, user_pwd, getResponse());
		if(login_info == DictKeys.login_info_3){
			redirect(admin+"/index");
		}else{
			redirect(admin+"/login/"+login_info);
		}
	}
	
	/**
	 * 登录 action
	 */
	@Clear(AdminContextInterceptor.class)
	public void logout(){
        ToolWeb.addCookie(this, null, null, true, DictKeys.current_cookie_user_admin, null, 0);
		redirect(admin+"/login");
	}
	/**
	 * 所有操作入口
	 */
	/*@ActionKey(value = "/manage/operate")
	public void operate(){
		String menu_no = getPara("mid");
		String opt_no = getPara("opt", "list");
		int route = getParaToInt("route", 0);
		SysOperate operate = SysOperateService.service.queryOperate(menu_no, opt_no);
		String role_no = SysUserContext.getCurrentSysUser().getStr("role_no");
		if(operate == null || !SysRoleOptService.service.queryAuthority(role_no, menu_no, opt_no))
			throw new RuntimeException("没有权限");
		
		String opt_type = operate.getStr("opt_type");
		String opt_name = operate.getStr("opt_name");
		String redit_url = operate.getStr("opt_url");
		String opt_param = operate.getStr("opt_param");//如果type是javascript的话，就使用forwardAction到opt_param
		if(opt_type.equals("page")){//跳转到静态页面
			if(redit_url.contains("?"))
				redit_url += "&";
			else
				redit_url += "?";
			redit_url += ("menu_no="+menu_no+"&opt_no="+opt_no+"&opt_name="+ToolStrYt.UrlEncode(opt_name));
			redirect(redit_url);
		}else if(opt_type.equals("forward") || opt_type.equals("load")){
			if(route == 1)
				setAttr("breadcrumbs", CommonService.service.queryRoute(menu_no, opt_name));
			forwardAction(redit_url);
		}else if(opt_type.equals("javascript")){
			if(route == 1)
				setAttr("breadcrumbs", CommonService.service.queryRoute(menu_no, opt_name));
			forwardAction(opt_param);
		}
		
	}*/
}
