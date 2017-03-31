package com.mypro.web.controller.front;

import java.util.List;
import com.jfinal.aop.Clear;
import com.mypro.annotation.Controller;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.model.PagerModel;
import com.mypro.model.front.QtAccFollow;
import com.mypro.model.front.QtAccount;
import com.mypro.service.front.QtAccFollowService;
import com.mypro.web.admin.BaseController;
/**
 * 关注
 * @author ibett
 *
 */
@Controller(controllerKey = "/follow")
public class FollowController extends BaseController {
	/**
	 * 点赞,或取消点赞，同一篇文章每个人每天最多点击5次
	 */
	public void toggle(){
		Integer acc_no = getParaToInt(0);//被关注的用户编号
		int flag = getParaToInt(1, -1);//获取操作,0:取消关注,1:关注
		QtAccount account = getCurrentAccount();//关注者，当前登录用户
		boolean l = false;
		
		QtAccFollow follow = new QtAccFollow().set("follow_acc", account.get("acc_no")).set("followed_acc", acc_no).set("follow_time", ToolDateTime.getDateByTime());
		if(flag == 1)
			l = QtAccFollowService.service.saveQtAccFollow(follow);
		else if(flag == 0)
			l = QtAccFollowService.service.removeQtAccFollow(follow);
		String html = null;
		if(l){
			if(flag == 1)//关注成功
				html = QtAccFollow.getFollowBtnHtml(acc_no, true);
			else
				html = QtAccFollow.getFollowBtnHtml(acc_no, false);
		}else{
			if(flag == 1)//关注失败
				html = QtAccFollow.getFollowBtnHtml(acc_no, false);
			else
				html = QtAccFollow.getFollowBtnHtml(acc_no, true);
		}
		renderHtml(html);
	}
	/**
	 * 查询用户的粉丝，如果目标用户与当前登录用户的id一样
	 */
	@Clear(FrontContextInterceptor.class)
	public void follower(){
		Integer acc_no = getParaToInt(0);//被关注的用户编号
		if(acc_no == null)
			renderHtml("");
		QtAccount account = getCurrentAccount();//当前登录的用户
		Integer cur_acc = null;
		if(account != null)
			cur_acc = account.getInt("acc_no");
		int cur_page = getParaToInt(1, 1);
		int page_size = getParaToInt(2, 10);
		PagerModel pager = new PagerModel();
		pager.setCurPage(cur_page);
		pager.setPageSize(page_size);
		/*QueryModel rq = new QueryModel();
		rq.set("af.followed_acc", acc_no);*/
		List<QtAccFollow> page = QtAccFollowService.service.queryFollowers(pager, acc_no, cur_acc);
		renderHtml(QtAccFollow.getFollowersHtml(page));
	}
	/**
	 * 查询用户所关注的用户，如果目标用户与当前登录用户的id一样
	 */
	@Clear(FrontContextInterceptor.class)
	public void following(){
		Integer acc_no = getParaToInt(0);//被关注的用户编号
		if(acc_no == null)
			renderHtml("");
		QtAccount account = getCurrentAccount();//当前登录的用户
		Integer cur_acc = null;
		if(account != null)
			cur_acc = account.getInt("acc_no");
		int cur_page = getParaToInt(1, 1);
		int page_size = getParaToInt(2, 10);
		PagerModel pager = new PagerModel();
		pager.setCurPage(cur_page);
		pager.setPageSize(page_size);
		/*QueryModel rq = new QueryModel();
		rq.set("af.follow_acc", acc_no);*/
		List<QtAccFollow> page = QtAccFollowService.service.queryFollowing(pager, acc_no, cur_acc);
		renderHtml(QtAccFollow.getFollowersHtml(page));
	}
}
