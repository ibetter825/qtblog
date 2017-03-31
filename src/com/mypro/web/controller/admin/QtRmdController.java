package com.mypro.web.controller.admin;

import org.apache.commons.lang.StringUtils;
import com.jfinal.plugin.activerecord.Page;
import com.mypro.annotation.Controller;
import com.mypro.common.tools.StrUtils;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.enums.ResultMessageEnum;
import com.mypro.model.PageResultModel;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.ResultModel;
import com.mypro.model.admin.QtRmd;
import com.mypro.service.admin.QtRmdService;
import com.mypro.web.admin.BaseController;

@Controller(controllerKey = "/admin/rmd")
public class QtRmdController extends BaseController {
	/**
	 * 进入到推荐内容编辑或添加的form表单
	 */
	@SuppressWarnings("unchecked")
	public void form(){
		String rmd_no = getPara("rmd_no");
		ResultModel model = null;
		if(StringUtils.isNotEmpty(rmd_no)){
			QtRmd rmd = QtRmdService.service.queryRmdById(rmd_no);
			model = new ResultModel();
			model.getData().put("rmd", rmd);
		}else
			model = new ResultModel("推荐编号不能为空！");
		renderJson(model);
	}
	/**
	 * 查询推荐内容列表,返回JSON
	 */
	public void list(){
		QueryModel rq = new QueryModel(this, "rmd");
		PagerModel pager = new PagerModel(this);
		Page<QtRmd> page = QtRmdService.service.queryRmdList(pager, rq);
		PageResultModel model = new PageResultModel();
		model.setRows(page.getList());
		model.setTotal(page.getTotalRow());
		model.setRowCount(page.getPageSize());
		model.setCurrent(pager.getCurPage());
		renderJson(model);
	}
	/**
	 * 保存推荐内容
	 */
	public void save(){
		QtRmd rmd = getModel(QtRmd.class, "rmd");
		String type = getPara("opt_type");
		Boolean success = false; 
		if(type.equals("add")){
			rmd.set("rmd_no", StrUtils.getUUID(false));
			rmd.set("rmd_time", ToolDateTime.getDateByTime());
			success = QtRmdService.service.saveQtRmd(rmd);
		}else if(type.equals("modify"))
			success = QtRmdService.service.updateQtRmd(rmd);
		ResultModel model = null;
		if(success){
			model = new ResultModel();
			//成功后自动更新缓存
			QtRmdService.service.refreshCache(rmd.getStr("rmd_type"));
		}else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	/**
	 * 删除推荐内容
	 */
	public void remove(){
		String ids = getPara("ids");
		String[] rmd_nos = StringUtils.isEmpty(ids)?null:ids.split(",");
		int res = QtRmdService.service.deleteQtRmd(rmd_nos);
		ResultModel model = null;
		if(res == rmd_nos.length)
			model = new ResultModel();
		else if(res < rmd_nos.length && res != 0)
			model = new ResultModel("2", "部分推荐删除失败!");
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	/**
	 * 手动更新缓存
	 */
	public void refresh(){
		String rmd_type = getPara(0,"hot");
		QtRmdService.service.refreshCache(rmd_type);
		renderJson(new ResultModel());
	}
}
