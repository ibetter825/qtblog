package com.mypro.web.controller.front;

import java.util.List;
import com.jfinal.plugin.activerecord.Page;
import com.mypro.annotation.Controller;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.front.QtAccFollow;
import com.mypro.model.front.QtAccount;
import com.mypro.model.front.QtArticle;
import com.mypro.model.front.QtArticleCount;
import com.mypro.model.front.QtArticleType;
import com.mypro.model.front.QtNotice;
import com.mypro.service.front.QtAccFollowService;
import com.mypro.service.front.QtAccountService;
import com.mypro.service.front.QtArticleService;
import com.mypro.service.front.QtArticleTypeService;
import com.mypro.service.front.QtColetService;
import com.mypro.service.front.QtCountService;
import com.mypro.service.front.QtNoticeService;
import com.mypro.web.admin.BaseController;
/**
 * 个人中心
 * @author ibett
 *
 */
@Controller(controllerKey = "/zone")
public class ZoneController extends BaseController {
	private final static String basePath = "/WEB-INF/view/front";
	public void index(){
		//查询该用户的所有随笔，日志等动态以及评论，点击数，点赞数，收藏数等等，并且按发表的时间排序
		String type = getPara(0, "t");//用户自定义分类,一般传过来的都是>=0的整数，查询全部的时候，修改传过来t
		int cur_page = getParaToInt(1, 1);
		int page_size = getParaToInt(2, 10);
		PagerModel pager = new PagerModel();
		pager.setCurPage(cur_page);
		pager.setPageSize(page_size);
		pager.setSortName("a.add_time");
		pager.setSortOrder("desc");
		QtAccount account = getCurrentAccount();
		Integer acc_no = account.getInt("acc_no");
		QueryModel rq = new QueryModel();
		rq.set("a.article_state[=]", 1);
		rq.set("a.acc_no", acc_no);
		int article_type = -1;
		if(!type.equals("t")){
			rq.set("a.article_type[=]", type);
			article_type = Integer.parseInt(type);
		}
		
		Page<QtArticle> page = QtArticleService.service.queryQtArtByParams(pager, rq);
		List<QtArticle> articles = page.getList();
		//文章栏目，以及该作者在各分类下的文章数量
		//List<Map> dicts = QtArticleService.service.queryArtCountByDict(acc_no, "article");
		//setAttr("dicts", SysDict.getFrontZoneLi(dicts));
		//用户自定义分类
		List<QtArticleType> types = QtArticleTypeService.service.queryQtArtTypeByAcc(acc_no);
		String ctitle = QtArticleType.getFrontZoneTypesHtml(types, article_type);
		String pagination = PagerModel.getPaginationHtml(getCtx()+"/zone/"+type, page, '-', true);
		String content = QtArticle.getArtcilesHtml(articles, true);
		
		if(isPjaxRequest()){//pjax请求
			renderHtml(ctitle+content+pagination);
		}else{
			setAttr("account", account);
			setAttr("ctitle", ctitle);
			setAttr("content", content);
			setAttr("pagination", pagination);
			setAttr("count", QtArticleCount.getFrontCountHtml(QtCountService.service.queryByAcc(acc_no), null));
			renderWithHeader(basePath+"/zone.html");
		}
	}
	/**
	 * 回收站
	 */
	public void trash(){
		String type = getPara(0, "t");//用户自定义分类,一般传过来的都是>=0的整数，查询全部的时候，修改传过来t
		int cur_page = getParaToInt(1, 1);
		int page_size = getParaToInt(2, 10);
		PagerModel pager = new PagerModel();
		pager.setCurPage(cur_page);
		pager.setPageSize(page_size);
		pager.setSortName("a.update_time");
		pager.setSortOrder("desc");
		QtAccount account = getCurrentAccount();
		Integer acc_no = account.getInt("acc_no");
		QueryModel rq = new QueryModel();
		rq.set("a.article_state[=]", -1);
		rq.set("a.acc_no", acc_no);
		
		Page<QtArticle> page = QtArticleService.service.queryQtArtByParams(pager, rq);
		List<QtArticle> articles = page.getList();
		//用户自定义分类
		String ctitle = "<div id=\"content-title\" class=\"title\"><strong><a class=\"pjax\" href=\"/zone/\" \">回收站</a></strong></div>";
		String pagination = PagerModel.getPaginationHtml("/zone/trash/"+type, page, '-', true);
		String content = QtArticle.getTrashArticlesHtml(articles);
		
		if(isPjaxRequest()){//pjax请求
			renderHtml(ctitle+content+pagination);
		}else{
			setAttr("account", account);
			setAttr("ctitle", ctitle);
			setAttr("content", content);
			setAttr("pagination", pagination);
			setAttr("count", QtArticleCount.getFrontCountHtml(QtCountService.service.queryByAcc(acc_no), null));
			renderWithHeader(basePath+"/zone.html");
		}
	}
	/**
	 * 个人中心粉丝
	 */
	public void follower(){
		QtAccount account = getCurrentAccount();//当前登录的用户
		Integer acc_no = account.getInt("acc_no");
		
		int cur_page = getParaToInt(0, 1);
		int page_size = getParaToInt(1, 10);
		PagerModel pager = new PagerModel();
		pager.setCurPage(cur_page);
		pager.setPageSize(page_size);
		/*QueryModel rq = new QueryModel();
		rq.set("af.followed_acc", acc_no);*/
		List<QtAccFollow> page = QtAccFollowService.service.queryFollowers(pager, acc_no, acc_no);//用户的
		
		String content = QtAccFollow.getFollowersHtml(page);
		String ctitle = QtAccFollow.getMainZoneFrontTitleHtml(null, "follower");
		
		if(isPjaxRequest()){
			renderHtml(ctitle+content);
		}else{
			setAttr("account", account);
			setAttr("content", content);
			setAttr("ctitle", ctitle);
			setAttr("count", QtArticleCount.getFrontCountHtml(QtCountService.service.queryByAcc(acc_no), null));
			renderWithHeader(basePath+"/zone.html");
		}
	}
	/**
	 * 个人中心用户关注
	 */
	public void following(){
		QtAccount account = getCurrentAccount();//当前登录的用户
		Integer acc_no = account.getInt("acc_no");
		int cur_page = getParaToInt(0, 1);
		int page_size = getParaToInt(1, 10);
		PagerModel pager = new PagerModel();
		pager.setCurPage(cur_page);
		pager.setPageSize(page_size);
		/*QueryModel rq = new QueryModel();
		rq.set("af.follow_acc", acc_no);*/
		List<QtAccFollow> page = QtAccFollowService.service.queryFollowing(pager, acc_no, acc_no);//用户的
		
		String content = QtAccFollow.getFollowersHtml(page);
		String ctitle = QtAccFollow.getMainZoneFrontTitleHtml(null, "following");
		if(isPjaxRequest()){
			renderHtml(ctitle+content);
		}else{
			setAttr("account", account);
			setAttr("content", content);
			setAttr("ctitle", ctitle);
			setAttr("count", QtArticleCount.getFrontCountHtml(QtCountService.service.queryByAcc(acc_no), null));
			renderWithHeader(basePath+"/zone.html");
		}
	}
	public void collect(){
		QtAccount account = getCurrentAccount();//当前登录的用户
		Integer acc_no = account.getInt("acc_no");
		int cur_page = getParaToInt(0, 1);
		int page_size = getParaToInt(1, 10);
		PagerModel pager = new PagerModel();
		pager.setCurPage(cur_page);
		pager.setPageSize(page_size);
		pager.setSortName("ac.collect_time");
		pager.setSortOrder("desc");
		
		QueryModel rq = new QueryModel();
		rq.set("ac.acc_no", acc_no);
		Page<QtArticle> page = QtColetService.service.queryAccColets(pager, rq);
		
		String content = QtArticle.getColectArtcilesHtml(page.getList(), true);
		String ctitle = "<h1 class=\"title\"><strong>收藏的文章</strong></h1>";
		if(isPjaxRequest()){
			renderHtml(ctitle+content);
		}else{
			setAttr("account", account);
			//收藏的文章需要，添加取消收藏的按钮
			setAttr("content", content);
			setAttr("ctitle", ctitle);
			setAttr("count", QtArticleCount.getFrontCountHtml(QtCountService.service.queryByAcc(acc_no), null));
			renderWithHeader(basePath+"/zone.html");
		}
	}
	/**
	 * 用户消息
	 */
	public void notice(){
		QtAccount account = getCurrentAccount();//当前登录的用户
		Integer acc_no = account.getInt("acc_no");
		int cur_page = getParaToInt(0, 1);
		int page_size = getParaToInt(1, 10);
		PagerModel pager = new PagerModel();
		pager.setCurPage(cur_page);
		pager.setPageSize(page_size);
		pager.setSortName("n.add_time");
		pager.setSortOrder("desc");
		
		QueryModel rq = new QueryModel();
		rq.set("n.nt_to_no", acc_no);//查询当前登录用户的消息
		Page<QtNotice> page = QtNoticeService.service.queryNOtices(pager, rq);
		
		String content = QtNotice.getNoticesHtml(page.getList());
		String ctitle = "<h1 class=\"title\"><strong>通知消息</strong></h1>";
		if(isPjaxRequest()){
			renderHtml(ctitle+content);
		}else{
			setAttr("account", account);
			//收藏的文章需要，添加取消收藏的按钮
			setAttr("content", content);
			setAttr("ctitle", ctitle);
			setAttr("count", QtArticleCount.getFrontCountHtml(QtCountService.service.queryByAcc(acc_no), null));
			renderWithHeader(basePath+"/zone.html");
		}
	}
	public void write(){
		renderWithHeader(basePath+"/write.html");
	}
	/**
	 * 进入设置页面
	 */
	public void setting(){
		//查询该用户的资料
		QtAccount account =	getCurrentAccount();
		if(account != null){
			//查询用户的所有资料
			setAttr("account", QtAccountService.service.queryAccAndInfoForSetting(account.getInt("acc_no")));
			renderWithHeader(basePath+"/setting.html");
			return;
		}
		renderError(404);
	}
}
