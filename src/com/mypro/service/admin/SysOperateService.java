package com.mypro.service.admin;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.admin.SysOperate;
import com.mypro.web.tools.ToolStrYt;

public class SysOperateService {
	public final static SysOperateService service = new SysOperateService();
	/**
	 * 根据主键，获取操作
	 * @return
	 */
	public SysOperate queryOperate(String menu_no, String opt_no) {
		SysOperate operate = SysOperate.dao.findFirst("select "+SysOperate.allwithmenu+" from sys_operate a left join sys_menu b on a.menu_no = b.menu_no where a.menu_no = ? and a.opt_no = ?", menu_no, opt_no);
		return operate;
	}
	public Page<SysOperate> queryOperateList(PagerModel pager, QueryModel rq) {
		String select = "select "+SysOperate.all;
		String sqlExceptSelect = " from sys_operate where opt_state <> -1";
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, false);
		sqlExceptSelect = (String) queryMap.get("sql");
		sqlExceptSelect = pager.getOrderSql(sqlExceptSelect);
		Object[] params = (String[]) queryMap.get("params");
		Page<SysOperate> page = null;
		if(params == null)
			page = SysOperate.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect);
		else
			page = SysOperate.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect, params);
		return page;
	}
	/**
	 * 查询操作列表
	 * @param rq
	 * @return
	 */
	public List<SysOperate> queryOperateList(QueryModel rq) {
		String select = "select " + SysOperate.all + " from sys_operate where opt_state <> -1";
		Map<String, Object> queryMap = rq.getQuerySql(select, false);
		String querySql = (String) queryMap.get("sql") + " order by opt_seq";
		Object[] params = (String[]) queryMap.get("params");
		List<SysOperate> opts = null;
		if(params == null)
			opts = SysOperate.dao.find(querySql);
		else
			opts = SysOperate.dao.find(querySql, params);
		return opts;
	}
	
	public boolean saveSysOperate(SysOperate opt) {
		try {
			return opt.save();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean updateSysOperate(SysOperate opt){
		try {
			return opt.update();
		} catch (Exception e) {
			return false;
		}
	}
	public SysOperate queryOperateById(Integer opt_id) {
		return  SysOperate.dao.findById(opt_id);
	}
	public int deleteSysOperate(String[] opt_ids){
		try {
			int l = Db.update("delete from sys_operate where opt_id in ("+ToolStrYt.joinArray(opt_ids, ",", "'")+")");
			return l; 
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
