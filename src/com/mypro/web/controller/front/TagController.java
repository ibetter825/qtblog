package com.mypro.web.controller.front;

import java.util.List;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Page;
import com.mypro.annotation.Controller;
import com.mypro.common.DictKeys;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.front.QtArticle;
import com.mypro.model.front.QtHomePage;
import com.mypro.model.front.QtMotto;
import com.mypro.model.front.QtTag;
import com.mypro.service.front.QtHomePageService;
import com.mypro.service.front.QtTagService;
import com.mypro.web.admin.BaseController;
/**
 * 点赞
 * @author ibett
 *
 */
@Controller(controllerKey = "/tag")
@Clear(FrontContextInterceptor.class)
public class TagController extends BaseController {
	/**
	 * 默认查询某一个标签下的文章列表
	 */
	public void index(){
		String tag_id = getPara(0, null);
		if(tag_id != null){
			QtTag tag = QtTagService.service.queryByTagId(tag_id);
			if(tag != null){
				int cur_page = getParaToInt(1, 1);
				int page_size = getParaToInt(2, 10);
				PagerModel pager = new PagerModel();
				pager.setCurPage(cur_page);
				pager.setPageSize(page_size);
				pager.setSortName("a.add_time");
				pager.setSortOrder("desc");
				QueryModel rq = new QueryModel();
				rq.set("tg.tag_id", tag_id);
				rq.set("a.article_state", 1);
				rq.set("a.article_flag", 0);
				Page<QtArticle> page = QtTagService.service.queryByTag(pager, rq);
				
				String ctitle = "<h1 class=\"title\"><strong>标签："+tag.getStr("tag_name")+"</strong></h1>";
				String content = QtArticle.getArtcilesHtml(page.getList(), false);
				String pagination = PagerModel.getPaginationHtml(getCtx()+"/tag/"+tag_id, page, '-', false);
				
				if(isPjaxRequest()){
					renderHtml(ctitle+content+pagination);
				}else{
					setAttr("content", content);
					setAttr("ctitle", ctitle);
					setAttr("pagination", pagination);
					setAttr("title", tag.getStr("tag_name")+" - " + PropKit.get(DictKeys.web_site_name));
					List<QtTag> hot_tags = QtTagService.service.queryHotTags(24);
					setAttr("htags", QtTag.getHotTagsHtml(hot_tags));
					setAttr("random", QtHomePage.getHomePageRandomsHtml(QtHomePageService.service.queryRandomArticles())); //随机推荐
					setAttr("motto", QtMotto.getSingleMottoHtml(QtHomePageService.service.queryRandomMotto()));
					renderWithHeader(basePath+"/module.html");
				}
				return;
			}
		}
		renderError(404);
	}
	/**
	 * 标签云
	 */
	public void cloud(){
		int cur_page = getParaToInt(0, 1);
		int page_size = getParaToInt(1, 50);
		PagerModel pager = new PagerModel();
		pager.setCurPage(cur_page);
		pager.setPageSize(page_size);
		Page<QtTag> page = QtTagService.service.queryTagCloud(pager, new QueryModel());
		
		String content = QtTag.getTagCloudHtml(page.getList());
		String pagination = PagerModel.getPaginationHtml(getCtx()+"/tag/cloud", page, '/', false);
		String ctitle = "<h1 class=\"title\"><strong>标签云</strong></h1>";
		if(isPjaxRequest()){
			renderHtml(ctitle+content+pagination);
		}else{
			List<QtTag> hot_tags = QtTagService.service.queryHotTags(24);
			String htags = QtTag.getHotTagsHtml(hot_tags);//热门标签
			setAttr("pagination", pagination);
			setAttr("content", content);
			setAttr("ctitle", ctitle);
			setAttr("htags", htags);
			setAttr("title", "标签云 - " + PropKit.get(DictKeys.web_site_name));
			setAttr("random", QtHomePage.getHomePageRandomsHtml(QtHomePageService.service.queryRandomArticles())); //随机推荐
			setAttr("motto", QtMotto.getSingleMottoHtml(QtHomePageService.service.queryRandomMotto()));
			renderWithHeader(basePath+"/module.html", "more", "cloud");
		}
	}
}
