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
import com.mypro.service.front.QtArticleService;
import com.mypro.service.front.QtHomePageService;
import com.mypro.service.front.QtTagService;
import com.mypro.web.admin.BaseController;
/**
 * 全文检索，检索成功后需要将匹配的文字高亮显示
 * @author ibett
 *
 */
@Controller(controllerKey = "/search")
@Clear(FrontContextInterceptor.class)
public class SearchController extends BaseController {
	public void index(){
		String sech_key = getPara("s");//搜索的关键词
		//关键词只能是中文，引文，数字等等，不能包含其他的特殊的字符
		Integer cur_page = getParaToInt("c", 1);
		Integer page_size = getParaToInt("p", 10);
		PagerModel pager = new PagerModel();
		pager.setCurPage(cur_page);
		pager.setPageSize(page_size);
		QueryModel rq = new QueryModel();
		rq.set("a.article_state", 1);
		rq.set("a.article_title[%]", sech_key);
		Page<QtArticle> page = QtArticleService.service.queryQtArtByParams(pager, rq);
		setAttr("pagination", PagerModel.getPaginationHtml(getCtx()+"/search?s="+sech_key+"&c", page, '=', false));
		setAttr("content", QtArticle.getSearchArtcilesHtml(page.getList(), sech_key));
		setAttr("ctitle", "<h1 class=\"title\"><strong>"+sech_key+" 的检索结果</strong></h1>");
		setAttr("title", sech_key + " - " + PropKit.get(DictKeys.web_site_name));
		setAttr("random", QtHomePage.getHomePageRandomsHtml(QtHomePageService.service.queryRandomArticles())); //随机推荐
		setAttr("motto", QtMotto.getSingleMottoHtml(QtHomePageService.service.queryRandomMotto()));
		List<QtTag> hot_tags = QtTagService.service.queryHotTags(24);
		setAttr("htags", QtTag.getHotTagsHtml(hot_tags));
		renderWithHeader(basePath+"/module.html");
	}
}
