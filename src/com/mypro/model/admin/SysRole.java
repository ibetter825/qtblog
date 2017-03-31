package com.mypro.model.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "sys_role", pkName = "role_no")
public class SysRole extends BaseModel<SysRole> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final SysRole dao = new SysRole();
	public static final String all = "role_no,role_name,role_desc,role_state";
	public static final String prefix_all = "role.role_no,role.role_name,role.role_desc,role.role_state";
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getPowerTree(List<SysMenu> menus, SysMenu menu, Map<String, List> opts){
		if(menus == null)
			return null;
		List<Map> res = new ArrayList<Map>();
        Map map = null;
        String menu_fno = null, menu_no = null;
		for (SysMenu m : menus) {
			menu_fno = m.get("menu_fno","");
			menu_no = m.get("menu_no","");
			if(menu == null){
				if(StringUtils.isNotEmpty(menu_fno))//这是一级菜单
					continue;
			}else{
				if(!menu_fno.equals(menu.get("menu_no")))
					continue;
			}
			map = new HashMap();
			map.put("id", menu_no);
	    	map.put("name", m.get("menu_name"));
	    	map.put("open", true);
	    	if(opts.containsKey(menu_no))
	    		map.put("children", opts.get(menu_no));
	    	else
	    		map.put("children", getPowerTree(menus, m, opts));
        	res.add(map);
		}
		return res;
	}
}
