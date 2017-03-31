package com.mypro.model.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "sys_menu", pkName = "menu_no")
public class SysMenu extends BaseModel<SysMenu> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final SysMenu dao = new SysMenu();
	public static final String all = "menu_no,menu_fno,menu_name,menu_desc,menu_seq,menu_state,menu_level,menu_icon,menu_param,menu_scort";
	public static final String simple = "menu_no,menu_fno,menu_name,menu_scort";
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getMenuTree(List<SysMenu> menus, SysMenu menu){
		if(menus == null)
			return null;
		List<Map> res = new ArrayList<Map>();
        Map map = null;
		for (SysMenu m : menus) {
			if(menu == null){
				if(StringUtils.isNotEmpty((String) m.get("menu_fno")))//这是一级菜单
					continue;
			}else{
				if(!m.get("menu_fno","").equals(menu.get("menu_no")))
					continue;
			}
			map = new HashMap();
			map.put("id", m.get("menu_no"));
	    	map.put("name", m.get("menu_name"));
	    	map.put("icon", m.get("menu_icon"));
	    	map.put("level", m.get("menu_level"));
	    	map.put("open", true);
        	map.put("children", getMenuTree(menus, m));
        	res.add(map);
		}
		return res;
	}
}
