package com.mypro.web.controller.front;

import java.util.List;
import com.jfinal.aop.Clear;
import com.jfinal.plugin.activerecord.Page;
import com.mypro.annotation.Controller;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.front.QtAccFollow;
import com.mypro.model.front.QtAccount;
import com.mypro.model.front.QtArticle;
import com.mypro.model.front.QtArticleCount;
import com.mypro.model.front.QtArticleType;
import com.mypro.service.front.QtAccFollowService;
import com.mypro.service.front.QtAccountService;
import com.mypro.service.front.QtArticleService;
import com.mypro.service.front.QtArticleTypeService;
import com.mypro.service.front.QtCountService;
import com.mypro.web.admin.BaseController;
/**
 * 个人主页
 * @author ibett
 *
 */
@Controller(controllerKey = "/main")
@Clear(FrontContextInterceptor.class)
public class MainController extends BaseController {
	private final static String basePath = "/WEB-INF/view/front";
	public void index(){
		Integer acc_no = getParaToInt(0);
		//查询该用户的资料
		if(acc_no != null){
			QtAccount account =	QtAccountService.service.queryAccAndInfoById(acc_no);
			if(account != null){
				String type = getPara(1, "t");//用户自定义分类,一般传过来的都是>=0的整数，查询全部的时候，修改传过来t
				int cur_page = getParaToInt(2, 1);
				int page_size = getParaToInt(3, 10);
				PagerModel pager = new PagerModel();
				pager.setCurPage(cur_page);
				pager.setPageSize(page_size);
				pager.setSortName("a.add_time");
				pager.setSortOrder("desc");
				QueryModel rq = new QueryModel();
				rq.set("a.article_state", 1);//只能查询状态正常的文章
				rq.set("a.article_flag", 0);//只能查询状态正常的文章
				rq.set("a.acc_no", acc_no);
				if(!type.equals("t"))
					rq.set("a.article_type[=]", type);
				Page<QtArticle> page = QtArticleService.service.queryQtArtByParams(pager, rq);
				List<QtArticle> articles = page.getList();
				String content = QtArticle.getArtcilesHtml(articles, false);
				//用户自定义分类
				String ctitle = "<h1 class=\"title\"><strong><a href=\""+getCtx()+"/main/"+acc_no+"-t\">所有文章</a></strong></h1>";
				//查询该用户关注数，查询该用户粉丝数，查询该用户文章数，收藏数，被点赞数
				String pagination = PagerModel.getPaginationHtml(getCtx()+"/main/"+acc_no+"-"+type, page, '-', true);
				if(isPjaxRequest()){//pjax请求
					renderHtml(ctitle+content+pagination);
				}else{
					String types = QtArticleType.getFrontMainTypesHtml(QtArticleTypeService.service.queryQtArtTypeByAcc(acc_no), acc_no);
					setAttr("account", account);
					setAttr("ctitle", ctitle);
					setAttr("content", content);
					setAttr("types", types);//这里的类型要改变
					setAttr("count", QtArticleCount.getFrontCountHtml(QtCountService.service.queryByAcc(acc_no), acc_no));
					setAttr("pagination", pagination);
					String follow = null;
					account = getCurrentAccount();
					if(account != null){
						Integer follow_acc = account.getInt("acc_no");
						if(!follow_acc.equals(acc_no))
							follow = QtAccFollow.getFollowBtnHtml(acc_no, QtAccFollowService.service.queryById(follow_acc, acc_no) != null);
					}else
						follow = QtAccFollow.getFollowBtnHtml(acc_no, false);
					setAttr("follow", follow);
					renderWithHeader(basePath+"/main.html");
				}
				return;
			}
		}
		renderError(404);
	}
	/**
	 * 个人中心粉丝
	 */
	public void follower(){
		Integer acc_no = getParaToInt(0);
		//查询该用户的资料
		if(acc_no != null){
			QtAccount account = QtAccountService.service.queryAccAndInfoById(acc_no);//目标用户
			int cur_page = getParaToInt(1, 1);
			int page_size = getParaToInt(2, 10);
			PagerModel pager = new PagerModel();
			pager.setCurPage(cur_page);
			pager.setPageSize(page_size);
			QueryModel rq = new QueryModel();
			rq.set("af.followed_acc", acc_no);
			QtAccount cur = getCurrentAccount();
			Integer cur_no = cur == null ? null : cur.getInt("acc_no");
			List<QtAccFollow> page = QtAccFollowService.service.queryFollowers(pager, acc_no, cur_no);//用户的
			String content = QtAccFollow.getFollowersHtml(page);
			String ctitle = QtAccFollow.getMainZoneFrontTitleHtml(acc_no, "follower");
			
			if(isPjaxRequest()){
				renderHtml(ctitle+content);
			}else{
				//用户自定义分类
				String types = QtArticleType.getFrontMainTypesHtml(QtArticleTypeService.service.queryQtArtTypeByAcc(acc_no), acc_no);
				setAttr("account", account);
				setAttr("types", types);//这里的类型要改变
				setAttr("content", content);
				setAttr("ctitle", ctitle);
				setAttr("count", QtArticleCount.getFrontCountHtml(QtCountService.service.queryByAcc(acc_no), acc_no));
				account = getCurrentAccount();
				String follow = null;
				if(account != null){
					Integer follow_acc = account.getInt("acc_no");
					if(!follow_acc.equals(acc_no))
						follow = QtAccFollow.getFollowBtnHtml(acc_no, QtAccFollowService.service.queryById(follow_acc, acc_no) != null);
				}else
					follow = QtAccFollow.getFollowBtnHtml(acc_no, false);
				setAttr("follow", follow);
				renderWithHeader(basePath+"/main.html");
			}
		}
	}
	/**
	 * 个人主页用户关注
	 */
	public void following(){
		Integer acc_no = getParaToInt(0);
		//查询该用户的资料
		if(acc_no != null){
			QtAccount account = QtAccountService.service.queryAccAndInfoById(acc_no);//目标用户
			int cur_page = getParaToInt(1, 1);
			int page_size = getParaToInt(2, 10);
			PagerModel pager = new PagerModel();
			pager.setCurPage(cur_page);
			pager.setPageSize(page_size);
			/*QueryModel rq = new QueryModel();
			rq.set("af.follow_acc", acc_no);*/
			QtAccount cur = getCurrentAccount();
			Integer cur_no = cur == null ? null : cur.getInt("acc_no");
			List<QtAccFollow> page = QtAccFollowService.service.queryFollowing(pager, acc_no, cur_no);//用户的
			String content = QtAccFollow.getFollowersHtml(page);
			String ctitle = QtAccFollow.getMainZoneFrontTitleHtml(acc_no, "following");
			if(isPjaxRequest()){
				renderHtml(ctitle+content);
			}else{
				//用户自定义分类
				String types = QtArticleType.getFrontMainTypesHtml(QtArticleTypeService.service.queryQtArtTypeByAcc(acc_no), acc_no);
				setAttr("types", types);//这里的类型要改变
				setAttr("account", account);
				setAttr("content", content);
				setAttr("ctitle", ctitle);
				setAttr("count", QtArticleCount.getFrontCountHtml(QtCountService.service.queryByAcc(acc_no), acc_no));
				account = getCurrentAccount();
				String follow = null;
				if(account != null){
					Integer follow_acc = account.getInt("acc_no");
					if(!follow_acc.equals(acc_no))
						follow = QtAccFollow.getFollowBtnHtml(acc_no, QtAccFollowService.service.queryById(follow_acc, acc_no) != null);
				}else
					follow = QtAccFollow.getFollowBtnHtml(acc_no, false);
				setAttr("follow", follow);
				renderWithHeader(basePath+"/main.html");
			}
		}
	}
}
