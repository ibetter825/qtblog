package com.mypro.model.front;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;

/**
 * 用户表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_account", pkName = "acc_no")
public class QtAccount extends BaseModel<QtAccount> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtAccount dao = new QtAccount();
	public static final String all = "acc_no,acc_name,acc_email,acc_phone,acc_pwd,acc_salt,acc_state";
	public static final String allwithacc = "acc.acc_no,acc.acc_name,acc.acc_email,acc.acc_phone,acc.acc_pwd,acc.acc_salt,acc.acc_state,info.nick_name,info.acc_avatar,info.acc_intro";
	public static final String allwithaccwithout = "acc.acc_no,acc.acc_name,acc.acc_email,acc.acc_phone,info.nick_name,info.acc_avatar";
	public static final String allwithoutpwd = "acc_no,acc_name,acc_email,acc_phone,acc_state";
	public static final String simple = "acc_no,acc_name";
	public static final String allforsetting = "acc.acc_no,acc.acc_name,acc.acc_email,acc.acc_phone,info.nick_name,info.acc_avatar,info.acc_gender,info.acc_realname,info.acc_birthday,info.acc_regtime,info.acc_intro";
}
