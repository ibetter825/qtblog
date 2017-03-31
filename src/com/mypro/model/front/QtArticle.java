package com.mypro.model.front;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.jfinal.kit.PropKit;
import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.model.BaseModel;
import com.mypro.model.admin.SysDict;

/**
 * 文章表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_article", pkName = "article_no")
public class QtArticle extends BaseModel<QtArticle> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtArticle dao = new QtArticle();
	public static final String all = "article_no,acc_no,article_title,article_summary,article_content,add_time,article_theme,article_type,article_imgs,article_tags,article_tag_ids,article_source,letter_paper,article_state,article_flag,article_from,article_static";
	public static final String simple = "article_no,acc_no,article_title,article_summary,article_content";
	public static final String allwithacc = getPrefixFields(new String[]{all,QtAccount.simple,SysDict.noandname}, new String[]{"art","acc","dict"});
	public static final String sql_query_article_by_acc = "select a.article_no,a.acc_no,a.article_title,a.article_summary,a.add_time,a.article_static,a.article_imgs,a.article_state,ac.comt_count from qt_article a left join qt_article_count ac on ac.article_no = a.article_no where a.article_state > -1 and a.acc_no = ? order by a.update_time desc limit ?,?;";
	public static final String sql_query_article_by_acc_select = "select a.article_no,a.acc_no,ai.nick_name,a.article_title,a.article_summary,a.add_time,a.article_static,a.update_time,a.article_imgs,a.article_type,a.article_state,d.dict_name,d.dict_no,ac.scan_count,ac.like_count,a.article_tags,a.article_tag_ids,ac.comt_count,t.type_name ";
	public static final String sql_query_article_by_acc_except = " from qt_article a left join qt_article_count ac on ac.article_no = a.article_no left join sys_dict d on a.article_theme = d.dict_no left join qt_art_type t on t.type_id = a.article_type left join qt_account_info ai on ai.acc_no = a.acc_no ";
	
	public static final String sql_query_article_by_count_select = "select a.article_no,a.acc_no,a.article_title,a.article_summary,a.add_time,a.article_imgs ";
	public static final String sql_query_article_by_count_except = " from qt_article a left join qt_article_count c on a.article_no = c.article_no ";
	public static final String sql_query_article_by_comt_count_select = "select a.article_no,a.article_title,a.add_time,ac.comt_count ";
	public static final String sql_query_article_by_comt_count_except = "from qt_article a left join qt_article_count ac on ac.article_no = a.article_no where a.article_state = 1";
	
	/**
	 * 获取文章静态化后的地址
	 * @return
	 */
	public static String getStaticLinkUrl(long add_time, int art_no){
		Integer[] node = ToolDateTime.getTimeNode(add_time);
		//目录结构为  /article/2016/10
		String htmlDefaultDir = "/" + PropKit.get(DictKeys.html_dir_name);
		String htmlRelativeUrl = htmlDefaultDir + "/" + (node[0] < 10 ? "0" + node[0] : node[0]) + "/" + (node[1] < 10 ? "0" + node[1] : node[1]) + "/";
		//名字为 no.html
		String htmlName = art_no+".html";//这样的话可能有重复,管他的
		return htmlRelativeUrl + htmlName;
	}
	/**
	 * 获取文章列表
	 * @param articles
	 * @param ctx
	 * @param self
	 * @return
	 */
	public static String getArtcilesHtml(List<QtArticle> articles, boolean self){
		StringBuffer buffer = new StringBuffer("<div id=\"content-main\">");
		if(articles.size() == 0)
			buffer.append("<article class=\"excerpt excerpt-one\"><p>没有结果!</p></article>");
		else{
			for (QtArticle art : articles)
				buffer.append(getArticleHtml(art, self, false, false, null));
		}
		buffer.append("</div>");
		return buffer.toString();
	}
	/**
	 * 获取收藏文章列表
	 * @param articles
	 * @param ctx
	 * @param self
	 * @return
	 */
	public static String getColectArtcilesHtml(List<QtArticle> articles, boolean self){
		StringBuffer buffer = new StringBuffer("<div id=\"content-main\">");
		if(articles.size() == 0)
			buffer.append("<article class=\"excerpt excerpt-one\"><p>没有结果!</p></article>");
		else{
			for (QtArticle art : articles)
				buffer.append(getArticleHtml(art, self, true, false, null));
		}
		buffer.append("</div>");
		return buffer.toString();
	}
	/**
	 * 检索文章列表
	 * @param articles
	 * @param ctx
	 * @param key
	 * @return
	 */
	public static String getSearchArtcilesHtml(List<QtArticle> articles, String key){
		StringBuffer buffer = new StringBuffer("<div id=\"content-main\">");
		if(articles.size() == 0)
			buffer.append("<article class=\"excerpt excerpt-one\"><p>没有结果!</p></article>");
		else{
			for (QtArticle art : articles)
				buffer.append(getArticleHtml(art, false, false, false, key));
		}
		buffer.append("</div>");
		return buffer.toString();
	}
	/**
	 * 获取回收站文章
	 * @return
	 */
	public static String getTrashArticlesHtml(List<QtArticle> articles){
		StringBuffer buffer = new StringBuffer("<div id=\"content-main\">");
		if(articles.size() == 0)
			buffer.append("<article class=\"excerpt excerpt-one\"><p>没有结果!</p></article>");
		else{
			for (QtArticle art : articles)
				buffer.append(getArticleHtml(art, true, false, true, null));
		}
		buffer.append("</div>");
		return buffer.toString();
	}
	/**
	 * 个人主页/空间文章
	 * @param art 文章
	 * @param ctx 根路径
	 * @param self 是否是文章作者
	 * @param colet 是否是收藏
	 * @return
	 */
	public static String getArticleHtml(QtArticle art, boolean self, boolean colet, boolean trash, String key){
		String str = null, css = "multi", dict_no = art.get("dict_no", null); int article_no = art.getInt("article_no");
		long add_time = art.getLong("add_time");
		int art_static = art.getInt("article_static");
		if(dict_no == null)
			dict_no = "";
		else
			dict_no = "<a class=\"cat label label-important\" href=\"/"+dict_no+"\"+>" + art.get("dict_name", "") + "<i class=\"label-arrow\"></i></a> ";
		String title = art.getStr("article_title"), summary = art.get("article_summary", "");
		if(key != null){
			title = title.replaceAll("(?i)"+key, "<span>"+key.toUpperCase()+"</span>");
			//summary = summary.replaceAll("(?i)"+key, "<span style=\"color:#FF5E52;\">"+key+"</span>");
		}
		String a = "/article/detail/"+article_no;
		if(art_static == 1)
			a = getStaticLinkUrl(add_time, article_no);
		String[] arr = null;
		StringBuffer buffer = new StringBuffer();
		int lth = 0;
		str = art.getStr("article_imgs");
		if(StringUtils.isNotEmpty(str) && !colet){
			arr = str.split(";");
			lth = arr.length;
			if(lth >= 4)
				css = "multi";
			else
				css = "one";
		}
		buffer.append("<article class=\"excerpt excerpt-"+css+"\"><header>");
		if(!colet)
			buffer.append(dict_no);
		buffer.append("<h2><a href=\""+a+"\" title=\""+art.getStr("article_title")+"\">"+title+"</a></h2>");
		
		if(arr != null && !colet)
			buffer.append("<small class=\"text-muted\"><span class=\"glyphicon glyphicon-picture\"></span>"+lth+"</small>");
		buffer.append("</header><p class=\"text-muted\">");
		buffer.append("<a href=\"/main/"+art.getInt("acc_no")+"\">"+art.get("nick_name","")+" </a>");
		if(!trash)
			buffer.append("发布于 "+ToolDateTime.getDate(add_time, ToolDateTime.pattern_ymd_hms));
		else
			buffer.append("删除于 "+ToolDateTime.getDate(art.getLong("update_time"), ToolDateTime.pattern_ymd_hms));
		if(self){
			if(!colet && !trash)
				buffer.append("<a class=\"art-opt\" href=\"/article/edit/"+article_no+"\" title=\"编辑\"><i class=\"glyphicon glyphicon-edit\"></i></a><a class=\"art-opt\" href=\"javascript:void(0);\" onclick=\"art.remove('"+article_no+"', this);\" title=\"删除\"><i class=\"glyphicon glyphicon-trash\"></i></a>");
			else if(!trash && colet)
				buffer.append("<a class=\"art-opt\" href=\"javascript:void(0);\" onclick=\"art.colet('"+article_no+"', 0, this);\" title=\"取消收藏\"><i class=\"glyphicon glyphicon-remove-circle\"></i></a>");
			else
				buffer.append("<a class=\"art-opt\" href=\"javascript:void(0);\" onclick=\"art.state('"+article_no+"', 1, this);\" title=\"恢复\"><i class=\"glyphicon glyphicon-share-alt\"></i></a>");
		}
		
		buffer.append("</p><p class=\"focus\">");
		if(arr != null && !colet){//图片
			buffer.append("<a href=\""+a+"\" class=\"thumbnail\">");
			if(lth >= 4){
				for(int i = 0; i < 4; i++)
					buffer.append("<span class=\"item\"><span class=\"thumb-span\"><img data-original=\""+arr[i]+"\" class=\"thumb\"/></span></span>");
			}else
				buffer.append("<span class=\"item\"><span class=\"thumb-span\"><img data-original=\""+arr[0]+"\" class=\"thumb\"/></span></span>");
			buffer.append("</a>");
		}
		buffer.append("</a></p>");
		buffer.append("<p class=\"note\">"+summary+"...</p>");
		buffer.append("<p class=\"text-muted views\"><span class=\"post-views\"><i class=\"glyphicon glyphicon-eye-open\"></i> 阅读 ("+art.get("scan_count", 0)+")</span><span class=\"post-comments\"><i class=\"glyphicon glyphicon-comment\"></i> 评论 ("+art.get("comt_count", 0)+")</span><span class=\"post-like\"><i class=\"glyphicon glyphicon-thumbs-up\"></i>赞 ("+art.get("like_count", 0)+")</span>");
		if(self)
			buffer.append("<span class=\"post-type\"><i class=\"glyphicon glyphicon-list-alt\"></i>分类：" + art.get("type_name", "未分类") + "</span>");
		str = art.getStr("article_tags");//标签
		if(StringUtils.isNotEmpty(str)){
			arr = str.split(",");
			lth = arr.length;
			String[] ids = art.getStr("article_tag_ids").split(",");
			buffer.append("<span class=\"post-tags\"><i class=\"glyphicon glyphicon-tags\"></i>标签：");
			for(int i = 0; i < lth; i++){
				buffer.append("<a href=\"/tag/"+ids[i]+"\" rel=\"tag\">"+arr[i]+"</a>");
				if(i != lth - 1)
					buffer.append(" / ");
			}
			buffer.append("</span>");
		}
		buffer.append("</p></article>");
		return buffer.toString();
	}
	/**
	 * detail页面的点赞，收藏等操作的html
	 * @param count
	 * @return
	 */
	public static String getArticleOptsHtml(QtArticleCount count){
		StringBuffer buffer = new StringBuffer();
		String _like = "action-before", _colet = "action-before";// _rept = "action-before";转载的功能延后
		if(count != null){
			if(count.get("_like", null) != null)
				_like = "action-actived";
			if(count.get("_colet", null) != null)
				_colet = "action-actived";
			buffer.append("<a href=\"javascript:void(0);\" class=\"action "+_like+"\" data-pid=\""+count.getInt("article_no")+"\" data-event=\"like\"><i class=\"glyphicon glyphicon-thumbs-up\"></i>赞 (<span>"+count.get("like_count",0)+"</span>)</a>");
			buffer.append("<a href=\"javascript:void(0);\" class=\"action "+_colet+"\" data-pid=\""+count.getInt("article_no")+"\" data-event=\"colet\"><i class=\"glyphicon glyphicon-heart-empty\"></i>收藏 (<span>"+count.get("colet_count",0)+"</span>)</a>");
			buffer.append("<a class=\"post-linkto action\" href=\"#comment\"><i class=\"glyphicon glyphicon-comment\"></i>评论 (<span>"+count.get("comt_count",0)+"</span>)</a>");
		}
		return buffer.toString();
	}
}
