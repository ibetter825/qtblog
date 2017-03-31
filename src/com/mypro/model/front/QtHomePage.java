package com.mypro.model.front;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.model.BaseModel;

/**
 * 首页数据与配置表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_home_page", pkName = "hp_no")
public class QtHomePage extends BaseModel<QtHomePage> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtHomePage dao = new QtHomePage();
	
	public static final String all = "hp_no,hp_type,hp_title,hp_img,hp_uri,hp_seq";

	/**
	 * 获取首页各部分数据
	 * @param hbdata
	 * @param ctx
	 * @return
	 */
	public static Map<String, String> getHomePageDataHtml(Map<String, List<QtHomePage>> hbdata){
		Iterator<String> it = hbdata.keySet().iterator();
		String key = null;
		List<QtHomePage> val = null;
		QtHomePage hp = null;
		Map<String, String> res = new HashMap<String, String>();
		while (it.hasNext()) {
			key = it.next();
			val = hbdata.get(key);
			if(key.equals("slide")){
				StringBuffer ol = new StringBuffer();
				StringBuffer div = new StringBuffer();
				for(int i = 0, l = val.size(); i < l; i++){
					hp = val.get(i);
					if(i == 0){
						ol.append("<li data-target=\"#slider\" data-slide-to=\""+i+"\" class=\"active\"></li>");
						div.append("<div class=\"item active\">");
					}else{
						ol.append("<li data-target=\"#slider\" data-slide-to=\""+i+"\"></li>");
						div.append("<div class=\"item\">");
					}
					div.append("<a target=\"_blank\" href=\""+hp.getStr("hp_uri")+"\">");
					div.append("<img src=\""+hp.getStr("hp_img")+"\">");
					div.append("<span class=\"carousel-caption\">"+hp.getStr("hp_title")+"</span>");
					div.append("<span class=\"carousel-caption\">"+hp.getStr("hp_title")+"</span><span class=\"carousel-bg\"></span></a></div>");
				}
				res.put("slideol", ol.toString());
				res.put("slidediv", div.toString());
			}else if(key.equals("large")){
				StringBuffer li = new StringBuffer();
				if(val.size() > 0){
					hp = val.get(0);
					li.append("<li class=\"large\"><a target=\"_blank\" href=\""+hp.getStr("hp_uri")+"\">");
					li.append("<img class=\"thumb\" data-original=\""+hp.getStr("hp_img")+"\">");
					li.append("<h4>"+hp.getStr("hp_title")+"</h4></a></li>");
				}
				res.put("large", li.toString());
			}else if(key.equals("focus")){
				StringBuffer li = new StringBuffer();
				for(int i = 0, l = val.size(); i < l; i++){
					hp = val.get(i);
					li.append("<li><a target=\"_blank\" href=\""+hp.getStr("hp_uri")+"\">");
					li.append("<img class=\"thumb\" data-original=\""+hp.getStr("hp_img")+"\">");
					li.append("<h4>"+hp.getStr("hp_title")+"</h4></a></li>");
				}
				res.put("focus", li.toString());
			}else if(key.equals("hot")){
				StringBuffer li = new StringBuffer();
				for(int i = 0, l = val.size(); i < l; i++){
					hp = val.get(i);
					li.append("<li class=\"item\">");
					li.append("<a href=\""+hp.getStr("hp_uri")+"\">");
					li.append("<img data-original=\""+hp.getStr("hp_img")+"\" class=\"thumb\"/>");
					li.append("<span>"+hp.getStr("hp_title")+"</span></a></li>");
				}
				res.put("hot", li.toString());
			}
		}
		return res;
	}
	/**
	 * 获取首页一周热门排行的html
	 * @param arts
	 * @param ctx
	 * @return
	 */
	public static String getHomePageRanksHtml(List<QtArticle> arts){
		StringBuffer li = new StringBuffer();
		QtArticle article = null;
		int stc = 0, article_no = 0;
		long time = 0;
		String url = null;
		for(int i = 0, l = arts.size(); i < l; i++){
			article = arts.get(i);
			time = article.getLong("add_time");
			stc = article.getInt("article_static");
			article_no = article.getInt("article_no");
			if(stc == 1)
				url = QtArticle.getStaticLinkUrl(time, article_no);
			else
				url = "/article/detail/"+article_no;
			li.append("<li><p class=\"text-muted\">");
			li.append("<span class=\"post-comments\"><i class=\"glyphicon glyphicon-comment\"></i> 评论 ("+article.getInt("comt_count")+")</span>");
			li.append("<span class=\"post-views\"><i class=\"glyphicon glyphicon-eye-open\"></i> 阅读 ("+article.getInt("scan_count")+")</span></p>");
			li.append("<span class=\"label label-"+(i+1)+"\">"+(i+1)+"</span>");
			li.append("<a target=\"_blank\" href=\""+url+"\" title=\""+article.getStr("article_title")+"\">"+article.getStr("article_title")+"</a></li>");
		}
		if(li.length() < 1)
			li.append("<li>暂无文章！</li>");
		return li.toString();
	}
	/**
	 * 首页和其他页面的随机推荐文章html
	 * @param arts
	 * @param ctx
	 * @return
	 */
	public static String getHomePageRandomsHtml(List<QtArticle> arts){
		StringBuffer buffer = new StringBuffer();
		int stc = 0, article_no = 0;
		long time = 0;
		String img = null, url = null;
		for (QtArticle art : arts) {
			time = art.getLong("add_time");
			stc = art.getInt("article_static");
			article_no = art.getInt("article_no");
			if(stc == 1)
				url = QtArticle.getStaticLinkUrl(time, article_no);
			else
				url = "/article/detail/"+article_no;
					
			buffer.append("<li><a href=\""+url+"\">");
			img = art.get("article_imgs");
			if(img == null)
				img = "/static/f/images/no_img.gif";
			else
				img = img.split(";")[0];
			buffer.append("<span class=\"thumbnail\"><img data-original=\""+img+"\" class=\"thumb\" style=\"display: inline;\"></span>");
			buffer.append("<span class=\"text\">"+art.getStr("article_title")+"</span>");
			buffer.append("<span class=\"text-muted\">"+ToolDateTime.fromToday(time)+"</span></a></li>");
		}
		return buffer.toString();
	}
}
