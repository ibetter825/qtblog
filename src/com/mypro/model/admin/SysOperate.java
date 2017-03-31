package com.mypro.model.admin;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "sys_operate", pkName = "opt_id")
public class SysOperate extends BaseModel<SysOperate> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final SysOperate dao = new SysOperate();
	public static final String all = "opt_id,opt_no,menu_no,opt_name,opt_desc,opt_seq,opt_state,opt_type,opt_url,opt_icon,is_show,opt_param,opt_class";
	public static final String prefix_all = "opt.opt_id,opt.opt_no,opt.menu_no,opt.opt_name,opt.opt_desc,opt.opt_seq,opt.opt_state,opt.opt_type,opt.opt_url,opt.opt_icon,opt.is_show,opt.opt_param,opt.opt_class";
	public static final String allwithmenu = "a.opt_id,a.opt_no,a.menu_no,a.opt_name,a.opt_desc,a.opt_seq,a.opt_state,a.opt_type,a.opt_url,a.opt_icon,a.is_show,a.opt_param,a.opt_class,b.menu_name";
	public static final String simp = "opt_id,opt_no,menu_no,opt_name,opt_url";
	public static final String prefix_simp = "opt.opt_id,opt.opt_no,opt.menu_no,opt.opt_name,opt.opt_url";
}
