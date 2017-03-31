package com.mypro.service.front;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.context.QtAccountContext;
import com.mypro.model.PagerModel;
import com.mypro.model.front.QtAccount;
import com.mypro.model.front.QtArtComt;
import com.mypro.model.front.QtArticle;
import com.mypro.model.front.QtComtReply;

public class QtCommentService {
	public final static QtCommentService service = new QtCommentService();
	
	public Map<String, Object> queryQtArtComtAndReplyByArtNo(PagerModel pager, String art_no){
		//先查询评论前5条，
		List<QtArtComt> comts = QtArtComt.dao.find(QtArtComt.sql_comt_acc, art_no, (pager.getCurPage() - 1)*pager.getPageSize(), pager.getPageSize());
		String nos = "";
		for (QtArtComt c : comts) 
			nos += c.getLong("comt_no")+",";
		//再通过评论，查询评论下的回复，前5条
		//List<QtComtReply> replies = QtComtReply.dao.find(QtComtReply.sql_comt_replies + nos + ")", 5);//sql语句什么的还得想个办法统一管理得好
		List<QtComtReply> replies = new ArrayList<QtComtReply>();
		if(nos != ""){
			nos = nos.substring(0,nos.length() - 1);
			replies = QtComtReply.dao.find(QtComtReply.sql_comt_replies_without_limit + nos + ")	ORDER BY r.add_time");
		}
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("comts", comts);
		res.put("replies", replies);
		res.put("rtime", ToolDateTime.getDateByTime());
		QtAccount current = QtAccountContext.getCurrentQtAccount();
		Integer raccno = current == null?null:current.getInt("acc_no");
		res.put("raccno", raccno);
		return res;
	}
	public Long saveQtArtComt(QtArtComt comt){
		boolean res = comt.save();
		if(res)
			return comt.getLong("comt_no");
		else
			return null; 
	}
	public boolean removeQtArtComt(String comt_no){
		//只能是评论者以及文章的作者才能删除
		QtAccount current = QtAccountContext.getCurrentQtAccount();
		Integer raccno = current.getInt("acc_no");
		QtArtComt comt = QtArtComt.dao.findFirst("select comt_no, art_no, from_acc from qt_article_comt where comt_no = ?", comt_no);
		boolean res = false;
		//数据表中设置过的，当删除评论时，评论相关的回复也会删除掉不需要另作处理
		if(comt != null){
			if(comt.getInt("from_acc").equals(raccno))//只能评论者才能删除
				res = comt.delete();
			else {//或者文章作者才能删除
				QtArticle article = QtArticle.dao.findFirst("select acc_no from qt_article where article_no = ?", comt.getInt("art_no")); 
				if(article.getInt("acc_no").equals(raccno))
					res = comt.set("comt_no", comt_no).delete();
			}
		}
		return res;
	}
}
