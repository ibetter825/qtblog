package com.mypro.service.front;

import java.util.List;
import java.util.Map;
import com.mypro.model.QueryModel;
import com.mypro.model.front.QtArticleType;

public class QtArticleTypeService {
	public final static QtArticleTypeService service = new QtArticleTypeService();
	
	/**
	 * 保存用户文章分类
	 * @param type
	 * @return
	 */
	public boolean saveQtArticleType(QtArticleType type){
		return type.save();
	}
	/**
	 * 修改用户文章分类
	 * @param type
	 * @return
	 */
	public boolean updateQtArticleType(QtArticleType type){
		return type.update();
	}
	/**
	 * 删除用户文章分类
	 * @param type
	 * @return
	 */
	public boolean removeQtArticleType(QtArticleType type){
		return type.delete();
	}
	/**
	 * 查询用户文章分类
	 * @param rq
	 * @return
	 */
	public List<QtArticleType> queryAtArticleTypes(QueryModel rq){
		String select = QtArticleType.sql_query_art_type;
		Map<String, Object> queryMap = rq.getQuerySql(select, false);
		select = (String) queryMap.get("sql");
		Object[] params = (String[]) queryMap.get("params");
		List<QtArticleType> types = null;
		if(params != null)
			types = QtArticleType.dao.find(select, params);
		else
			types = QtArticleType.dao.find(select);
		return types;
	}
	/**
	 * 查询用户自定义分类，并统计分类下的文章数量，未分类的文章设置type_id为空,article_type为0
	 * @param acc_no
	 * @return
	 */
	public List<QtArticleType> queryQtArtTypeByAcc(Integer acc_no){
		return QtArticleType.dao.find(QtArticleType.sql_query_art_type_by_acc, acc_no, acc_no);
	}
}
