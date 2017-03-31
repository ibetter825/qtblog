package com.mypro.web.controller.admin;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.plugin.ehcache.EvictInterceptor;
import com.mypro.annotation.Controller;
import com.mypro.enums.ResultMessageEnum;
import com.mypro.model.PageResultModel;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.ResultModel;
import com.mypro.model.admin.SysDict;
import com.mypro.service.admin.SysDictService;
import com.mypro.web.admin.BaseController;

@Controller(controllerKey = "/admin/dict")
public class DictController extends BaseController {
	protected static String basePath = baseAdminPath + "/dict";
	/**
	 * 默认进入字典列表
	 */
	public void index(){
		renderFreeMarker(basePath+"/dict.html");
	}
	/**
	 * 进入到字典编辑或添加的form表单
	 */
	@SuppressWarnings("unchecked")
	public void form(){
		String dict_no = getPara("dict_no");
		ResultModel model = null;
		if(StringUtils.isNotEmpty(dict_no)){
			SysDict dict = SysDictService.service.queryDictById(dict_no);
			model = new ResultModel();
			model.getData().put("dto", dict);
		}else
			model = new ResultModel("字典编号不能为空！");
		renderJson(model);
	}
	/**
	 * 查询字典列表,返回JSON
	 */
	public void list(){
		QueryModel rq = new QueryModel(this, "dict");
		PagerModel pager = new PagerModel(this);
		Page<SysDict> page = SysDictService.service.queryDictList(pager, rq);
		PageResultModel model = new PageResultModel();
		model.setRows(page.getList());
		model.setTotal(page.getTotalRow());
		model.setRowCount(page.getPageSize());
		model.setCurrent(pager.getCurPage());
		renderJson(model);
	}
	/**
	 * 查询字典树,返回JSON
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before(CacheInterceptor.class)
	@CacheName("dictTree")
	public void tree(){
		List<Map> dicts = SysDictService.service.queryDictList(new QueryModel());
		ResultModel model = new ResultModel();
		model.getData().put("dicts", dicts);
		renderJson(model);
	}
	/**
	 * 保存新字典
	 */
	@Before(EvictInterceptor.class)
	@CacheName("dictTree")
	public void save(){
		SysDict dict = getModel(SysDict.class, "dict");
		String type = getPara("opt_type");
		Boolean success = false; 
		if(type.equals("add"))
			success = SysDictService.service.saveSysDict(dict);
		else if(type.equals("modify"))
			success = SysDictService.service.updateSysDict(dict);
		ResultModel model = null;
		if(success)
			model = new ResultModel();
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	/**
	 * 删除字典
	 */
	@Before(EvictInterceptor.class)
	@CacheName("dictTree")
	public void remove(){
		String ids = getPara("ids");
		String[] dict_nos = StringUtils.isEmpty(ids)?null:ids.split(",");
		int res = SysDictService.service.deleteSysDict(dict_nos);
		ResultModel model = null;
		if(res == dict_nos.length)
			model = new ResultModel();
		else if(res < dict_nos.length && res != 0)
			model = new ResultModel("2", "部分数据删除失败!");
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	/**
	 * 验证menu_no是否已经存在
	 */
	public void vali(){
		String dict_no = getPara("fieldValue");
		String id = getPara("fieldId");
		SysDict dict = SysDictService.service.queryDictById(dict_no);
		if(dict != null)
			renderJson("[\""+id+"\",false]");
		else
			renderJson("[\""+id+"\",true]");
	}
}
