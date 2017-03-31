package com.mypro.model.front;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;

/**
 * 用户收藏表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_article_collect", pkName = "acc_no,art_no")
public class QtArticleColet extends BaseModel<QtArticleColet> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtArticleColet dao = new QtArticleColet();
	public static final String all = "acc_no,art_no,collect_time";
	public static final String sql_query_acc_colet_select = "SELECT ai.acc_no, ai.acc_avatar, ai.nick_name, a.article_no, a.add_time, a.article_type, t.type_name, a.article_static, a.article_title, a.article_summary, a.article_state, a.article_imgs, a.article_tags, a.article_tag_ids, a.article_theme ";
	public static final String sql_query_acc_colet_except = "FROM qt_article_collect ac LEFT JOIN qt_account_info ai ON ai.acc_no = ac.acc_no LEFT JOIN qt_article a ON a.article_no = ac.art_no LEFT JOIN qt_art_type t ON a.article_type = t.type_id ";
}
