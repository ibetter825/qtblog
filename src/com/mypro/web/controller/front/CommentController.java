package com.mypro.web.controller.front;

import java.util.Map;
import com.jfinal.aop.Clear;
import com.mypro.annotation.Controller;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.context.QtAccountContext;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.model.PagerModel;
import com.mypro.model.ResultModel;
import com.mypro.model.front.QtAccount;
import com.mypro.model.front.QtArtComt;
import com.mypro.model.front.QtComtReply;
import com.mypro.service.front.QtCommentService;
import com.mypro.service.front.QtReplyService;
import com.mypro.web.admin.BaseController;
/**
 * 个人中心
 * @author ibett
 *
 */
@Controller(controllerKey = "/comt")
public class CommentController extends BaseController {
	@SuppressWarnings("unused")
	private final static String basePath = "/WEB-INF/view/front";
	@Clear(FrontContextInterceptor.class)
	public void index(){
		String article_no = getPara(0);
		Integer cur_page = getParaToInt(1); 
		Integer page_size = getParaToInt(2);
		PagerModel pager = new PagerModel();
		pager.setCurPage(cur_page);
		pager.setPageSize(page_size);
		
		Map<String , Object> result = QtCommentService.service.queryQtArtComtAndReplyByArtNo(pager, article_no);
		renderJson(result);
	}
	/**
	 * 添加评论或回复
	 */
	@SuppressWarnings("unchecked")
	public void add(){
		Integer post_type = getParaToInt("post_type");
		Integer post_art_no = getParaToInt("post_art_no");
		Integer post_to_acc = getParaToInt("post_to_acc");
		QtAccount account = QtAccountContext.getCurrentQtAccount();
		Integer post_from_acc = account.getInt("acc_no");
		String post_from_avatar = account.getStr("acc_avatar");
		String post_from_name = account.getStr("nick_name");
		String post_content = getPara("content");
		ResultModel result = null;
		if(post_type == 1){//评论
			QtArtComt comt = new QtArtComt();
			comt.set("comt_content", post_content);
			comt.set("add_time", ToolDateTime.getDateByTime());
			comt.set("art_no", post_art_no);
			comt.set("from_acc", post_from_acc);
			comt.set("to_acc", post_to_acc);
			Long comt_no = QtCommentService.service.saveQtArtComt(comt);
			if(comt_no != null){
				result = new ResultModel();
				result.getData().put("rtime", ToolDateTime.getDateByTime());
				result.getData().put("dto", comt);
				result.getData().put("from_name", post_from_name);
				result.getData().put("from_avatar", post_from_avatar);
			}else
				result = new ResultModel("网络问题,请稍后再试");
		}else if(post_type == 2){//回复
			String post_comt_no = getPara("post_comt_no");
			QtComtReply reply = new QtComtReply();
			reply.set("reply_content", post_content);
			reply.set("add_time", ToolDateTime.getDateByTime());
			reply.set("comt_no", post_comt_no);
			reply.set("from_acc", post_from_acc);
			reply.set("to_acc", post_to_acc);
			Long reply_no = QtReplyService.service.saveQtComtReply(reply);
			if(reply_no != null){
				result = new ResultModel();
				result.getData().put("rtime", ToolDateTime.getDateByTime());
				result.getData().put("dto", reply);
				result.getData().put("from_name", post_from_name);
				result.getData().put("from_avatar", post_from_avatar);
			}else
				result = new ResultModel("网络问题,请稍后再试");
		}
		renderJson(result);
	}
	public void del(){
		//删除评论和回复
		String no = getPara(0);
		Integer type = getParaToInt(1);
		boolean res = false;
		if(type == 1)
			res = QtCommentService.service.removeQtArtComt(no);
		else if(type == 2)
			res = QtReplyService.service.removeQtComtReply(no);
		ResultModel result = null;
		if(res)
			result = new ResultModel();
		else
			result = new ResultModel("网络问题,请稍后再试");
		renderJson(result);
	}
}
