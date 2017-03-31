package com.mypro.service.admin;

import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.admin.QtRmd;
import com.mypro.web.tools.ToolStrYt;

public class QtRmdService {
	public final static QtRmdService service = new QtRmdService();
	
	public QtRmd queryRmdById(String rmd_no){
		return QtRmd.dao.findById(rmd_no);
	}
	/**
	 * 查询文章列表
	 * @param pager
	 * @param rq
	 * @return
	 */
	public Page<QtRmd> queryRmdList(PagerModel pager, QueryModel rq) {
		String select = "select "+QtRmd.all;
		String sqlExceptSelect = " from qt_rmd ";
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, true);
		sqlExceptSelect = (String) queryMap.get("sql");
		sqlExceptSelect = pager.getOrderSql(sqlExceptSelect);
		Object[] params = (String[]) queryMap.get("params");
		Page<QtRmd> page = null;
		if(params == null)
			page = QtRmd.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect);
		else
			page = QtRmd.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect, params);
		return page;
	}
	public boolean saveQtRmd(QtRmd rmd){
		return rmd.save();
	}
	public boolean updateQtRmd(QtRmd rmd){
		try {
			return rmd.update();
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 删除菜单
	 * @param rmd_nos
	 * @return
	 */
	public int deleteQtRmd(String[] rmd_nos){
		try {
			int l = Db.update("delete from qt_rmd where rmd_no in ("+ToolStrYt.joinArray(rmd_nos, ",", "'")+")");
			return l; 
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	/**
	 * 更新缓存
	 * @param type
	 * @return
	 */
	public void refreshCache(String rmd_type){
		List<QtRmd>	list = QtRmd.dao.find(QtRmd.sql_query_rmd_by_type, rmd_type);
		CacheKit.put("commonCache", "rmd-"+rmd_type, list);
	}
}
