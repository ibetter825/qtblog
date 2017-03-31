package com.mypro.web.controller.admin;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.plugin.ehcache.EvictInterceptor;
import com.mypro.annotation.Controller;
import com.mypro.enums.ResultMessageEnum;
import com.mypro.model.PageResultModel;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.ResultModel;
import com.mypro.model.admin.SysMenu;
import com.mypro.model.front.QtArticle;
import com.mypro.service.admin.QtArticleService;
import com.mypro.service.admin.SysMenuService;
import com.mypro.web.admin.BaseController;

@Controller(controllerKey = "/admin/article")
public class QtArticleController extends BaseController {
	protected static String basePath = baseAdminPath + "/article";
	/**
	 * 默认进入文章列表
	 */
	public void index(){
		renderFreeMarker(basePath+"/article.html");
	}
	/**
	 * 进入到文章编辑或添加的form表单
	 */
	@SuppressWarnings("unchecked")
	public void form(){
		String menu_no = getPara("menu_no");
		ResultModel model = null;
		if(StringUtils.isNotEmpty(menu_no)){
			SysMenu menu = SysMenuService.service.queryMenuById(menu_no);
			model = new ResultModel();
			model.getData().put("menu", menu);
		}else
			model = new ResultModel("文章编号不能为空！");
		renderJson(model);
	}
	/**
	 * 查询文章列表,返回JSON
	 */
	public void list(){
		QueryModel rq = new QueryModel(this, "art", "acc");
		PagerModel pager = new PagerModel(this);
		Page<QtArticle> page = QtArticleService.service.queryArticleList(pager, rq);
		PageResultModel model = new PageResultModel();
		model.setRows(page.getList());
		model.setTotal(page.getTotalRow());
		model.setRowCount(page.getPageSize());
		model.setCurrent(pager.getCurPage());
		renderJson(model);
	}
	/**
	 * 查询文章树,返回JSON
	 */
	@SuppressWarnings("unchecked")
	public void tree(){
		List<SysMenu> menus = SysMenuService.service.queryMenuList(new QueryModel());
		ResultModel model = new ResultModel();
		model.getData().put("menus", menus);
		renderJson(model);
	}
	/**
	 * 保存文章
	 */
	public void save(){
		QtArticle article = getModel(QtArticle.class, "art");
		String type = getPara("opt_type");
		Boolean success = false; 
		if(type.equals("modify"))
			success = QtArticleService.service.updateQtArticle(article);
		ResultModel model = null;
		if(success)
			model = new ResultModel();
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	/**
	 * 静态化页面
	 */
	public void html(){
		String ids = getPara("ids");
		String[] art_nos = StringUtils.isEmpty(ids)?null:ids.split(",");
		int c = QtArticleService.service.staticArticle(art_nos, getRequestDomain());
		ResultModel model = null;
		if(c == art_nos.length)
			model = new ResultModel();
		else if(c == 0)
			model = new ResultModel("静态化文章失败");
		else
			model = new ResultModel("部分文章静态化成功");
		renderJson(model);
	}
	/**
	 * 删除文章
	 */
	@Before(EvictInterceptor.class)
	@CacheName("menuTree")
	public void remove(){
		String ids = getPara("ids");
		String[] menu_nos = StringUtils.isEmpty(ids)?null:ids.split(",");
		int res = SysMenuService.service.deleteSysMenu(menu_nos);
		ResultModel model = null;
		if(res == menu_nos.length)
			model = new ResultModel();
		else if(res < menu_nos.length && res != 0)
			model = new ResultModel("2", "部分文章删除失败!");
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	/**
	 * 验证menu_no是否已经存在
	 */
	public void vali(){
		String menu_no = getPara("fieldValue");
		String id = getPara("fieldId");
		SysMenu menu = SysMenuService.service.queryMenuById(menu_no);
		if(menu != null)
			renderJson("[\""+id+"\",false]");
		else
			renderJson("[\""+id+"\",true]");
	}
}
