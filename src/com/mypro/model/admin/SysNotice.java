package com.mypro.model.admin;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "sys_notice", pkName = "notice_id")
public class SysNotice extends BaseModel<SysNotice> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final SysNotice dao = new SysNotice();
	public static final String all = "notice_id,notice_title,notice_abstract,notice_content,add_time,start_time,end_time,notice_type,notice_state,creator_no,notice_level";
	public static final String simple = "notice_id,notice_title,notice_abstract,add_time,start_time,end_time,notice_type,notice_state,creator_no,notice_level";
	public static final String simplewithuser = "notice.notice_id,notice.notice_title,notice.notice_abstract,notice.add_time,notice.start_time,notice.end_time,notice.notice_type,notice.notice_state,notice.creator_no,notice.notice_level,info.real_name";
}
