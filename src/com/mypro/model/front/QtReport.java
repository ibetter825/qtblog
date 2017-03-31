package com.mypro.model.front;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;

/**
 * 举报表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_report", pkName = "report_no")
public class QtReport extends BaseModel<QtReport> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtReport dao = new QtReport();
	public static final String all = "report_no,report_reason,report_state,report_type,report_acc,comt_no,reply_no,art_no,acc_no,add_time";
}
