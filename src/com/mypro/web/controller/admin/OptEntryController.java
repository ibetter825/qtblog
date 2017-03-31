package com.mypro.web.controller.admin;

import com.jfinal.kit.PropKit;
import com.mypro.annotation.Controller;
import com.mypro.common.DictKeys;
import com.mypro.context.SysUserContext;
import com.mypro.model.admin.SysOperate;
import com.mypro.service.admin.CommonService;
import com.mypro.service.admin.SysOperateService;
import com.mypro.service.admin.SysRoleOptService;
import com.mypro.web.admin.BaseController;
import com.mypro.web.tools.ToolStrYt;

@Controller(controllerKey = "/admin/operate")
public class OptEntryController extends BaseController {
	/**
	 * 所有操作入口
	 */
	public void index(){
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
			String admin = PropKit.get(DictKeys.config_default_admin_route);
			if(redit_url.startsWith("/admin"))
				redit_url = redit_url.replaceAll("/admin", admin);
			forwardAction(redit_url);
		}else if(opt_type.equals("javascript")){
			if(route == 1)
				setAttr("breadcrumbs", CommonService.service.queryRoute(menu_no, opt_name));
			String admin = PropKit.get(DictKeys.config_default_admin_route);
			if(opt_param.startsWith("/admin"))
				opt_param = opt_param.replaceAll("/admin", admin);
			forwardAction(opt_param);
		}
		
	}
}
