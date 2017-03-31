package com.mypro.web.controller.admin;

import com.jfinal.plugin.ehcache.CacheKit;
import com.mypro.annotation.Controller;
import com.mypro.enums.ResultMessageEnum;
import com.mypro.model.ResultModel;
import com.mypro.model.front.QtHomePage;
import com.mypro.service.front.QtHomePageService;
import com.mypro.service.front.QtLinkService;
import com.mypro.service.front.QtNavService;
import com.mypro.service.front.QtTagService;
import com.mypro.web.admin.BaseController;

@Controller(controllerKey = "/admin/hp")
public class QtHomePageController extends BaseController {
	
	/**
	 * 查询所有的首页配置
	 */
	@SuppressWarnings("unchecked")
	public void query(){
		Integer hp_no = getParaToInt(0);
		String hp_type = getPara(1);
		ResultModel model = new ResultModel();
		model.getData().put("res", QtHomePageService.service.queryHomePagesWithOutCache(hp_no, hp_type));
		renderJson(model);
	}
	/**
	 * 保存推荐内容
	 */
	public void save(){
		QtHomePage hp = getModel(QtHomePage.class, "hp");
		boolean success = QtHomePageService.service.saveOrUpdateHomePage(hp); 
		ResultModel model = null;
		if(success)
			model = new ResultModel();
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	/**
	 * 更新首页缓存
	 */
	public void refresh(){
		//1:更新首页hpdata缓存 2:更新首页最新文章缓存 3:更新首页热门缓存 4:更新随机推荐缓存 5:一周热门缓存 6:导航缓存 7:名人名言 8:友情链接
		int type = getParaToInt(0, 1);
		switch (type) {
			case 1:
				CacheKit.remove("commonCache", "hpdata");
				QtHomePageService.service.queryHomePages();
				break;
			case 2:
				CacheKit.remove("commonCache", "articles");//传的参数太多，不重新查询一次
				break;
			case 3:
				CacheKit.remove("commonCache", "htags");
				QtTagService.service.queryHotTags(24);
				break;
			case 4:
				CacheKit.remove("commonCache", "random");//移除缓存
				QtHomePageService.service.queryRandomArticles();//再查询一次
				break;
			case 5:
				CacheKit.remove("commonCache", "rank");//移除缓存
				QtHomePageService.service.queryRankArticles();//再查询一次
				break;
			case 6:
				CacheKit.remove("commonCache", "navs");//移除缓存
				QtNavService.service.queryNavs();
				break;
			case 7:
				CacheKit.remove("commonCache", "motto");//移除缓存
				QtHomePageService.service.queryRandomMotto();
				break;
			case 8:
				CacheKit.remove("commonCache", "link");//移除缓存
				QtLinkService.service.queryLinks();
				break;
		}
		renderJson(new ResultModel());
	}
	/**
	 * 删除推荐内容
	 */
	public void remove(){
		//String id = getPara("id");
		renderJson("");
	}
}
