package com.mypro.service.admin;

import java.util.ArrayList;
import java.util.List;
import com.mypro.model.admin.SysMenu;

public class CommonService {
	public final static CommonService service = new CommonService();
	/**
	 * 查询当前页面路由地址
	 * @param menu_no
	 * @param opt_name
	 * @return
	 */
	public List<String> queryRoute(String menu_no, String opt_name){
		List<SysMenu> menus = SysMenuService.service.queryMenusByMenuScort(menu_no);
		List<String> breadcrumbs = new ArrayList<String>();
		for (SysMenu m : menus) 
			breadcrumbs.add(m.getStr("menu_name"));
		breadcrumbs.add(opt_name);
		return breadcrumbs;
	}
}
