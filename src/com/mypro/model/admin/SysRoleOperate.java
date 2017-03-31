package com.mypro.model.admin;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "sys_role_operate", pkName = "role_no,menu_no,opt_no")
public class SysRoleOperate extends BaseModel<SysRoleOperate> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final SysRoleOperate dao = new SysRoleOperate();
	public static final String all = "role_no,menu_no,opt_no";
}
