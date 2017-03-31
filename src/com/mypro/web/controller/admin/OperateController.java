package com.mypro.web.controller.admin;

import org.apache.commons.lang.StringUtils;
import com.jfinal.plugin.activerecord.Page;
import com.mypro.annotation.Controller;
import com.mypro.enums.ResultMessageEnum;
import com.mypro.model.PageResultModel;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.ResultModel;
import com.mypro.model.admin.SysOperate;
import com.mypro.service.admin.SysOperateService;
import com.mypro.web.admin.BaseController;

@Controller(controllerKey = "/admin/opt")
public class OperateController extends BaseController {
	/**
	 * 查询菜单列表,返回JSON
	 */
	public void list(){
		QueryModel rq = new QueryModel(this, "opt");
		PagerModel pager = new PagerModel(this);
		Page<SysOperate> page = SysOperateService.service.queryOperateList(pager, rq);
		PageResultModel model = new PageResultModel();
		model.setRows(page.getList());
		model.setTotal(page.getTotalRow());
		model.setRowCount(page.getPageSize());
		model.setCurrent(pager.getCurPage());
		renderJson(model);
	}
	@SuppressWarnings("unchecked")
	public void form(){
		Integer opt_id = getParaToInt("opt_id");
		ResultModel model = null;
		if(opt_id != null){
			SysOperate opt = SysOperateService.service.queryOperateById(opt_id);
			model = new ResultModel();
			model.getData().put("opt", opt);
		}else
			model = new ResultModel("操作ID不能为空！");
		renderJson(model);
	}
	/**
	 * 保存操作
	 */
	public void save(){
		SysOperate opt = getModel(SysOperate.class, "opt");
		String type = getPara("opt_type");
		Boolean success = false; 
		if(type.equals("add"))
			success = SysOperateService.service.saveSysOperate(opt);
		else if(type.equals("modify"))
			success = SysOperateService.service.updateSysOperate(opt);
		ResultModel model = null;
		if(success)
			model = new ResultModel();
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	public void remove(){
		String ids = getPara("ids");
		String[] opt_ids = StringUtils.isEmpty(ids)?null:ids.split(",");
		int res = SysOperateService.service.deleteSysOperate(opt_ids);
		ResultModel model = null;
		if(res == opt_ids.length)
			model = new ResultModel();
		else if(res < opt_ids.length && res != 0)
			model = new ResultModel("2", "部分操作删除失败!");
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
}
