package com.mypro.web.controller.front;

import java.util.List;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PropKit;
import com.mypro.annotation.Controller;
import com.mypro.common.DictKeys;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.model.front.QtHomePage;
import com.mypro.model.front.QtMotto;
import com.mypro.model.front.QtTag;
import com.mypro.service.front.QtHomePageService;
import com.mypro.service.front.QtTagService;
import com.mypro.web.admin.BaseController;
/**
 * 留言板
 * @author ibett
 *
 */
@Controller(controllerKey = "/msg")
@Clear(FrontContextInterceptor.class)
public class MsgController extends BaseController {
	public void index(){
		String ctitle = "<h1 class=\"title\"><strong>留言板</strong></h1>";
		String content = "<div id=\"content-main\"><article class=\"excerpt excerpt-one\"><p>没有结果!</p></article></div>";
		
		if(isPjaxRequest()){
			renderHtml(ctitle+content);
		}else{
			setAttr("content", content);
			setAttr("ctitle", ctitle);
			setAttr("title", "留言板 - " + PropKit.get(DictKeys.web_site_name));
			List<QtTag> hot_tags = QtTagService.service.queryHotTags(24);
			setAttr("htags", QtTag.getHotTagsHtml(hot_tags));
			setAttr("random", QtHomePage.getHomePageRandomsHtml(QtHomePageService.service.queryRandomArticles())); //随机推荐
			setAttr("motto", QtMotto.getSingleMottoHtml(QtHomePageService.service.queryRandomMotto()));
			renderWithHeader(basePath+"/module.html", "more,message");
		}
	}
}
