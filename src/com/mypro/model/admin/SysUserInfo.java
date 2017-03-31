package com.mypro.model.admin;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "sys_user_info", pkName = "user_no")
public class SysUserInfo extends BaseModel<SysUserInfo> {
	/*public SysUser() {
		super(SysUser.class);
	}*/
	private static final long serialVersionUID = -836437769319422544L;
	public static final SysUserInfo dao = new SysUserInfo();
	//所有字段
	public static final String all = "user_no,real_name,user_avatar,user_gender,add_time,update_time,last_login_time,last_login_ip,user_phone,user_email,user_address";
	public static final String prefix_all = "info.user_no,info.real_name,info.user_avatar,info.user_gender,info.add_time,info.update_time,info.last_login_time,info.last_login_ip,info.user_phone,info.user_email,info.user_address";
	public static final String simp = "real_name,user_gender,user_phone,user_email";
	public static final String prefix_simp = "info.real_name,info.user_avatar,info.user_gender,info.user_phone,info.user_email";
}
