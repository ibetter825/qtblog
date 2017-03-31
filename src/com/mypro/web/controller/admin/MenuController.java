package com.mypro.web.controller.admin;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.plugin.ehcache.EvictInterceptor;
import com.mypro.annotation.Controller;
import com.mypro.enums.ResultMessageEnum;
import com.mypro.model.PageResultModel;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.ResultModel;
import com.mypro.model.admin.SysMenu;
import com.mypro.service.admin.SysMenuService;
import com.mypro.web.admin.BaseController;

@Controller(controllerKey = "/admin/menu")
public class MenuController extends BaseController {
	protected static String basePath = baseAdminPath + "/menu";
	/**
	 * 默认进入菜单列表
	 */
	public void index(){
		renderFreeMarker(basePath+"/menu.html");
	}
	/**
	 * 进入到菜单编辑或添加的form表单
	 */
	@SuppressWarnings("unchecked")
	public void form(){
		String menu_no = getPara("menu_no");
		ResultModel model = null;
		if(StringUtils.isNotEmpty(menu_no)){
			SysMenu menu = SysMenuService.service.queryMenuById(menu_no);
			model = new ResultModel();
			model.getData().put("menu", menu);
		}else
			model = new ResultModel("菜单编号不能为空！");
		renderJson(model);
	}
	/**
	 * 查询菜单列表,返回JSON
	 */
	public void list(){
		QueryModel rq = new QueryModel(this, "menu");
		PagerModel pager = new PagerModel(this);
		Page<SysMenu> page = SysMenuService.service.queryMenuList(pager, rq);
		PageResultModel model = new PageResultModel();
		model.setRows(page.getList());
		model.setTotal(page.getTotalRow());
		model.setRowCount(page.getPageSize());
		model.setCurrent(pager.getCurPage());
		renderJson(model);
	}
	/**
	 * 查询菜单树,返回JSON
	 */
	@SuppressWarnings("unchecked")
	@Before(CacheInterceptor.class)
	@CacheName("menuTree")
	public void tree(){
		List<SysMenu> menus = SysMenuService.service.queryMenuList(new QueryModel());
		ResultModel model = new ResultModel();
		model.getData().put("menus", menus);
		renderJson(model);
	}
	/**
	 * 保存新菜单
	 */
	@Before(EvictInterceptor.class)
	@CacheName("menuTree")
	public void save(){
		SysMenu menu = getModel(SysMenu.class, "menu");
		String type = getPara("opt_type");
		Boolean success = false; 
		if(type.equals("add"))
			success = SysMenuService.service.saveSysMenu(menu);
		else if(type.equals("modify"))
			success = SysMenuService.service.updateSysMenu(menu);
		ResultModel model = null;
		if(success)
			model = new ResultModel();
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	/**
	 * 删除菜单
	 */
	@Before(EvictInterceptor.class)
	@CacheName("menuTree")
	public void remove(){
		String ids = getPara("ids");
		String[] menu_nos = StringUtils.isEmpty(ids)?null:ids.split(",");
		int res = SysMenuService.service.deleteSysMenu(menu_nos);
		ResultModel model = null;
		if(res == menu_nos.length)
			model = new ResultModel();
		else if(res < menu_nos.length && res != 0)
			model = new ResultModel("2", "部分菜单删除失败!");
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	/**
	 * 验证menu_no是否已经存在
	 */
	public void vali(){
		String menu_no = getPara("fieldValue");
		String id = getPara("fieldId");
		SysMenu menu = SysMenuService.service.queryMenuById(menu_no);
		if(menu != null)
			renderJson("[\""+id+"\",false]");
		else
			renderJson("[\""+id+"\",true]");
	}
}
