package com.mypro.model.front;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;

/**
 * 用户详细表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_account_info", pkName = "acc_no")
public class QtAccountInfo extends BaseModel<QtAccountInfo> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtAccountInfo dao = new QtAccountInfo();
}
