package com.mypro.service.admin;

import java.util.Map;

import com.jfinal.plugin.activerecord.Page;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.admin.SysLogs;

public class SysLogsService {
	public final static SysLogsService service = new SysLogsService();
	public void saveLogs(SysLogs log){
		log.save();
	}
	public Page<SysLogs> queryLogList(PagerModel pager, QueryModel rq) {
		String select = "select "+SysLogs.allwithuser;
		String sqlExceptSelect = " from sys_logs log left join sys_user user on log.log_user = user.user_no";
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, true);
		sqlExceptSelect = (String) queryMap.get("sql");
		sqlExceptSelect = pager.getOrderSql(sqlExceptSelect);
		Object[] params = (String[]) queryMap.get("params");
		Page<SysLogs> page = null;
		if(params == null)
			page = SysLogs.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect);
		else
			page = SysLogs.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect, params);
		return page;
	}
}
