package com.mypro.service.front;

import com.mypro.context.QtAccountContext;
import com.mypro.model.front.QtAccount;
import com.mypro.model.front.QtArticle;
import com.mypro.model.front.QtComtReply;

public class QtReplyService {
	public final static QtReplyService service = new QtReplyService();
	public Long saveQtComtReply(QtComtReply reply){
		boolean res = reply.save();
		if(res)
			return reply.getLong("reply_no");
		else
			return null;
	}
	public boolean removeQtComtReply(String reply_no){
		//只有回复的作者才能删除
		//或者回复所对应文章作者才能删除
		QtAccount current = QtAccountContext.getCurrentQtAccount();
		Integer raccno = current.getInt("acc_no");
		QtComtReply reply = QtComtReply.dao.findFirst("select r.comt_no, c.art_no, r.from_acc from qt_comt_reply r left join qt_article_comt c on c.comt_no = r.comt_no where reply_no = ?", reply_no);
		
		boolean res = false;
		//数据表中设置过的，当删除评论时，评论相关的回复也会删除掉不需要另作处理
		if(reply != null){
			if(reply.getInt("from_acc").equals(raccno))//只能评论者才能删除
				res = reply.delete();
			else {//或者文章作者才能删除
				QtArticle article = QtArticle.dao.findFirst("select acc_no from qt_article where article_no = ?", reply.getInt("art_no")); 
				if(article.getInt("acc_no").equals(raccno))
					res = reply.set("reply_no", reply_no).delete();
			}
		}
		return res;
	}
}
