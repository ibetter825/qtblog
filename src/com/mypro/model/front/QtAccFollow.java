package com.mypro.model.front;

import java.util.List;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;

/**
 * 用户关注表表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_acc_follow", pkName = "follow_acc,followed_acc")
public class QtAccFollow extends BaseModel<QtAccFollow> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtAccFollow dao = new QtAccFollow();
	public static final String all = "follow_acc,followed_acc,follow_time";
	
	public static final String sql_query_follows_select = "SELECT ai.acc_no, ai.acc_avatar, ai.nick_name, ( SELECT count(article_no) FROM qt_article WHERE acc_no = ai.acc_no AND article_state = 1 "
														+ " ) AS art_count, ( SELECT count(follow_acc) FROM qt_acc_follow WHERE followed_acc = ai.acc_no ) AS flwer_count ";
	public static final String sql_query_followers_except = " FROM qt_acc_follow af LEFT JOIN qt_account_info ai ON ai.acc_no = af.follow_acc ";//WHERE af.followed_acc = ?
	public static final String sql_query_following_except = " FROM qt_acc_follow af LEFT JOIN qt_account_info ai ON ai.acc_no = af.followed_acc ";//WHERE af.follow_acc = ?
	//第一个参数为目标用户，第二个参数为当前用户
	public static final String sql_query_main_followers = "select a.acc_no, a.acc_avatar, a.nick_name, a.art_count, a.acc_intro, a.flwer_count, f.follow_acc as is_follow from ( SELECT ai.acc_no, ai.acc_avatar, ai.nick_name, ai.acc_intro, ( SELECT count(article_no) FROM 	qt_article WHERE acc_no = ai.acc_no AND article_state = 1 ) AS art_count, ( SELECT 	count(follow_acc) FROM 	qt_acc_follow WHERE followed_acc = ai.acc_no ) AS flwer_count FROM qt_acc_follow af LEFT JOIN qt_account_info ai ON ai.acc_no = af.follow_acc WHERE af.followed_acc = ? ) a left join qt_acc_follow f on f.followed_acc = a.acc_no and f.follow_acc = ? limit ?,?";
	public static final String sql_query_zone_followers = "SELECT ai.acc_no, ai.acc_avatar, ai.nick_name, ai.acc_intro,( SELECT count(article_no) FROM 	qt_article WHERE acc_no = ai.acc_no AND article_state = 1 ) AS art_count, ( SELECT 	count(follow_acc) FROM 	qt_acc_follow WHERE followed_acc = ai.acc_no ) AS flwer_count, null as is_follow FROM qt_acc_follow af LEFT JOIN qt_account_info ai ON ai.acc_no = af.follow_acc WHERE af.followed_acc = ? limit ?,?";
	//查询目标用户正在关注的用户
	public static final String sql_query_nocurr_followings = "SELECT ai.acc_no, ai.acc_avatar, ai.nick_name, ai.acc_intro, ( SELECT count(article_no) FROM qt_article WHERE acc_no = ai.acc_no AND article_state = 1 ) AS art_count, ( SELECT count(follow_acc) FROM qt_acc_follow WHERE follow_acc = ai.acc_no ) AS flwer_count FROM qt_acc_follow af LEFT JOIN qt_account_info ai ON ai.acc_no = af.followed_acc WHERE af.follow_acc = ? limit ?,?";
	public static final String sql_query_withcurr_followings = "select a.acc_no, a.acc_avatar, a.nick_name, a.art_count, a.acc_intro, a.flwer_count, f.follow_acc as is_follow from ( SELECT ai.acc_no, ai.acc_avatar, ai.nick_name, ai.acc_intro, ( SELECT count(article_no) FROM qt_article WHERE acc_no = ai.acc_no AND article_state = 1 ) AS art_count, ( SELECT 	count(follow_acc) FROM 	qt_acc_follow WHERE follow_acc = ai.acc_no ) AS flwer_count FROM qt_acc_follow af LEFT JOIN qt_account_info ai ON ai.acc_no = af.followed_acc WHERE af.follow_acc = ? ) a left join qt_acc_follow f on f.followed_acc = a.acc_no and f.follow_acc = ? limit ?,?";
	
	/**
	 * 主页关注按钮
	 * @param follow 关注者，当前登录用户
	 * @param followed_acc_no 被关注的用户id
	 * @param is_followed 是否已经被关注
	 * @return
	 */
	public static String getFollowBtnHtml(Integer followed_acc_no, boolean is_followed){
		String btn = "btn-info", icon = "glyphicon-plus", text = "添加", title="添加";
		int status = 1;
		//查看用户是否已经关注过该用户
		if(is_followed){
			btn = "btn-success";
			icon = "glyphicon-ok";
			text = "正在";
			status = 0;
			title="取消";
		}
		String html = "<button title=\""+title+"关注\" class=\"btn btn-follow "+btn+"\" onclick=\"acc.follow('"+followed_acc_no+"',this);\" data-status=\""+status+"\"><i class=\"glyphicon "+icon+"\"></i><b>"+text+"关注</b></button><button class=\"opt-follow btn "+btn+"\"><i class=\"glyphicon glyphicon-align-justify\"></i></button>";
		return html;
	}
	/**
	 * 获取主页或者个人中心的粉丝的html标签
	 * @param followers
	 * @return
	 */
	public static String getFollowersHtml(List<QtAccFollow> followers){
		StringBuffer buffer = new StringBuffer("<div id=\"content-main\">");
		Object is_follow = null;
		String text = null, clz = null, btn = null, title = null;
		Integer acc_no = null;
		int data_status = 0;
		for (QtAccFollow f : followers){
			acc_no = f.getInt("acc_no");
			is_follow = f.get("is_follow");
			if(is_follow == null){
				text = title = "添加";
				clz = "glyphicon-plus";
				data_status = 1;
				btn = "btn-info";
			}else{
				text = "正在";
				title = "取消";
				btn = "btn-success";
				clz = "glyphicon-ok";
				data_status = 0;
			}
			buffer.append("<ul class=\"flow\"><li class=\"flow-li\"><img src=\""+f.getStr("acc_avatar")+"\"/></li>");
			buffer.append("<li class=\"flow-li\"><span><a class=\"flow-user-name\" href=\"/main/"+acc_no+"\">"+f.getStr("nick_name")+"</a>");
			buffer.append("<a class=\"flow-link\" href=\"/main/"+acc_no+"\">文章:"+f.getLong("art_count")+"</a><i>|</i><a class=\"flow-link\" href=\"#\">粉丝:"+f.getLong("flwer_count")+"</a>");
			buffer.append("</span><span>"+f.getStr("acc_intro")+"</span></li>");
			buffer.append("<li class=\"flow-li flow-li-btn\"><button title=\""+title+"关注\" class=\"btn btn-follow "+btn+"\" onclick=\"acc.follow('"+acc_no+"',this);\" data-status=\""+data_status+"\"><i class=\"glyphicon "+clz+"\"></i><b> "+text+"关注</b></button><button class=\"opt-follow btn "+btn+"\"><i class=\"glyphicon glyphicon-align-justify\"></i></button></li></ul>");
		}
		buffer.append("</div>");
		return buffer.toString();
	}
	/**
	 * 获取主页或者个人中心，点击粉丝或关注时的ctitle
	 * @param ctx
	 * @param acc_no
	 * @param active 当前选中的标签
	 * @return
	 */
	public static String getMainZoneFrontTitleHtml(Integer acc_no, String active){
		String route = "zone";
		String no = null;
		if(acc_no != null){
			route = "main";
			no = acc_no.toString();
		}else
			no = "";
		String fer = "", fng = "";
		if("follower".equals(active))
			fer = " title-tag-active";
		else if("following".equals(active))
			fng = " title-tag-active";
		return "<div id=\"content-title\" class=\"title flw-title\"><span class=\"title-tag"+fer+"\"><a class=\"pjax\" href=\"/"+route+"/follower/"+no+"\">粉丝</a></span><span class=\"title-tag"+fng+"\"><a class=\"pjax\" href=\"/"+route+"/following/"+no+"\">关注</a></span></div>";
	}
}
