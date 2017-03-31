package com.mypro.model.front;

import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;

/**
 * 文章评论回复表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_comt_reply", pkName = "reply_no")
public class QtComtReply extends BaseModel<QtComtReply> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtComtReply dao = new QtComtReply();
	public static final String all = "reply_no,reply_content,add_time,comt_no,from_acc,to_acc";
	public static final String sql_comt_replies_without_limit = "SELECT  "
																+"	r.reply_no,  "
																+"	r.reply_content,  "
																+"	r.add_time,  "
																+"	r.comt_no,  "
																+"	r.from_acc,  "
																+"	r.to_acc,  "
																+"	f.nick_name AS from_name,  "
																+"	f.acc_avatar AS from_avatar,  "
																+"	t.nick_name AS to_name,  "
																+"	t.acc_avatar AS to_avatar  "
																+"	FROM  "
																+"	qt_comt_reply r  "
																+"	LEFT JOIN qt_account_info f ON f.acc_no = r.from_acc  "
																+"	LEFT JOIN qt_account_info t ON t.acc_no = r.to_acc  "
																+"	WHERE  "
																+"	comt_no IN (";
	public static final String sql_comt_replies = "SELECT  "
													+"	replies.reply_no,  "
													+"	replies.reply_content, "
													+"	replies.add_time,  "
													+"	replies.comt_no, "
													+"	replies.from_acc,  "
													+"	replies.to_acc,  "
													+"	replies.from_name, "
													+"	replies.from_avatar,  "
													+"	replies.to_name, "
													+"	replies.to_avatar  "
													+"FROM "
													+"	(  "
													+"		SELECT "
													+"			reply_no, "
													+"			reply_content, "
													+"			add_time, "
													+"			comt_no,  "
													+"			from_acc, "
													+"			to_acc, "
													+"			from_name, "
													+"			from_avatar, "
													+"			to_name,  "
													+"			to_avatar, "
													+"			rank "
													+"		FROM "
													+"			( "
													+"				SELECT  "
													+"					b.reply_no, "
													+"					b.reply_content, "
													+"					b.add_time, "
													+"					b.comt_no, "
													+"					b.from_acc, "
													+"					b.to_acc,  "
													+"					b.from_name,  "
													+"					b.from_avatar, "
													+"					b.to_name, "
													+"					b.to_avatar,  "
													+"					@rownum :=@rownum + 1, "
													+" "
													+"				IF ( "
													+"					@pdept = b.comt_no ,@rank :=@rank + 1 ,@rank := 1 "
													+"				) AS rank, "
													+"				@pdept := b.comt_no  "
													+"			FROM "
													+"				(  "
													+"					SELECT "
													+"						r.reply_no, "
													+"						r.reply_content, "
													+"						r.add_time, "
													+"						r.comt_no,  "
													+"						r.from_acc, "
													+"						r.to_acc, "
													+"						f.nick_name AS from_name, "
													+"						f.acc_avatar AS from_avatar, "
													+"						t.nick_name AS to_name, "
													+"						t.acc_avatar AS to_avatar "
													+"					FROM  "
													+"						qt_comt_reply r  "
													+"					LEFT JOIN qt_account_info f ON f.acc_no = r.from_acc"
													+"					LEFT JOIN qt_account_info t ON t.acc_no = r.to_acc  "
													+"					ORDER BY "
													+"						r.add_time  "
													+"				) b, "
													+"				(  "
													+"					SELECT "
													+"						@rownum := 0,  "
													+"						@pdept := NULL ,@rank := 0 "
													+"				) c  "
													+"			) result  "
													+"		HAVING "
													+"			rank <= ? "
													+"	) replies  "
													+"WHERE "
													+"	replies.comt_no IN (";
}
