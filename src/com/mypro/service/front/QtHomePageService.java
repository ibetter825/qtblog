package com.mypro.service.front;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.front.QtArticle;
import com.mypro.model.front.QtHomePage;
import com.mypro.model.front.QtMotto;

public class QtHomePageService {
	public final static QtHomePageService service = new QtHomePageService();
	
	/**
	 * 查询首页所有配置，没有缓存的数据
	 * @return
	 */
	public Map<String, List<QtHomePage>> queryHomePagesWithOutCache(Integer hp_no, String hp_type){
		String sql = "select " + QtHomePage.all + " from qt_home_page";
		List<QtHomePage> datas = null;
		Map<String, List<QtHomePage>> map = new HashMap<String, List<QtHomePage>>();
		if(hp_type != null){
			sql += " where hp_no = ? and hp_type = ? order by hp_seq";
			datas = QtHomePage.dao.find(sql, hp_no, hp_type);
			map.put(hp_type, new ArrayList<QtHomePage>());
		}else{
			datas = QtHomePage.dao.find(sql+" order by hp_seq");
			map.put("slide", new ArrayList<QtHomePage>());
			map.put("large", new ArrayList<QtHomePage>());
			map.put("focus", new ArrayList<QtHomePage>());
			map.put("hot", new ArrayList<QtHomePage>());
		}
		for (QtHomePage hp : datas)
			map.get(hp.get("hp_type")).add(hp);
		return map;
	}
	/**
	 * 查询首页全部内容
	 * @param pager
	 * @return
	 */
	public Map<String, List<QtHomePage>> queryHomePages(){
		Map<String, List<QtHomePage>> hp = CacheKit.get("commonCache", "hpdata", new IDataLoader(){
			public Object load(){
				List<QtHomePage> datas = QtHomePage.dao.find("select " + QtHomePage.all + " from qt_home_page order by hp_seq"); 
				Map<String, List<QtHomePage>> map = new HashMap<String, List<QtHomePage>>();
				map.put("slide", new ArrayList<QtHomePage>());
				map.put("large", new ArrayList<QtHomePage>());
				map.put("focus", new ArrayList<QtHomePage>());
				map.put("hot", new ArrayList<QtHomePage>());
				for (QtHomePage hp : datas)
					map.get(hp.get("hp_type")).add(hp);
				return map;
			}
		});
		return hp;
	}
	/**
	 * 查询首页一周热门排行,查询评论最多的文章,每周日晚上自动更新热门文章的缓存
	 * @return
	 */
	public List<QtArticle> queryRankArticles(){
		List<QtArticle> hots = CacheKit.get("commonCache", "rank", new IDataLoader(){
			public Object load(){
				long time = ToolDateTime.getDateByTime() - 1000 * 60 * 60 * 12 * 7;//7天
				List<QtArticle>  ls = QtArticleService.service.queryRankArticles(time, 5);
				return ls;
			}
		});
		return hots;
	}
	/**
	 * 随机查询一条名人名言
	 * @return
	 */
	public QtMotto queryRandomMotto(){
		QtMotto motto = CacheKit.get("commonCache", "motto", new IDataLoader(){
			public Object load(){
				return QtMotto.dao.find(QtMotto.sql_query_random_motto, 1).get(0);
			}
		});
		return motto;
	}
	/**
	 * 查询随机推荐的文章，10篇，每天晚上12点更新缓存
	 * @return
	 */
	public List<QtArticle> queryRandomArticles(){
		List<QtArticle> randoms = CacheKit.get("commonCache", "random", new IDataLoader(){
			public Object load(){
				return QtArticleService.service.queryRandomArticles(10);
			}
		});
		return randoms;
	}
	/**
	 * 更新首页缓存,后台手动更新
	 * @return
	 */
	public boolean refreshHomePages(){
		List<QtHomePage> datas = QtHomePage.dao.find("select " + QtHomePage.all + " from qt_home_page order by hp_seq"); 
		Map<String, List<QtHomePage>> map = new HashMap<String, List<QtHomePage>>();
		map.put("slide", new ArrayList<QtHomePage>());
		map.put("large", new ArrayList<QtHomePage>());
		map.put("focus", new ArrayList<QtHomePage>());
		map.put("hot", new ArrayList<QtHomePage>());
		for (QtHomePage hp : datas)
			map.get(hp.get("hp_type")).add(hp);
		CacheKit.put("commonCache", "hpdata", map);
		return true;
	}
	/**
	 * 获取首页最新发布的文章，每天早上8点和晚上6点更新缓存
	 * @param pager
	 * @param rq
	 * @return
	 */
	public String queryHomePageQtArts(final PagerModel pager, final QueryModel rq){
		String html = CacheKit.get("commonCache", "articles", new IDataLoader(){
			public Object load(){
				String select = QtArticle.sql_query_article_by_acc_select;
				String sqlExceptSelect = QtArticle.sql_query_article_by_acc_except;
				Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, true);
				sqlExceptSelect = (String) queryMap.get("sql");
				sqlExceptSelect = pager.getOrderSql(sqlExceptSelect);
				Object[] params = (String[]) queryMap.get("params");
				Page<QtArticle> page = QtArticle.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect, params);
				return QtArticle.getArtcilesHtml(page.getList(), false);
			}
		});
		return html;
	}
	/**
	 * 保存或删除homepage
	 * @param hp
	 * @return
	 */
	public boolean saveOrUpdateHomePage(QtHomePage hp){
		if(Integer.parseInt(hp.get("hp_no", -1).toString()) != -1)
			return hp.update();
		else
			return hp.save();
	}
}
