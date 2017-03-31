package com.mypro.service.front;

import java.util.Map;
import com.jfinal.plugin.activerecord.Page;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.front.QtArticle;
import com.mypro.model.front.QtArticleColet;

/**
 * 收藏
 * @author ibett
 *
 */
public class QtColetService {
	public final static QtColetService service = new QtColetService();
	public boolean saveQtArtColet(QtArticleColet colet){
		return colet.save();
	}
	public boolean removeQtArtColet(QtArticleColet colet){
		return colet.delete();
	}
	/**
	 * 查询用户收藏的文章
	 * @param pager
	 * @param rq
	 * @return
	 */
	public Page<QtArticle> queryAccColets(PagerModel pager, QueryModel rq){
		String select = QtArticleColet.sql_query_acc_colet_select;
		String sqlExceptSelect = QtArticleColet.sql_query_acc_colet_except;
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
}
