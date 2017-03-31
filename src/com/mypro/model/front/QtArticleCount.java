package com.mypro.model.front;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;

/**
 * 统计表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_article_count", pkName = "article_no")
public class QtArticleCount extends BaseModel<QtArticleCount> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtArticleCount dao = new QtArticleCount();
	public static final String all = "article_no,like_count,agst_count,rept_count,share_count,scan_count,colet_count";
	
	public static final String sql_query_by_pk = "select c.article_no,c.like_count,c.agst_count,c.rept_count,c.comt_count,c.colet_count,l.acc_no as _like,co.acc_no as _colet,r.acc_no as _rept from qt_article_count c left join qt_article_like l on c.article_no = l.art_no and l.acc_no = ?" 
												+ " left join qt_article_collect co on c.article_no = co.art_no and co.acc_no = ?"
												+ " left join qt_article_rept r on c.article_no = r.art_no and r.acc_no = ?"
												+ " where c.article_no = ?";
	public static final String sql_count_by_acc = "select count(ac.article_no) as article_count, sum(ac.like_count) as like_count, sum(ac.colet_count) as colet_count, sum(ac.rept_count) as rept_count, (select count(af.follow_acc) from qt_acc_follow af where af.followed_acc = a.acc_no) as flwer_count, (select count(af.followed_acc) from qt_acc_follow af where af.follow_acc = a.acc_no) as flwing_count from qt_article_count ac left join qt_article a on a.article_no = ac.article_no where a.article_state = 1 and a.acc_no = ?";
	
	/**
	 * 获取main或者zone中的统计数量
	 * @param count
	 * @param acc_no
	 */
	public static String getFrontCountHtml(QtArticleCount count, Integer acc_no){
        StringBuffer buffer = new StringBuffer();
        if(acc_no == null){//zone
        	buffer.append("<span><a class=\"pjax\" href=\"/zone/following/\">关注:"+count.get("flwing_count", 0)+"</a></span> <i>|</i> ");
        	buffer.append("<span><a class=\"pjax\" href=\"/zone/follower/\">粉丝:"+count.get("flwer_count", 0)+"</a></span><b><br/></b> <i class=\"hide\">|</i> </span>");
        	buffer.append("<span><a class=\"pjax\" href=\"/zone/\">文章数:"+count.get("article_count", 0)+"</a></span> <i>|</i> ");
        	buffer.append("<span><a class=\"pjax\" href=\"/zone/collect/\">收藏数:"+count.get("colet_count", 0)+"</a></span> <i>|</i> ");
        	buffer.append("<span>收获喜欢:"+count.get("like_count", 0)+"</span>");
        }else{
        	buffer.append("<span><a class=\"pjax\" href=\"/main/following/"+acc_no+"\">关注:"+count.get("flwing_count", 0)+"</a></span> <i>|</i> ");
        	buffer.append("<span><a class=\"pjax\" href=\"/main/follower/"+acc_no+"\">粉丝:"+count.get("flwer_count", 0)+"</a></span><b><br/></b> <i class=\"hide\">|</i> </span>");
        	buffer.append("<span><a class=\"pjax\" href=\"/main/"+acc_no+"\">文章数:"+count.get("article_count", 0)+"</a></span> <i>|</i> ");
        	buffer.append("<span>收获喜欢:"+count.get("like_count", 0)+"</span>");
        }
        return buffer.toString();
	}
}
