package com.mypro.service.admin;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.ICallback;
import com.jfinal.plugin.activerecord.Page;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.admin.SysMenu;
import com.mypro.web.tools.ToolStrYt;

public class SysMenuService {
	public final static SysMenuService service = new SysMenuService();
	/**
	 * 根据条件获取所有的菜单
	 * @return
	 */
	public Page<SysMenu> queryMenuList(PagerModel pager, QueryModel rq) {
		String select = "select "+SysMenu.all;
		String sqlExceptSelect = " from sys_menu where menu_state <> -1";
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, false);
		sqlExceptSelect = (String) queryMap.get("sql");
		sqlExceptSelect = pager.getOrderSql(sqlExceptSelect);
		Object[] params = (String[]) queryMap.get("params");
		Page<SysMenu> page = null;
		if(params == null)
			page = SysMenu.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect);
		else
			page = SysMenu.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect, params);
		return page;
	}
	/**
	 * 查询menu tree
	 * @param rq
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysMenu> queryMenuList(QueryModel rq){
		String querySql = "select "+SysMenu.all+" from sys_menu where menu_state <> -1";
		Map<String, Object> queryMap = rq.getQuerySql(querySql, false);
		List<SysMenu> menus = null;
		if(queryMap.get("params") == null)
			menus = SysMenu.dao.find(querySql);
		else
			menus = SysMenu.dao.find(querySql, (Object[]) queryMap.get("params"));
		if(menus != null)
			menus = SysMenu.getMenuTree(menus, null);
		return menus;
	}
	/**
	 * 根据角色编号获取菜单列表,调用存储过程
	 * @param role_no
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SysMenu> queryMenuList(final String role_no){
		List<SysMenu> menus = (List<SysMenu>) Db.execute(new ICallback() {
			public ResultSet rs=null; 
            CallableStatement proc = null; 
            List<SysMenu> menus = null;
			@Override
			public Object call(Connection con) throws SQLException {
				proc=con.prepareCall("{call pro_role_menus(?)}"); 
                proc.setString(1, role_no); 
                proc.execute(); 
                rs=(ResultSet)proc.getResultSet(); 
                SysMenu menu = null;
        		try {
        			while (rs.next()) {
        				menu = new SysMenu();
        				menu.set("menu_no", rs.getString("menu_no"));
        				menu.set("menu_fno", rs.getString("menu_fno"));
        				menu.set("menu_name", rs.getString("menu_name"));
        				menu.set("menu_desc", rs.getString("menu_desc"));
        				menu.set("menu_seq", rs.getInt("menu_seq"));
        				menu.set("menu_state", rs.getInt("menu_state"));
        				menu.set("menu_level", rs.getInt("menu_level"));
        				menu.set("menu_icon", rs.getString("menu_icon"));
        				menu.set("menu_scort", rs.getString("menu_scort"));
        				if(menus == null)
        					menus = new ArrayList<SysMenu>();
        				menus.add(menu);
        			}
        		} catch (SQLException e) {
        			e.printStackTrace();
        			return null;
        		}
				return menus;
			}
		});
		if(menus != null)
			menus = SysMenu.getMenuTree(menus, null);
		return menus;
	}
	/**
	 * 根据主键查询菜单
	 * @param menu_no
	 * @return
	 */
	public SysMenu queryMenuById(String menu_no) {
		return SysMenu.dao.findById(menu_no);
	}
	/**
	 * 根据menu_no数组查询菜单集合
	 * @param menu_nos
	 * @return
	 */
	public List<SysMenu> queryMenusByIds(String[] menu_nos) {
		return SysMenu.dao.find("select "+SysMenu.all+" from sys_menu where menu_no in (" + ToolStrYt.joinArray(menu_nos, ",", "'") + ") order by menu_level");
	}
	/**
	 * 根据menu_no查询出的menu_scort查询其上级菜单
	 * @param menu_no
	 * @return
	 */
	public List<SysMenu> queryMenusByMenuScort(String menu_no){
		return SysMenu.dao.find("select "+SysMenu.simple+" from sys_menu a where INSTR((select menu_scort from sys_menu where menu_no = ?),a.menu_no) order by menu_level", menu_no);
	}
	/**
	 * 保存系统菜单
	 * @param menu
	 * @return
	 */
	public boolean saveSysMenu(SysMenu menu) {
		try {
			String menu_fno = menu.getStr("menu_fno");
			if(StringUtils.isEmpty(menu_fno))//一级菜单
				menu.set("menu_scort", menu.getStr("menu_no"));
			else{
				SysMenu fmenu = queryMenuById(menu.getStr("menu_fno"));
				if(fmenu == null)
					return false;
				menu.set("menu_level", fmenu.getInt("menu_level")+1);
				menu.set("menu_scort", fmenu.getStr("menu_scort")+","+menu.getStr("menu_no"));
			}
			return menu.save();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 修改系统菜单
	 * @param menu
	 * @return
	 */
	public boolean updateSysMenu(SysMenu menu){
		try {
			return menu.update();
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 删除菜单
	 * @param menu_nos
	 * @return
	 */
	public int deleteSysMenu(String[] menu_nos){
		try {
			int l = Db.update("delete from sys_menu where menu_no in ("+ToolStrYt.joinArray(menu_nos, ",", "'")+")");
			return l; 
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
