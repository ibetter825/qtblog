package com.mypro.web.controller.admin;

import java.util.List;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mypro.annotation.Controller;
import com.mypro.enums.ResultMessageEnum;
import com.mypro.model.ResultModel;
import com.mypro.model.admin.SysMenu;
import com.mypro.service.admin.SysRoleOptService;
import com.mypro.web.admin.BaseController;

@Controller(controllerKey = "/admin/roleopt")
public class RoleOptController extends BaseController {
	
	/**
	 * 查询角色树,返回JSON
	 */
	@SuppressWarnings("unchecked")
	public void power(){
		String role_no = getPara("role_no");
		List<SysMenu> menus = SysRoleOptService.service.queryPower(role_no);
		ResultModel model = new ResultModel();
		model.getData().put("power", menus);
		renderJson(model);
	}
	/**
	 * 保存分配的权限
	 * @throws Exception 
	 */
	@Before(Tx.class)
	public void savePower(){
		String opts = getPara("opts");
		String role = getPara("role");
		boolean res = SysRoleOptService.service.savePower(opts, role);
		ResultModel model =  null;
		if(res)
			model = new ResultModel();
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
}
