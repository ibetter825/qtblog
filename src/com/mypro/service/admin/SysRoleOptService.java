package com.mypro.service.admin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import com.mypro.model.admin.SysMenu;
import com.mypro.model.admin.SysOperate;
import com.mypro.model.admin.SysRole;
import com.mypro.model.admin.SysRoleOperate;

public class SysRoleOptService {
	public final static SysRoleOptService service = new SysRoleOptService();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<SysMenu> queryPower(String role_no) {
		//先查询角色对应的所有操作
		String querySql = "SELECT res.opt_id, res.opt_no, res.menu_no, res.opt_name, res.opt_seq, res.role_no FROM ( SELECT a.opt_id, a.opt_no, a.menu_no, a.opt_name,	a.opt_seq, NULL AS role_no FROM sys_operate a LEFT JOIN sys_menu b ON b.menu_no = a.menu_no WHERE a.opt_state <> - 1 AND b.menu_state = 1 AND CONCAT(a.menu_no, '-', a.opt_no) NOT IN ( SELECT CONCAT(c.menu_no, '-', c.opt_no) menu_opt FROM 	sys_role_operate c 	WHERE c.role_no = ? ) UNION ( SELECT a.opt_id, 	a.opt_no, a.menu_no, 	a.opt_name, a.opt_seq, b.role_no 	FROM	sys_operate a	LEFT JOIN sys_role_operate b ON a.opt_no = b.opt_no AND a.menu_no = b.menu_no WHERE a.opt_state <> - 1 AND b.role_no = ?) ) res ORDER BY res.menu_no, res.opt_seq";
		List<SysOperate> opts = SysOperate.dao.find(querySql, role_no, role_no);
		
		Map<String, List> container = new HashMap(); List<Map> list; Map map = null;
		for (SysOperate o : opts) {
			map = new HashMap();
			map.put("id", o.getStr("opt_no"));
			map.put("menu", o.getStr("menu_no"));
	    	map.put("name", o.getStr("opt_name"));
	    	map.put("isLeaf", true);
	    	map.put("checked", StringUtils.isEmpty(o.getStr("role_no"))?false:true);
			if(container.containsKey(o.getStr("menu_no")))
				container.get(o.get("menu_no")).add(map);
			else{
				list = new ArrayList<Map>();
				list.add(map);
				container.put(o.getStr("menu_no"), list);
			}
		}
		
		//先查询所有的菜单与操作，以及角色对应的操作与菜单
		querySql = "select "+SysMenu.all+" from sys_menu where menu_state = 1";
		List<SysMenu> menus = SysMenu.dao.find(querySql);
		
		menus = SysRole.getPowerTree(menus, null, container);
		return menus;
	}
	public List<SysOperate> queryOptsByRoleAndMenu(String role_no, String menu_no){
		String sql = "select b.menu_no,b.opt_no,b.opt_name,b.opt_seq,b.opt_type,b.opt_url,b.opt_icon,b.opt_param,b.opt_class from sys_role_operate a left join sys_operate b on a.menu_no = b.menu_no and a.opt_no = b.opt_no where b.opt_state <> -1 and b.is_show = 1 and a.role_no = ? and a.menu_no = ? order by b.opt_seq";
		List<SysOperate> opts = SysOperate.dao.find(sql, role_no, menu_no);
		return opts;
	}
	public boolean savePower(String opts, String role_no){
		boolean res = true;
		try {
			String[] optArray = opts.split(",");
			String sql = "delete from sys_role_operate where role_no = ?";
			Db.update(sql, role_no);
			sql = "insert into sys_role_operate values(?,?,?)";
			
			for (String o : optArray) {
				if(!new SysRoleOperate().set("role_no", role_no).set("menu_no", o.split("-")[0]).set("opt_no", o.split("-")[1]).save()){
					res = false;
					break;
				}
			}
		} catch (Exception e) {
			res = false;
			try {
				DbKit.getConfig().getThreadLocalConnection().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return res;
	}
	/**
	 * 查询角色是否拥有该按钮的权限
	 * @param role_no
	 * @param menu_no
	 * @param opt_no
	 * @return
	 */
	public boolean queryAuthority(String role_no, String menu_no, String opt_no){
		String sql = "select " + SysRoleOperate.all + " from sys_role_operate where role_no = ? and menu_no = ? and opt_no = ? ";
		SysRoleOperate ro = SysRoleOperate.dao.findFirst(sql, role_no, menu_no, opt_no);
		if(ro == null)
			return false;
		else
			return true;
	}
}
