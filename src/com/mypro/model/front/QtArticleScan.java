package com.mypro.model.front;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;

/**
 * 统计表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_article_scan", pkName = "acc_no")
public class QtArticleScan extends BaseModel<QtArticleScan> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtArticleScan dao = new QtArticleScan();
	public static final String all = "acc_no,article_no,scan_time";
}
