package com.mypro.web.controller.front;

import java.util.List;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PropKit;
import com.mypro.annotation.Controller;
import com.mypro.common.DictKeys;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.model.front.QtHomePage;
import com.mypro.model.front.QtLink;
import com.mypro.model.front.QtMotto;
import com.mypro.model.front.QtTag;
import com.mypro.service.front.QtHomePageService;
import com.mypro.service.front.QtLinkService;
import com.mypro.service.front.QtTagService;
import com.mypro.web.admin.BaseController;
/**
 * 友情链接
 * @author ibett
 *
 */
@Controller(controllerKey = "/link")
@Clear(FrontContextInterceptor.class)
public class LinkController extends BaseController {
	public void index(){
		String ctitle = "<h1 class=\"title\"><strong>友情链接</strong></h1>";
		String content = QtLink.getLinkHtml(QtLinkService.service.queryLinks());
		
		if(isPjaxRequest()){
			renderHtml(ctitle+content);
		}else{
			setAttr("content", content);
			setAttr("ctitle", ctitle);
			setAttr("title", "友情链接 - " + PropKit.get(DictKeys.web_site_name));
			List<QtTag> hot_tags = QtTagService.service.queryHotTags(24);
			setAttr("htags", QtTag.getHotTagsHtml(hot_tags));
			setAttr("random", QtHomePage.getHomePageRandomsHtml(QtHomePageService.service.queryRandomArticles())); //随机推荐
			setAttr("motto", QtMotto.getSingleMottoHtml(QtHomePageService.service.queryRandomMotto()));
			renderWithHeader(basePath+"/module.html", "more", "link");
		}
	}
}
