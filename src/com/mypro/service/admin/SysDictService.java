package com.mypro.service.admin;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.admin.SysDict;
import com.mypro.web.tools.ToolStrYt;

public class SysDictService {
	public final static SysDictService service = new SysDictService();
	/**
	 * 根据条件获取所有的数据字典
	 * @return
	 */
	public Page<SysDict> queryDictList(PagerModel pager, QueryModel rq) {
		String select = "select "+SysDict.all;
		String sqlExceptSelect = " from sys_dict where dict_state <> -1";
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, false);
		sqlExceptSelect = (String) queryMap.get("sql");
		sqlExceptSelect = pager.getOrderSql(sqlExceptSelect);
		Object[] params = (String[]) queryMap.get("params");
		Page<SysDict> page = null;
		if(params == null)
			page = SysDict.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect);
		else
			page = SysDict.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect, params);
		return page;
	}
	/**
	 * 查询dict tree
	 * @param rq
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map> queryDictList(QueryModel rq){
		String querySql = "select "+SysDict.all+" from sys_dict where dict_state <> -1";
		Map<String, Object> queryMap = rq.getQuerySql(querySql, false);
		querySql = (String) queryMap.get("sql") + " order by dict_seq asc";
		List<SysDict> dicts = null;
		if(queryMap.get("params") == null)
			dicts = SysDict.dao.find(querySql);
		else
			dicts = SysDict.dao.find(querySql, (Object[]) queryMap.get("params"));
		List<Map> res = null;
		if(dicts != null)
			res = SysDict.getDictTree(dicts, null);
		return res;
	}
	/**
	 * 根据主键查询数据字典
	 * @param dict_no
	 * @return
	 */
	public SysDict queryDictById(String dict_no) {
		return SysDict.dao.findById(dict_no);
	}
	/**
	 * 根据条件查询数据字典
	 * @param dict_no
	 * @return
	 */
	public SysDict queryDict(QueryModel rq) {
		String querySql = "select "+SysDict.all+" from sys_dict where dict_state <> -1";
		Map<String, Object> queryMap = rq.getQuerySql(querySql, false);
		SysDict dict = null;
		if(queryMap.get("params") == null)
			dict = SysDict.dao.findFirst(querySql);
		else
			dict = SysDict.dao.findFirst(querySql, (Object[]) queryMap.get("params"));
		return dict;
	}
	/**
	 * 根据menu_no数组查询菜单集合
	 * @param dict_nos
	 * @return
	 */
	public List<SysDict> queryDictsByIds(String[] dict_nos) {
		return SysDict.dao.find("select "+SysDict.all+" from sys_dict where dict_no in (" + ToolStrYt.joinArray(dict_nos, ",", "'") + "order by dict_level)");
	}
	/**
	 * 根据dict_no查询出的dict_scort查询其上级菜单
	 * @param dict_no
	 * @return
	 */
	public List<SysDict> queryMenusByDictScort(String dict_no){
		return SysDict.dao.find("select "+SysDict.all+" from sys_dict a where INSTR((select dict_scort from sys_dict where dict_no = ?),a.dict_no) order by dict_level", dict_no);
	}
	/**
	 * 保存数据字典
	 * @param dict
	 * @return
	 */
	public boolean saveSysDict(SysDict dict) {
		try {
			String dict_fno = dict.getStr("dict_fno");
			if(StringUtils.isEmpty(dict_fno))//一级菜单
				dict.set("dict_scort", dict.getStr("dict_no"));
			else{
				SysDict fdict = queryDictById(dict.getStr("dict_fno"));
				if(fdict == null)
					return false;
				dict.set("dict_level", fdict.getInt("dict_level")+1);
				dict.set("dict_scort", fdict.getStr("dict_scort")+","+dict.getStr("dict_no"));
			}
			return dict.save();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 修改数字典
	 * @param dict
	 * @return
	 */
	public boolean updateSysDict(SysDict dict){
		try {
			return dict.update();
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 删除数据字典
	 * @param dict_nos
	 * @return
	 */
	public int deleteSysDict(String[] dict_nos){
		try {
			int l = Db.update("delete from sys_dict where dict_no in ("+ToolStrYt.joinArray(dict_nos, ",", "'")+")");
			return l; 
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
