package com.mypro.service.front;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.context.QtAccountContext;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.admin.SysDict;
import com.mypro.model.front.QtAccount;
import com.mypro.model.front.QtArtComt;
import com.mypro.model.front.QtArticle;
import com.mypro.model.front.QtArticleCount;
import com.mypro.model.front.QtArticleScan;
import com.mypro.model.front.QtComtReply;
import com.mypro.model.front.QtTag;
import com.mypro.web.tools.ToolStrYt;

public class QtArticleService {
	public final static QtArticleService service = new QtArticleService();
	/**
	 * 保存文章
	 * @param article
	 * @return
	 */
	@Before(Tx.class)
	public boolean saveQtArticle(QtArticle article){
		boolean res = article.save();
		if(res){
			String article_tags = article.get("article_tags", null);
			if(article_tags != null){
				String article_tag_ids = article.getStr("article_tag_ids");//已经存在的标签，对应的是该标签的id，不存在的标签对应的是0
				String[] tag_names = article_tags.split(",");
				String[] tag_ids = article_tag_ids.split(",");
				QtTag tag = null;
				StringBuffer buffer = new StringBuffer("insert into qt_art_tag values ");
				//保存文章时，新增标签，在文章与标签表中添加记录，感觉这一步略为复杂了些
				int tag_id = 0;
				for(int i = 0, l = tag_names.length; i < l; i++){
					if("0".equals(tag_ids[i])){//此标签为不存在的标签记录
						tag = new QtTag();
						tag.set("tag_name", tag_names[i]);
						tag.set("add_time", ToolDateTime.getDateByTime());
						//tag.save();
						tag_id = QtTagService.service.saveOrQuery(tag);
						tag_ids[i] = tag_id+"";
					}
					buffer.append(" (" + tag_ids[i] + ",'" + article.getInt("article_no") + "','" + article.getInt("acc_no") + "'),");
				}
				String sql = buffer.toString();
				Db.update(sql.substring(0, sql.length() - 1));//添加tag与art的关联
				res = article.set("article_tag_ids", StringUtils.join(tag_ids, ',')).update();
			}
		}
		return res;
	}
	/**
	 * 修改文章
	 * @param article
	 * @param old_tag_ids
	 * @return
	 */
	@Before(Tx.class)
	public boolean editQtArticle(QtArticle article, String old_tag_ids){
		article.set("update_time", ToolDateTime.getDateByTime()).set("article_static", 0);
		if(old_tag_ids != null){
			String new_tag_names = article.get("article_tags", null);
			String sql = "delete from qt_art_tag where art_no = ?";
			if(new_tag_names != null){
				String new_tag_ids = article.getStr("article_tag_ids");//已经存在的标签，对应的是该标签的id，不存在的标签对应的是0
				String[] new_tag_id_arr = new_tag_ids.split(",");
				if(StringUtils.isNotBlank(old_tag_ids)){
					Integer[] tag_id_int_arr = ToolStrYt.strArrToIntArr(old_tag_ids.split(",")); 
					Arrays.sort(tag_id_int_arr);
					old_tag_ids = StringUtils.join(tag_id_int_arr, ",");
					tag_id_int_arr = ToolStrYt.strArrToIntArr(new_tag_id_arr); 
					Arrays.sort(tag_id_int_arr);
					new_tag_ids = StringUtils.join(tag_id_int_arr, ",");//新标签排序后
					if(!new_tag_ids.equals(old_tag_ids) || (old_tag_ids+",").contains("0,"))//新标签与旧标签不相等，存在标签修改，旧标签中id包括0表示有问题
						Db.update(sql, article.getInt("article_no"));//需要删除该文章与标签的对应关系
					else//如果两者相等，则不需要对标签做任何操作
						return article.update();
				}
				String[] new_tag_name_arr = new_tag_names.split(",");
				QtTag tag = null;
				StringBuffer buffer = new StringBuffer("insert into qt_art_tag values ");
				//修改文章时，新增的标签，在文章与标签表中添加记录，感觉这一步略为复杂了些
				//并且重新添加关联记录，另外文章本来就存在的标签对应的tag_num不做修改，新增的标签需要修改tag_num
				int tag_id = 0;
				for(int i = 0, l = new_tag_name_arr.length; i < l; i++){
					if(new_tag_id_arr[i].equals("0")){//此标签为不存在的标签记录
						tag = new QtTag();
						tag.set("tag_name", new_tag_name_arr[i]);
						tag.set("add_time", ToolDateTime.getDateByTime());
						//tag.save();
						tag_id = QtTagService.service.saveOrQuery(tag);
						new_tag_id_arr[i] = tag_id+"";
					}
					buffer.append(" (" + new_tag_id_arr[i] + ",'" + article.getInt("article_no") + "','" + article.getInt("acc_no") + "'),");
				}
				sql = buffer.toString();
				Db.update(sql.substring(0, sql.length() - 1));//添加tag与art的关联
				article.set("article_tag_ids", StringUtils.join(new_tag_id_arr, ','));
			}else{
				if(StringUtils.isNotBlank(old_tag_ids))
					Db.update(sql, article.getInt("article_no"));//需要删除该文章与标签的对应关系
			}
		}
		return article.update();
	}
	/**
	 * 根据文章no查询文章
	 * @param article_no
	 * @return
	 */
	public QtArticle queryQtArticleByNo(Integer article_no, Integer article_state){
		String sql = "select " + QtArticle.allwithacc + " from qt_article art left join qt_account acc on art.acc_no = acc.acc_no left join sys_dict dict on dict.dict_no = art.article_theme where art.article_no = ?";
		if(article_state != null)
			sql += " and art.article_state = " + article_state;
		else
			sql += " and art.article_state > -1";
		return (QtArticle) QtArticle.dao.findFirst(sql, article_no);
	}
	/**
	 * 查询文章的前后两篇文章
	 * @param models
	 * @return
	 */
	public List<QtArticle> queryQtArticleNextAndPrev(QtArticle article){
		String base_sql = "(select article_no, article_title, add_time from qt_article where acc_no = '"+article.getInt("acc_no")+"' and article_state = 1 and article_flag = 0 and article_theme = '"+article.getStr("article_theme")+"' and article_type = "+article.getInt("article_type");
		String prev_sql = " and add_time < "+article.getLong("add_time")+" order by add_time desc limit 1)";
		String next_sql = " and add_time > "+article.getLong("add_time")+" order by add_time limit 1)";
		String sql = base_sql + prev_sql + " union all " + base_sql + next_sql;
		return QtArticle.dao.find(sql);
	}
	/**
	 * 根据文章no以及分页查询评论与回复
	 * @param pager
	 * @param art_no
	 * @return
	 */
	public Map<String, Object> queryQtArtComtAndReplyByArtNo(PagerModel pager, String art_no){
		//先查询评论前5条，
		List<QtArtComt> comts = QtArtComt.dao.find(QtArtComt.sql_comt_acc, (pager.getCurPage() - 1) * pager.getPageSize(), pager.getPageSize());
		String nos = "";
		for (QtArtComt c : comts) 
			nos += c.getLong("comt_no")+",";
		nos = nos.substring(0,nos.length() - 1);
		//再通过评论，查询评论下的回复，前5条
		List<QtComtReply> replies = QtComtReply.dao.find(QtComtReply.sql_comt_replies + nos + ")", 5);//sql语句什么的还得像个办法统一管理得好
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("comts", comts);
		res.put("replies", replies);
		res.put("rtime", ToolDateTime.getDateByTime());
		return res;
	}
	/**
	 * 根据用户编号查询该用户的分页文章以及评论数量，点赞数量
	 * @param pager
	 * @param rq
	 * @return
	 */
	public Page<QtArticle> queryQtArtByParams(PagerModel pager, QueryModel rq){
		String select = QtArticle.sql_query_article_by_acc_select;
		String sqlExceptSelect = QtArticle.sql_query_article_by_acc_except;
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, true);
		sqlExceptSelect = (String) queryMap.get("sql");
		sqlExceptSelect = pager.getOrderSql(sqlExceptSelect);
		Object[] params = (String[]) queryMap.get("params");
		Page<QtArticle> page = null;
		if(params == null)
			page = QtArticle.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect);
		else
			page = QtArticle.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect, params);
		
		return page;
	}
	/**
	 * 查询一周热门文章
	 * @param size
	 * @return
	 */
	public List<QtArticle> queryRankArticles(long time, int size){
		String sql = "select a.article_no,a.article_title,a.add_time,a.article_static,ac.scan_count,ac.like_count,ac.comt_count from qt_article a left join qt_article_count ac on ac.article_no = a.article_no where a.article_state = 1 and a.article_flag = 0 and a.add_time >= ? order by ac.comt_count desc limit ?";
		List<QtArticle> articles = QtArticle.dao.find(sql, time, size);
		return articles;
	}
	/**
	 * 查询随机推荐的文章
	 * @param size
	 * @return
	 */
	public List<QtArticle> queryRandomArticles(int size){
		String sql = "SELECT  t1.article_no,t1.article_title,t1.add_time,t1.article_static,t1.article_imgs FROM `qt_article` AS t1 JOIN ( SELECT ROUND( RAND() * ( (SELECT MAX(article_no) FROM `qt_article`) - (SELECT MIN(article_no) FROM `qt_article`)) + (SELECT MIN(article_no) FROM `qt_article`)) AS id ) AS t2 WHERE t1.article_no >= t2.id AND t1.article_state = 1 AND t1.article_flag = 0 ORDER BY t1.article_no LIMIT ?";
		return QtArticle.dao.find(sql, size);
	}
	/**
	 * 获取文章的基本信息，用户昵称，文章栏目，浏览量
	 * @param article_no
	 * @return
	 */
	public QtArticle queryArticleInfo(final Integer article_no){
		String sql = "select a.article_no, a.acc_no, a.add_time, i.nick_name, a.article_theme, d.dict_name, c.scan_count from qt_article a left join qt_article_count c on a.article_no = c.article_no left join qt_account_info i on i.acc_no = a.acc_no left join sys_dict d on d.dict_no = a.article_theme where a.article_no = ?";
		new Thread(new Runnable() {
			@Override
			public void run() {
				queryQtArtScanCount(article_no);
			}
		}).start();
		return QtArticle.dao.findFirst(sql, article_no);
	}
	/**
	 * 查询某文章的浏览数
	 * @param article_no
	 * @return
	 */
	public int queryQtArtScanCount(Integer article_no){
		//这里面的东西要改，不能有这么多的操作
		String sql = "update qt_article_count set scan_count = scan_count + 1 where article_no = ?";
		int l = Db.update(sql, article_no);
		if(l == 0)
			new QtArticleCount().set("article_no", article_no).save();//这里可以在添加文章时使用触发器自动添加进来
		//以下的代码可以使用线程池，或者使用aop
		QtAccount account = QtAccountContext.getCurrentQtAccount();
		if(account != null){
			Integer acc_no = account.get("acc_no");
			long scan_time = ToolDateTime.getDateByTime();
			QtArticleScan scan = new QtArticleScan();
			scan.set("acc_no", acc_no);
			scan.set("article_no", article_no);
			scan.set("scan_time", scan_time);
			if(!scan.update())
				scan.save();
		}
		sql = "select scan_count as count from qt_article_count where article_no = ? ";
		int count = Db.findFirst(sql, article_no).getInt("count"); 
		return count;
	}
	/**
	 * 查询统计量最多的文章
	 * @return
	 */
	public Page<QtArticle> queryQtArtByCount(PagerModel pager, QueryModel rq){
		String select = QtArticle.sql_query_article_by_count_select;
		String sqlExceptSelect = QtArticle.sql_query_article_by_count_except;
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, true);
		sqlExceptSelect = (String) queryMap.get("sql");
		sqlExceptSelect = pager.getOrderSql(sqlExceptSelect);
		Object[] params = (String[]) queryMap.get("params");
		Page<QtArticle> page = null;
		if(params == null)
			page = QtArticle.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect);
		else
			page = QtArticle.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect, params);
		return page;
	}
	/**
	 * 按评论数量查询用户的文章列表
	 * @param pager
	 * @param rq
	 * @return
	 */
	public Page<QtArticle> queryQtArticleByComtCount(PagerModel pager, QueryModel rq){
		String select = QtArticle.sql_query_article_by_comt_count_select;
		String sqlExceptSelect = QtArticle.sql_query_article_by_comt_count_except;
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, false);
		sqlExceptSelect = (String) queryMap.get("sql");
		sqlExceptSelect = pager.getOrderSql(sqlExceptSelect);
		Object[] params = (String[]) queryMap.get("params");
		Page<QtArticle> page = null;
		if(params == null)
			page = QtArticle.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect);
		else
			page = QtArticle.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect, params);
		return page;
	}
	/**
	 * 统计某用户下文章分类下文章的数量
	 * @param acc_no
	 * @param art_type
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map> queryArtCountByDict(String acc_no, String art_type){
		String sql = "select count(a.acc_no) as art_count, "+SysDict.simplewithb+" from sys_dict b left join qt_article a on a.article_theme = b.dict_no and a.acc_no = ? where b.dict_param = ? group by b.dict_no order by b.dict_level,b.dict_seq";
		List<SysDict> dicts = SysDict.dao.find(sql, acc_no, art_type);
		List<Map> res = null;
		if(dicts != null)
			res = SysDict.getDictTree(dicts, null);
		return res;
	}
}
