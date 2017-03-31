package com.mypro.model.front;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;

/**
 * 用户点赞表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_article_like", pkName = "acc_no,art_no")
public class QtArticleLike extends BaseModel<QtArticleLike> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtArticleLike dao = new QtArticleLike();
	public static final String all = "acc_no,art_no,like_time";
}
