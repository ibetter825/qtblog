package com.mypro.web.controller.front;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.jfinal.aop.Clear;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Page;
import com.mypro.annotation.Controller;
import com.mypro.common.DictKeys;
import com.mypro.common.StaticData;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.front.QtArticle;
import com.mypro.model.front.QtHomePage;
import com.mypro.model.front.QtMotto;
import com.mypro.model.front.QtTag;
import com.mypro.service.front.QtArticleService;
import com.mypro.service.front.QtHomePageService;
import com.mypro.service.front.QtTagService;
import com.mypro.web.admin.BaseController;

@Controller(controllerKey = "/")
@Clear(FrontContextInterceptor.class)
public class IndexController extends BaseController {
	public void index(){
		String theme = getPara(0, null);
		List<QtTag> hot_tags = QtTagService.service.queryHotTags(24);
		String htags = QtTag.getHotTagsHtml(hot_tags);//热门标签
		if(theme == null){
			//查询最新的文章
			PagerModel pager = new PagerModel();
			pager.setCurPage(1);
			pager.setPageSize(10);
			pager.setSortName("a.add_time");
			pager.setSortOrder("desc");
			QueryModel rq = new QueryModel();
			rq.set("a.article_state", 1);
			rq.set("a.article_flag", 0);
			String articles = QtHomePageService.service.queryHomePageQtArts(pager, rq);
			Map<String, String> hpdata = QtHomePage.getHomePageDataHtml(QtHomePageService.service.queryHomePages());
			
			StringBuffer html = new StringBuffer();
			html.append("<div id=\"slider\" class=\"carousel slide\" data-ride=\"carousel\"><ol class=\"carousel-indicators\">");
			html.append(hpdata.get("slideol"));
			html.append("</ol><div class=\"carousel-inner\">").append(hpdata.get("slidediv")).append("</div>");
			html.append("<a class=\"left carousel-control\" href=\"#slider\" role=\"button\" data-slide=\"prev\">");
			html.append("<span class=\"glyphicon glyphicon-chevron-left\" aria-hidden=\"true\"></span></a>");
			html.append("<a class=\"right carousel-control\" href=\"#slider\" role=\"button\" data-slide=\"next\">");
			html.append("<span class=\"glyphicon glyphicon-chevron-right\" aria-hidden=\"true\"></span></a></div>");
			html.append("<div class=\"focusmo\"><ul>").append(hpdata.get("large")).append(hpdata.get("focus")).append("</ul></div>");
			
			html.append("<div class=\"most-comment-posts\"><h3 class=\"title\"><strong>一周热门排行</strong></h3><ul>");
			html.append(QtHomePage.getHomePageRanksHtml(QtHomePageService.service.queryRankArticles())).append("</ul></div>");
			html.append("<div class=\"sticky\"><h3 class=\"title\"><strong>热门推荐</strong></h3><ul>").append(hpdata.get("hot")).append("</ul></div>");
			html.append("<h3 class=\"title\"><!-- <small class=\"pull-right\">24小时更新：0篇 &nbsp; &nbsp; 一周更新：0篇</small> --><strong>最新发布</strong></h3>");
			html.append(articles);
			
			if(isPjaxRequest())
				renderHtml(html.toString());
			else{
				setAttr("home", html.toString());
				//setAttr("rank", QtHomePage.getHomePageRanksHtml(QtHomePageService.service.queryRankArticles())); //一周热门排行
				setAttr("random", QtHomePage.getHomePageRandomsHtml(QtHomePageService.service.queryRandomArticles())); //随机推荐
				setAttr("motto", QtMotto.getSingleMottoHtml(QtHomePageService.service.queryRandomMotto()));
				//setAttr("articles", articles);
				//setAttr("hpdata", hpdata);
				setAttr("htags", htags);
				renderWithHeader("index.html", "home");//首页不用pajax，内容 不一样
			}
		}else {
			int cur_page = getParaToInt(1, 1);
			int page_size = getParaToInt(2, 10);
			PagerModel pager = new PagerModel();
			pager.setCurPage(cur_page);
			pager.setPageSize(page_size);
			pager.setSortName("a.add_time");
			pager.setSortOrder("desc");
			QueryModel rq = new QueryModel();
			rq.set("a.article_state", 1);
			rq.set("a.article_flag", 0);
			rq.set("a.article_theme", theme);
			String title = MapUtils.getString(StaticData.navMapper, theme, null);
			if(title == null){
				renderError(404);
				return;
			}
			Page<QtArticle> page = QtArticleService.service.queryQtArtByParams(pager, rq);
			String pagination = PagerModel.getPaginationHtml(getCtx()+"/"+theme, page, '-', true);
			String content = QtArticle.getArtcilesHtml(page.getList(), false);
			String ctitle = "<h1 class=\"title\"><strong>"+title+"</strong></h1>";
			if(isPjaxRequest()){
				renderHtml(ctitle+content+pagination);
			}else{
				setAttr("pagination", pagination);
				setAttr("content", content);
				setAttr("ctitle", ctitle);
				setAttr("htags", htags);
				setAttr("title", title + " - " + PropKit.get(DictKeys.web_site_name));
				setAttr("random", QtHomePage.getHomePageRandomsHtml(QtHomePageService.service.queryRandomArticles())); //随机推荐
				setAttr("motto", QtMotto.getSingleMottoHtml(QtHomePageService.service.queryRandomMotto()));
				renderWithHeader(basePath+"/module.html", MapUtils.getString(StaticData.ruteMapper, theme, ""));
			}
		}
	}
}
