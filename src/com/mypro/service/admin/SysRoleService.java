package com.mypro.service.admin;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.admin.SysRole;
import com.mypro.web.tools.ToolStrYt;

public class SysRoleService {
	public final static SysRoleService service = new SysRoleService();
	public Page<SysRole> queryRoleList(PagerModel pager, QueryModel rq) {
		String select = "select "+SysRole.all;
		String sqlExceptSelect = " from sys_role where role_state <> -1";
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, false);
		sqlExceptSelect = (String) queryMap.get("sql");
		sqlExceptSelect = pager.getOrderSql(sqlExceptSelect);
		Object[] params = (String[]) queryMap.get("params");
		Page<SysRole> page = null;
		if(params == null)
			page = SysRole.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect);
		else
			page = SysRole.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect, params);
		return page;
	}
	/**
	 * 查询所有角色
	 * @param rq
	 * @return
	 */
	public List<SysRole> queryRoleList(QueryModel rq){
		String select = "select "+SysRole.all;
		String sqlExceptSelect = " from sys_role where role_state <> -1";
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, false);
		sqlExceptSelect = (String) queryMap.get("sql");
		Object[] params = (String[]) queryMap.get("params");
		List<SysRole> roles = null;
		if(params == null)
			roles = SysRole.dao.find(select + sqlExceptSelect);
		else
			roles = SysRole.dao.find(select + sqlExceptSelect, params);
		return roles;
	}
	public SysRole queryRoleById(String role_no) {
		return SysRole.dao.findById(role_no);
	}
	public boolean saveSysRole(SysRole role) {
		try {
			return role.save();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean updateSysRole(SysRole role){
		try {
			return role.update();
		} catch (Exception e) {
			return false;
		}
	}
	public int deleteSysRole(String[] role_nos){
		try {
			int l = Db.update("delete from sys_role where role_no in ("+ToolStrYt.joinArray(role_nos, ",", "'")+")");
			return l; 
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
