package com.mypro.service.admin;

import java.util.List;
import java.util.Map;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.admin.SysNotice;
import com.mypro.model.admin.SysNoticeReceiver;
import com.mypro.web.tools.ToolStrYt;

public class SysNoticeService {
	public final static SysNoticeService service = new SysNoticeService();
	public Page<SysNotice> queryNoticeList(PagerModel pager, QueryModel rq) {
		String select = "select "+SysNotice.simplewithuser;
		String sqlExceptSelect = " from sys_notice notice left join sys_user_info info on info.user_no = notice.creator_no where notice.notice_state <> -1";
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, false);
		sqlExceptSelect = (String) queryMap.get("sql");
		sqlExceptSelect = pager.getOrderSql(sqlExceptSelect);
		Object[] params = (String[]) queryMap.get("params");
		Page<SysNotice> page = null;
		if(params == null)
			page = SysNotice.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect);
		else
			page = SysNotice.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect, params);
		return page;
	}
	@Before(Tx.class)
	public boolean saveSysNotice(SysNotice notice, String[] receiver_nos) {
		if(receiver_nos != null){
			notice.set("notice_type", 1).save();
			int notice_id = notice.getInt("notice_id");
			for (String no : receiver_nos) 
				new SysNoticeReceiver().set("notice_id", notice_id).set("receiver_no", no).save();
		}else
			notice.save();
		return true;
	}
	@Before(Tx.class)
	public boolean updateSysNotice(SysNotice notice, String[] receiver_nos){
		Integer notice_id = notice.getInt("notice_id");
		Db.update("delete from sys_notice_receiver where notice_id = ?", notice_id);
		if(receiver_nos != null){
			notice.set("notice_type", 1).update();
			for (String no : receiver_nos) 
				new SysNoticeReceiver().set("notice_id", notice_id).set("receiver_no", no).save();
		}else
			notice.set("notice_type", 0).update();
		return true;
	}
	public SysNotice queryNoticeById(Integer notice_id) {
		return SysNotice.dao.findById(notice_id);
	}
	public List<SysNoticeReceiver> queryNoticeReceiversById(Integer notice_id){
		return SysNoticeReceiver.dao.find("select " + SysNoticeReceiver.all + " from sys_notice_receiver where notice_id = ?", notice_id);
	}
	public List<SysNotice> queryUnreadNoticeByUserNo(String user_no){
		String sql = "select a.notice_id,a.notice_title,a.notice_level,a.add_time from sys_notice a left join sys_notice_receiver b on a.notice_id = b.notice_id  where b.receiver_no = ? and b.is_read = 0 and a.notice_type = 1 and a.notice_state = 2 and a.end_time >= unix_timestamp(now())";
			   sql += " union ";
			   sql += "select a.notice_id,a.notice_title,a.notice_level,a.add_time from sys_notice a where a.notice_id not in (select notice_id from sys_notice_reader where reader_no = ?) and a.notice_state = 2 and a.end_time >= unix_timestamp(now()) and a.notice_type = 0";
	    return SysNotice.dao.find(sql,user_no,user_no);
	}
	public int deleteSysNotice(String[] notice_ids){
		try {
			int l = Db.update("delete from sys_notice where notice_id in ("+ToolStrYt.joinArray(notice_ids, ",", "'")+")");
			return l; 
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	public int updateSysNoticeState(String[] notice_ids, Integer notice_state){
		int l = 0;
		try {
			for (String id : notice_ids) {
				if(new SysNotice().set("notice_id", id).set("notice_state", notice_state).update())
					l++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return l;
	}
}
