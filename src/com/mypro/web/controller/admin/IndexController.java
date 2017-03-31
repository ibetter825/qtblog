package com.mypro.web.controller.admin;

import java.util.List;

import com.jfinal.kit.PropKit;
import com.mypro.annotation.Controller;
import com.mypro.common.DictKeys;
import com.mypro.context.SysUserContext;
import com.mypro.model.ResultModel;
import com.mypro.model.admin.SysMenu;
import com.mypro.model.admin.SysUser;
import com.mypro.service.admin.SysMenuService;
import com.mypro.web.admin.BaseController;

@Controller(controllerKey = "/admin/index")
public class IndexController extends BaseController {
	protected static String basePath = baseAdminPath + "/index";
	private static String admin = PropKit.get(DictKeys.config_default_admin_route);
	public void index(){
		SysUser user = SysUserContext.getCurrentSysUser();
		if(user != null){
			user.remove("user_pwd", "user_salt");
			setAttr(DictKeys.current_session_user, user);
			render(basePath+"/index.html");
		}else
			redirect(admin+"/login");
	}
	/**
	 * 获取用户的菜单
	 */
	@SuppressWarnings("unchecked")
	public void menus(){
		SysUser user = SysUserContext.getCurrentSysUser();
		ResultModel model = null;
		if(user != null){
			String role_no = user.getStr("role_no");
			List<SysMenu> menus = SysMenuService.service.queryMenuList(role_no);
			model = new ResultModel();
			model.getData().put("menus", menus);
		}else
			model = new ResultModel("-1", "没有查询到菜单");
		renderJson(model);
	}
}
