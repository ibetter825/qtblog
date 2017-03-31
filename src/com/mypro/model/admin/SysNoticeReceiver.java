package com.mypro.model.admin;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "sys_notice_receiver", pkName = "notice_id,receiver_no")
public class SysNoticeReceiver extends BaseModel<SysNoticeReceiver> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final SysNoticeReceiver dao = new SysNoticeReceiver();
	public static final String all = "notice_id,receiver_no,is_read,read_time";
}
