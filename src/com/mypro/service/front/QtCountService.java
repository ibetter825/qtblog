package com.mypro.service.front;

import com.mypro.model.front.QtArticleCount;

public class QtCountService {
	public final static QtCountService service = new QtCountService();
	/**
	 * 根据主键查询
	 * @param art_no
	 * @param acc_no
	 * @return
	 */
	public QtArticleCount queryByPK(String art_no, Integer acc_no){
		if(acc_no != null)
			return QtArticleCount.dao.findFirst(QtArticleCount.sql_query_by_pk, acc_no, acc_no, acc_no,art_no);
		else
			return QtArticleCount.dao.findById(art_no);
	}
	/**
	 * 根据用户名称关联aticle查询并统计，该用户文章的被收藏，转载，喜欢的数量，以及文章的总数
	 * @param acc_no
	 * @return
	 */
	public QtArticleCount queryByAcc(Integer acc_no){
		return QtArticleCount.dao.findFirst(QtArticleCount.sql_count_by_acc, acc_no);
	}
}
