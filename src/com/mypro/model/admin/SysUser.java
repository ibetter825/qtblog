package com.mypro.model.admin;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "sys_user", pkName = "user_no")
public class SysUser extends BaseModel<SysUser> {
	/*public SysUser() {
		super(SysUser.class);
	}*/
	private static final long serialVersionUID = -836437769319422544L;
	public static final SysUser dao = new SysUser();
	//所有字段
	public static final String all = "user_no,user_pwd,user_salt,user_name,user_state,role_no";
	public static final String prefix_all = "user.user_no,user.user_pwd,user.user_salt,user.user_name,user.user_state,user.role_no";
	//基本字段
	public static final String simp = "user_no,user_name,user_state,role_no";
	public static final String prefix_simp = "user.user_no,user.user_name,user.user_state,user.role_no";
	
	public static final String allwithinfobase = prefix_all+","+SysUserInfo.prefix_simp;
	public static final String userwithinfo = prefix_simp+","+SysUserInfo.prefix_all;
	public static final String userwithinfobase = prefix_simp+","+SysUserInfo.prefix_simp;
}
