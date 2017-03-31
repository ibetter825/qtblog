package com.mypro.model.front;

import java.util.List;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;

/**
 * 友情链接表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_link", pkName = "link_id")
public class QtLink extends BaseModel<QtLink> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtLink dao = new QtLink();
	
	public static final String all = "link_id,link_name,link_url,link_seq,link_type,link_desc";
	public static String getLinkHtml(List<QtLink> links){
		StringBuffer buffer = new StringBuffer("<div id=\"content-main\"><ul class=\"plinks\">");
		StringBuffer home = new StringBuffer("<li id=\"linkcat-47\" class=\"linkcat\"><h2>首页友链</h2><ul class=\"xoxo blogroll\">");//首页友链
		StringBuffer content = new StringBuffer("<li id=\"linkcat-47\" class=\"linkcat\"><h2>友请链接</h2><ul class=\"xoxo blogroll\">");//内容友链
		String li = null;
		for (QtLink link : links) {
			li = "<li><a href=\""+link.getStr("link_url")+"\" title=\""+link.getStr("link_desc")+"\" target=\"_blank\">"+link.getStr("link_name")+"</a></li>";
			if(link.getInt("link_type") == 0)
				home.append(li);
			else
				content.append(li);
		}
		buffer.append(home.append("</ul></li>").toString()).append(content.append("</ul></li>").toString()).append("</ul></div>");
		return buffer.toString();
	}
}
