package com.mypro.model.admin;

import java.util.List;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;

/**
 * 推荐内容列表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_rmd", pkName = "rmd_no")
public class QtRmd extends BaseModel<QtRmd> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtRmd dao = new QtRmd();
	public static final String all = "rmd_no,rmd_title,rmd_summary,rmd_url,rmd_img,rmd_seq,rmd_type,rmd_time,rmd_state";
	public static final String sql_query_all_rmd = "select " + all + " from qt_rmd_list";
	public static final String sql_query_rmd_by_type = "select " + all + " from qt_rmd where rmd_state = 1 and rmd_type = ? order by rmd_seq asc";
	public static final String sql_query_rmd_by_all = "select " + all + " from qt_rmd where rmd_state = 1 order by rmd_seq asc";
	
	/**
	 * 返回文章详情等页面推荐文章列表
	 * @param rmds
	 * @param ctx
	 * @return
	 */
	public static String getDetailRmdHtml(List<QtRmd> rmds){
		StringBuffer buffer = new StringBuffer();
		for (QtRmd rd : rmds)
			buffer.append("<li class=\"item\"><a href=\""+rd.getStr("rmd_url")+"\"><img data-original=\""+rd.get("rmd_img", "/static/f/images/rmd_no_img.gif")+"\" class=\"thumb\" style=\"display: block;\">"+rd.getStr("rmd_title")+"</a></li>");
		return buffer.toString();
	}
}
