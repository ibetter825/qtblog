package com.mypro.service.front;

import java.util.Map;
import com.jfinal.plugin.activerecord.Page;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.front.QtNotice;

public class QtNoticeService {
	public final static QtNoticeService service = new QtNoticeService();
	/**
	 * 分页查询用户消息记录
	 * @param pager
	 * @param rq
	 * @return
	 */
	public Page<QtNotice> queryNOtices(PagerModel pager, QueryModel rq){
		String select = QtNotice.sql_query_notcie_select;
		String sqlExceptSelect = QtNotice.sql_query_notcie_except;
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, true);
		sqlExceptSelect = (String) queryMap.get("sql");
		sqlExceptSelect = pager.getOrderSql(sqlExceptSelect);
		Object[] params = (String[]) queryMap.get("params");
		Page<QtNotice> page = null;
		if(params == null)
			page = QtNotice.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect);
		else
			page = QtNotice.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect, params);
		return page;
	}
}
