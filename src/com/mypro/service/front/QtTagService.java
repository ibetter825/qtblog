package com.mypro.service.front;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.front.QtArticle;
import com.mypro.model.front.QtTag;

public class QtTagService {
	public final static QtTagService service = new QtTagService();
	
	/**
	 * 查询编辑或撰写文章的页面，该用户常用的tags或热门的tags
	 * @return
	 */
	public Map<String, Object> queryWriteRmdTags(Integer acc_no){
		//查询出来的:type=1:热门tags,type=0:常用tags
		List<QtTag> tags = QtTag.dao.find(QtTag.sql_write_rmd, acc_no);
		List<String> oftens = null, hots = null;
		Map<String, Integer> mapping = null;
		long type = 0;
		String name = null;
		for (QtTag t : tags) {
			type = t.getLong("type");
			name = t.getStr("tag_name");
			if(mapping == null) mapping = new HashMap<String, Integer>();
			mapping.put(name, t.getInt("tag_id"));
			if(type == 0){
				if(oftens == null) oftens = new ArrayList<String>();
				oftens.add(name);
			}else{
				if(hots == null) hots = new ArrayList<String>();
				hots.add(name);
			}
		}
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("oftens", oftens);
		res.put("hots", hots);
		res.put("mapping", mapping);
		return res;
	}
	/**
	 * 根据tag_name查询标签
	 * @param tag_name
	 * @return
	 */
	public QtTag queryByTagName(String tag_name){
		return QtTag.dao.findFirst("select " + QtTag.all + " from qt_tag where tag_name = ?", tag_name);
	}
	public QtTag queryByTagId(String tag_id){
		return QtTag.dao.findById(tag_id);
	}
	/**
	 * 通过tag查询文章列表
	 * @param pager
	 * @param rq
	 * @return
	 */
	public Page<QtArticle> queryByTag(PagerModel pager, QueryModel rq){
		String select = QtTag.sql_query_article_by_tag_select;
		String sqlExceptSelect = QtTag.sql_query_article_by_tag_except;
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, true);
		sqlExceptSelect = (String) queryMap.get("sql");
		sqlExceptSelect = pager.getOrderSql(sqlExceptSelect);
		Object[] params = (String[]) queryMap.get("params");
		Page<QtArticle> page = null;
		if(params == null)
			page = QtArticle.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect);
		else
			page = QtArticle.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect, params);
		return page;
	}
	/**
	 * 获取用户标签云
	 * @param pager
	 * @param rq
	 * @return
	 */
	public Page<QtTag> queryTagCloud(PagerModel pager, QueryModel rq){
		String select = QtTag.sql_query_tag_cloud_select;
		String sqlExceptSelect = QtTag.sql_query_tag_cloud_except;
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, false);
		sqlExceptSelect = (String) queryMap.get("sql");
		sqlExceptSelect = pager.getOrderSql(sqlExceptSelect);
		Object[] params = (String[]) queryMap.get("params");
		Page<QtTag> page = null;
		if(params == null)
			page = QtTag.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect);
		else
			page = QtTag.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect, params);
		return page;
	}
	/**
	 * 查询热门标签
	 * @param pager
	 * @return
	 */
	public List<QtTag> queryHotTags(int limit){
		final int l = limit; 
		List<QtTag> htags = CacheKit.get("commonCache", "htags", new IDataLoader(){
			public Object load(){
				return QtTag.dao.find("select " + QtTag.all + " from qt_tag order by tag_num desc limit ?", l);
			}
		});
		return htags;
	}
	/**
	 * 保存获取id,或这查询获取id
	 * @param tag
	 * @return
	 */
	public Integer saveOrQuery(QtTag tag){
		try {
			if(tag.save())
				return tag.getInt("tag_id");
		} catch (Exception e) {
			QtTag t = queryByTagName(tag.getStr("tag_name"));
			if(t != null)
				return t.getInt("tag_id");
		}
		return 0;
	}
}
