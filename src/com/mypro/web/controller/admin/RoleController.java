package com.mypro.web.controller.admin;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.plugin.ehcache.EvictInterceptor;
import com.mypro.annotation.Controller;
import com.mypro.enums.ResultMessageEnum;
import com.mypro.model.PageResultModel;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.ResultModel;
import com.mypro.model.admin.SysRole;
import com.mypro.service.admin.SysRoleService;
import com.mypro.web.admin.BaseController;

@Controller(controllerKey = "/admin/role")
public class RoleController extends BaseController {
	protected static String basePath = baseAdminPath + "/role";
	/**
	 * 默认进入角色列表
	 */
	public void index(){
		renderFreeMarker(basePath+"/role.html");
	}

	@SuppressWarnings("unchecked")
	public void form(){
		String role_no = getPara("role_no");
		ResultModel model = null;
		if(StringUtils.isNotEmpty(role_no)){
			SysRole role = SysRoleService.service.queryRoleById(role_no);
			model = new ResultModel();
			model.getData().put("dto", role);
		}else
			model = new ResultModel("角色编号不能为空！");
		renderJson(model);
	}
	/**
	 * 查询角色列表,返回JSON
	 */
	@SuppressWarnings("unchecked")
	public void list(){
		QueryModel rq = new QueryModel(this, "role");
		PagerModel pager = new PagerModel(this);
		if(pager.getPageSize() != -1){
			Page<SysRole> page = SysRoleService.service.queryRoleList(pager, rq);
			PageResultModel model = new PageResultModel();
			model.setRows(page.getList());
			model.setTotal(page.getTotalRow());
			model.setRowCount(page.getPageSize());
			model.setCurrent(pager.getCurPage());
			renderJson(model);
		}else{
			List<SysRole> roles = SysRoleService.service.queryRoleList(rq);
			ResultModel model = new ResultModel();
			model.getData().put("roles", roles);
			renderJson(model);
		}
	}
	/**
	 * 保存新角色
	 */
	public void save(){
		SysRole role = getModel(SysRole.class, "role");
		String type = getPara("opt_type");
		Boolean success = false; 
		if(type.equals("add"))
			success = SysRoleService.service.saveSysRole(role);
		else if(type.equals("modify"))
			success = SysRoleService.service.updateSysRole(role);
		ResultModel model = null;
		if(success)
			model = new ResultModel();
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	/**
	 * 删除角色
	 */
	@Before(EvictInterceptor.class)
	@CacheName("menuTree")
	public void remove(){
		String ids = getPara("ids");
		String[] role_nos = StringUtils.isEmpty(ids)?null:ids.split(",");
		int res = SysRoleService.service.deleteSysRole(role_nos);
		ResultModel model = null;
		if(res == role_nos.length)
			model = new ResultModel();
		else if(res < role_nos.length && res != 0)
			model = new ResultModel("2", "部分角色删除失败!");
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	/**
	 * 验证role_no是否已经存在
	 */
	public void vali(){
		String role_no = getPara("fieldValue");
		String id = getPara("fieldId");
		SysRole role = SysRoleService.service.queryRoleById(role_no);
		if(role != null)
			renderJson("[\""+id+"\",false]");
		else
			renderJson("[\""+id+"\",true]");
	}
}
