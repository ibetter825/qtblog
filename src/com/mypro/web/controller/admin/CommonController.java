package com.mypro.web.controller.admin;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PropKit;
import com.mypro.admin.interceptor.AdminContextInterceptor;
import com.mypro.annotation.Controller;
import com.mypro.common.DictKeys;
import com.mypro.context.SysUserContext;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.front.interceptor.FrontCurrentInterceptor;
import com.mypro.model.admin.SysOperate;
import com.mypro.model.admin.SysUser;
import com.mypro.service.admin.CommonService;
import com.mypro.service.admin.SysRoleOptService;
import com.mypro.web.admin.BaseController;
import com.mypro.web.tools.ToolStrYt;

@Controller(controllerKey = "/common")
@Clear({FrontCurrentInterceptor.class,FrontContextInterceptor.class})
public class CommonController extends BaseController {
	/**
	 * 跳转到error页面
	 */
	@Clear(AdminContextInterceptor.class)
	public void error(){
		//String message = getPara("message");
		String redirectUrl = getPara("redirectUrl");
		//setAttr("message", ToolStrYt.UrlDecode(message));
		setAttr("redirectUrl", ToolStrYt.UrlDecode(redirectUrl));
		renderFreeMarker("error/error.html");
	}
	/**
	 * 跳转到指定页面
	 */
	public void jump(){
		String menu_no = getPara("menu_no");
		String opt_no = getPara("opt_no");
		String opt_name = ToolStrYt.UrlDecode(getPara("opt_name"));
		//查询路径
		if(StringUtils.isNotEmpty(menu_no)){
			setAttr("breadcrumbs", CommonService.service.queryRoute(menu_no, opt_name));
			if(opt_no.equals("list")){
				SysUser user = SysUserContext.getCurrentSysUser();
				List<SysOperate> operates = SysRoleOptService.service.queryOptsByRoleAndMenu(user.getStr("role_no"), menu_no);  
				setAttr("operates", operates);
				setAttr("admin", PropKit.get(DictKeys.config_default_admin_route));
			}
		}
		renderFreeMarker(getPara("page"));
	}
}
