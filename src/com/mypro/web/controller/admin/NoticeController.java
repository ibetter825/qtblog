package com.mypro.web.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.jfinal.aop.Duang;
import com.jfinal.plugin.activerecord.Page;
import com.mypro.annotation.Controller;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.context.SysUserContext;
import com.mypro.enums.ResultMessageEnum;
import com.mypro.model.PageResultModel;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.ResultModel;
import com.mypro.model.admin.SysNotice;
import com.mypro.model.admin.SysNoticeReceiver;
import com.mypro.model.admin.SysUser;
import com.mypro.service.admin.SysNoticeService;
import com.mypro.service.admin.SysUserService;
import com.mypro.web.admin.BaseController;

@Controller(controllerKey = "/admin/notice")
public class NoticeController extends BaseController {
	/**
	 * 查询菜单列表,返回JSON
	 */
	public void list(){
		QueryModel rq = new QueryModel(this, "notice", "info");
		PagerModel pager = new PagerModel(this);
		Page<SysNotice> page = SysNoticeService.service.queryNoticeList(pager, rq);
		PageResultModel model = new PageResultModel();
		model.setRows(page.getList());
		model.setTotal(page.getTotalRow());
		model.setRowCount(page.getPageSize());
		model.setCurrent(pager.getCurPage());
		renderJson(model);
	}
	public void form(){
		Integer notice_id = getParaToInt("ids");
		setAttr("notice", null);
		setAttr("opt_type", "add");
		List<SysNoticeReceiver> receivers = null;
		List<SysUser> users = SysUserService.service.queryUserList(new QueryModel());
		if(notice_id != null){
			setAttr("opt_type", "modify");
			SysNotice notice = SysNoticeService.service.queryNoticeById(notice_id);
			if(notice != null){
				receivers = SysNoticeService.service.queryNoticeReceiversById(notice_id);
				setAttr("notice", notice);
			}
		}
		List<Map<String, String>> rec = new ArrayList<Map<String,String>>();
		Map<String, String> re = null;
		if(receivers == null) receivers = new ArrayList<SysNoticeReceiver>();
		HashSet<String> is = new HashSet<>();
		for (SysNoticeReceiver r : receivers) 
			is.add(r.getStr("receiver_no"));
		
		for (SysUser user : users) {
			re = new HashMap<String, String>();
			re.put("user_no", user.getStr("user_no"));
			re.put("real_name", user.getStr("real_name"));
			re.put("selected", "0");
			if(is.contains(user.getStr("user_no")))
				re.put("selected", "1");
			rec.add(re);
		}
		setAttr("receivers", rec);
		renderFreeMarker("form.html");
	}
	public void detail(){
		Integer notice_id = getParaToInt("notice_id");
		SysNotice notice = SysNoticeService.service.queryNoticeById(notice_id);
		if(notice != null){
			setAttr("notice", notice);
			renderFreeMarker("detail.html");
		}else{
			ResultModel model = new ResultModel("系统通知不存在或已被删除");
			fail(model);
		}
	}
	/**
	 * 保存操作
	 */
	public void save(){
		SysNotice notice = getModel(SysNotice.class, "notice");
		String[] receiver_nos = getParaValues("receiver.receiver_no");
		String type = getPara("opt_type");
		Boolean success = false; 
		SysNoticeService service = Duang.duang(SysNoticeService.class);
		if(type.equals("add")){
			long add_time = ToolDateTime.getDateByTime();
			notice.set("add_time", add_time).set("creator_no", SysUserContext.getCurrentSysUser().get("user_no"));
			success = service.saveSysNotice(notice,receiver_nos);
		}else if(type.equals("modify"))
			success = service.updateSysNotice(notice, receiver_nos);
		ResultModel model = null;
		if(success)
			model = new ResultModel();
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	//单独修改SysNotice对象
	public void modify(){
		String ids = getPara("ids");
		Integer notice_state = getParaToInt("state");
		String[] notice_ids = StringUtils.isEmpty(ids)?null:ids.split(",");
		int res = SysNoticeService.service.updateSysNoticeState(notice_ids, notice_state);
		ResultModel model = null;
		if(res == notice_ids.length)
			model = new ResultModel();
		else if(res < notice_ids.length && res != 0)
			model = new ResultModel("2", "部分操作失败!");
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	public void remove(){
		String ids = getPara("ids");
		String[] notice_ids = StringUtils.isEmpty(ids)?null:ids.split(",");
		int res = SysNoticeService.service.deleteSysNotice(notice_ids);
		ResultModel model = null;
		if(res == notice_ids.length)
			model = new ResultModel();
		else if(res < notice_ids.length && res != 0)
			model = new ResultModel("2", "部分操作删除失败!");
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	/**
	 * 获取用户未读消息
	 */
	@SuppressWarnings("unchecked")
	public void unread(){
		List<SysNotice> notices = SysNoticeService.service.queryUnreadNoticeByUserNo(SysUserContext.getCurrentSysUser().getStr("user_no"));
		ResultModel model = null;
		if(notices != null){
			model = new ResultModel();
			model.getData().put("notices", notices);
		}else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
}
