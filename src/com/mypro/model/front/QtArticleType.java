package com.mypro.model.front;

import java.util.List;
import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;

/**
 * 用户自定义文章类别表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_art_type", pkName = "type_id")
public class QtArticleType extends BaseModel<QtArticleType> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtArticleType dao = new QtArticleType();
	public static final String all = "type_id,type_name,acc_no,add_time,type_state";
	
	//查询用户自定义分类，并统计各分类下的文章数
	public static final String sql_query_art_type_by_acc = "(select t.type_id,t.type_name,a.article_type, count(a.article_no) as article_count from qt_art_type t left join qt_article a on t.type_id = a.article_type and a.article_state = 1 where t.acc_no = ?  group by t.type_id,t.type_name order by t.add_time desc)"
														+ " union all (select t.type_id,t.type_name,a.article_type, count(a.article_no) as article_count from qt_article a left join qt_art_type t on t.type_id = a.article_type where a.article_state = 1 and a.article_type = 0 and a.acc_no = ? group by t.type_id,t.type_name)";
	
	public static final String sql_query_art_type = "select " + all + " from qt_art_type where type_state = 1 ";
	
	/**
	 * 用户write页面的文章分类,如果传入的type_id = -1则表示不是修改文章
	 * @param types
	 * @param type_id
	 * @return
	 */
	public static String getFrontOptions(List<QtArticleType> types, int type_id){
		StringBuffer str = new StringBuffer();
		StringBuffer res = new StringBuffer("<div id=\"type-choosed\" class=\"select-choosed\"><span>");
		int id = 0; String selected = null, name = null, choosed = "选择分类...";
		str.append("<ul id=\"type-options\" class=\"select-options\">");
		if(type_id == 0 || type_id == -1)
			selected = " selected";
		else
			selected = "";
		str.append("<li class=\"t-option"+selected+"\" data-no=\"0\">"+choosed+"</li>");
		for(int i = 0, l = types.size(); i < l; i++){
			selected = "";
			name = types.get(i).getStr("type_name");
			id = types.get(i).getInt("type_id");
			if(type_id != -1 && type_id == id){
				selected = " selected";
				choosed = name;
			}
			str.append("<li class=\"t-option"+selected+"\" data-no=\""+id+"\">"+name+"<i class=\"type-opt delete glyphicon glyphicon-trash\"></i><i class=\"type-opt edit glyphicon glyphicon-pencil\"></i></li>");
		}
		str.append("<li title=\"添加分类\" class=\"t-add\"><i class=\"type-opt add glyphicon glyphicon-plus\"></i></li>");
		res.append(choosed);
		res.append("</span><i class=\"glyphicon glyphicon-chevron-down\"></i></div>");
		str.append("</ul>");
		return res.append(str).toString();
    };
    /**
     * 个人中心文章分类，以及文章数量
     * @param types
     * @param type 当前选择的type
     * @return
     */
	public static String getFrontZoneTypesHtml(List<QtArticleType> types, int type){
		StringBuffer buffer = new StringBuffer("<div id=\"content-title\" class=\"title\"><strong><a class=\"pjax\" href=\"/zone/\" \">所有文章</a></strong>");
		String clz = null;
		int ty = 0;
		for (QtArticleType t : types){
			clz = "";
			ty = t.get("type_id", 0);
			if(ty == type)
				clz = " title-tag-active";
			buffer.append("<span class=\"title-tag"+clz+"\"><a class=\"pjax\" href=\"/zone/"+ty+"\">"+t.get("type_name", "未分类")+" ("+t.getLong("article_count")+")</a></span>");
		}
		buffer.append("</div>");
		return buffer.toString();
	}
	/**
     * 个人主页文章分类，以及文章数量
     * @param types
     * @return
     */
	public static String getFrontMainTypesHtml(List<QtArticleType> types, Integer acc_no){
		if(types.size() == 0)
			return null;
		StringBuffer buffer = new StringBuffer("<ul class=\"user-content\">");
		for (QtArticleType t : types)
			buffer.append("<li><a class=\"pjax\" href=\"/main/"+acc_no+"-"+t.get("type_id", 0)+"\" ><i class=\"glyphicon glyphicon-book\"></i>"+t.get("type_name", "未分类")+"</a></li>");
		buffer.append("</ul>");
		return buffer.toString();
	}
}
