package com.mypro.model.front;

import org.apache.commons.lang.StringUtils;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;

/**
 * 名人名言表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_motto", pkName = "mto_id")
public class QtMotto extends BaseModel<QtMotto> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtMotto dao = new QtMotto();
	
	public static final String all = "mto_id,mto_title,mto_content,mto_author";
	public static final String sql_query_random_motto = "SELECT t1.mto_id, t1.mto_title, t1.mto_content, t1.mto_author FROM `qt_motto` AS t1 JOIN ( SELECT ROUND( RAND() * ( (SELECT MAX(mto_id) FROM `qt_motto`) - (SELECT MIN(mto_id) FROM `qt_motto`) ) + (SELECT MIN(mto_id) FROM `qt_motto`) ) AS id ) AS t2 WHERE t1.mto_id >= t2.id ORDER BY t1.mto_id LIMIT ?";
	
	public static String getSingleMottoHtml(QtMotto motto){
		StringBuffer html = new StringBuffer("<div class=\"widget widget_textads motto\"><a class=\"style02\" href=\"javascript:;/\"><strong>名人名言</strong>");
		String title = motto.getStr("mto_title");
		String content = motto.getStr("mto_content");
		String author = motto.getStr("mto_author");
		if(StringUtils.isNotEmpty(title))
			html.append("<h2>"+title+"</h2>");
		html.append("<p class=\"motto-content\">"+content+"</p>");
		html.append("<p class=\"motto-author\">—— "+author+"</p>");
		html.append("</a></div>");
		return html.toString();
	}
}
