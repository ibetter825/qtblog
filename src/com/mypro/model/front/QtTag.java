package com.mypro.model.front;

import java.util.List;
import java.util.Map;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.model.BaseModel;

/**
 * 文章表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_tag", pkName = "tag_id")
public class QtTag extends BaseModel<QtTag> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtTag dao = new QtTag();
	
	public static final String all = "tag_id,tag_name,tag_num,add_time";
	public static final String sql_write_rmd = "(select distinct t.tag_id,t.tag_name,t.tag_num,t.add_time,0 as type from qt_art_tag a left join qt_tag t on t.tag_id = a.tag_id where a.acc_no = ? order by t.tag_num desc limit 0,5)"
											+ " union all (select distinct tag_id,tag_name,tag_num,add_time,1 as type from qt_tag order by tag_num desc limit 0,5) ";
	public static final String sql_query_article_by_tag_select = "select a.article_no,a.acc_no,a.article_title,a.article_summary,a.add_time,a.article_static,a.article_imgs,a.article_type,a.article_state,ai.nick_name,d.dict_name,d.dict_no,ac.scan_count,a.article_tags,a.article_tag_ids,(select count(c.comt_no) from qt_article_comt c where c.art_no = a.article_no) as comt_count,t.type_name ";
	public static final String sql_query_article_by_tag_except = " from qt_article a left join qt_article_count ac on ac.article_no = a.article_no left join sys_dict d on a.article_theme = d.dict_no left join qt_art_type t on t.type_id = a.article_type left join qt_art_tag tg ON tg.art_no = a.article_no left join qt_account_info ai on ai.acc_no = a.acc_no ";
	public static final String sql_query_tag_cloud_select = "SELECT t.tag_id,t.tag_name,t.tag_num,x.article_no,x.article_title,x.add_time,x.article_static ";
	public static final String sql_query_tag_cloud_except = " FROM qt_tag t LEFT JOIN ( SELECT atg.tag_id, a.article_no, a.article_title, a.add_time, a.article_static FROM qt_article a LEFT JOIN qt_art_tag atg ON atg.art_no = a.article_no WHERE a.article_state = 1 AND a.article_flag = 0 AND a.article_tag_ids + 0 > 0 GROUP BY atg.tag_id ORDER BY a.add_time DESC ) x ON x.tag_id = t.tag_id ORDER BY t.tag_num DESC";
	
	@SuppressWarnings("unchecked")
	public static String getWriteRmdTags(Map<String, Object> tags){
		List<String> oftens = (List<String>) tags.get("oftens"), hots = (List<String>) tags.get("hots");
		StringBuffer html = null;
		if(oftens != null){
			if(html == null) html = new StringBuffer("<ul class=\"recmd-container\">");
			html.append("<li class=\"rcmd-li\"><label>常用标签</label>");
			for (String t : oftens) 
				html.append("<span class=\"rcmd-tag\">"+t+"</span>");
			html.append("</li>");
		}
		if(hots != null){
			if(html == null) html = new StringBuffer("<ul class=\"recmd-container\">");
			html.append("<li class=\"rcmd-li\"><label>热门标签</label>");
			for (String t : hots) 
				html.append("<span class=\"rcmd-tag\">"+t+"</span>");
			html.append("</li>");
		}
		if(html != null)
			html.append("</ul>");
		else
			html = new StringBuffer();
		return html.toString();
	}
	/**
	 * 获取热门标签
	 * @return
	 */
	public static String getHotTagsHtml(List<QtTag> tags){
		StringBuffer buffer = new StringBuffer();
		for (QtTag t : tags)
			buffer.append("<li><a href=\"/tag/"+t.getInt("tag_id")+"\" data-original-title=\""+t.getInt("tag_num")+"篇文章\">"+t.getStr("tag_name")+"</a></li>");
		return buffer.toString();
	}
	/**
	 * 获取标签云
	 * @param tags
	 * @param ctx
	 * @return
	 */
	public static String getTagCloudHtml(List<QtTag> tags){
		StringBuffer buffer = new StringBuffer("<div id=\"content-main\">");
		if(tags.size() == 0)
			buffer.append("<article class=\"excerpt excerpt-one\"><p>没有结果!</p></article>");
		else{
			buffer.append("<ul class=\"tagslist\">");
			int stc = 0, art_no = 0;
			String a = null; long time = 0;
			for (QtTag t : tags){
				stc = t.getInt("article_static");
				time = t.getLong("add_time");
				art_no = t.getInt("article_no");
				if(stc == 0)
					a = "/article/detail/"+art_no;
				else
					a = QtArticle.getStaticLinkUrl(time, art_no);
				if(t.getInt("article_no") == null)
					continue;
				buffer.append("<li><a class=\"tagname pjax\" href=\"/tag/"+t.getInt("tag_id")+"\">"+t.get("tag_name", "")+"</a><strong>x "+t.getInt("tag_num")+"</strong><br><a href=\""+a+"\">"+t.getStr("article_title")+"</a><br><span class=\"muted\">"+ToolDateTime.getDate(time, ToolDateTime.pattern_ymd)+"</span></li>");
			}
			buffer.append("</ul>");
		}
		buffer.append("</div>");
		return buffer.toString();
	}
}
