package com.mypro.model.admin;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "sys_logs", pkName = "log_id")
public class SysLogs extends BaseModel<SysLogs> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final SysLogs dao = new SysLogs();
	public static final String all = "log_id,log_desc,log_uri,log_ip,log_time,log_user";
	public static final String allwithuser = "log.log_id,log.log_desc,log.log_uri,log.log_ip,log.log_time,log.log_user,user.user_name";
}
