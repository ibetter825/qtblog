package com.mypro.model.front;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;

/**
 * 文章评论表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_article_comt", pkName = "comt_no")
public class QtArtComt extends BaseModel<QtArtComt> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtArtComt dao = new QtArtComt();
	public static final String all = "comt_no,comt_content,add_time,art_no,from_acc,to_acc";
	public static final String sql_comt_acc = "select (select count(reply_no) from qt_comt_reply where comt_no = c.comt_no) as reply_count, c.comt_no,c.comt_content,c.add_time,c.art_no,c.from_acc,c.to_acc,f.acc_avatar as from_avatar, f.nick_name as from_name, t.acc_avatar as to_avatar, t.nick_name as to_name from qt_article_comt c left join qt_account_info f on f.acc_no = c.from_acc left join qt_account_info t on t.acc_no = c.to_acc where c.art_no = ? order by c.add_time desc limit ?,?";
}
