package com.mypro.service.front;

import java.util.List;
import com.mypro.model.PagerModel;
import com.mypro.model.front.QtAccFollow;

/**
 * 关注
 * @author ibett
 *
 */
public class QtAccFollowService {
	public final static QtAccFollowService service = new QtAccFollowService();
	public boolean saveQtAccFollow(QtAccFollow follow){
		return follow.save();
	}
	public boolean removeQtAccFollow(QtAccFollow follow){
		return follow.delete();
	}
	/**
	 * 通过主键查询
	 * @param follow_acc 关注人
	 * @param followed_acc 被关注人
	 * @return
	 */
	public QtAccFollow queryById(Integer follow_acc, Integer followed_acc){
		return QtAccFollow.dao.findById(follow_acc, followed_acc);
	}
	/**
	 * 查询目标用户的粉丝,以及粉丝的文章数，粉丝的粉丝数
	 * @param pager
	 * @param tget_acc 目标用户
	 * @param cur_acc 当前用户
	 * 个人中心时如果不需要查询是否关注
	 * @return
	 */
	public List<QtAccFollow> queryFollowers(PagerModel pager, Integer tget_acc, Integer cur_acc){
		String sql = null;
		List<QtAccFollow> flwers = null;
		if(cur_acc == null){//未登录用户
			sql = QtAccFollow.sql_query_zone_followers;
			flwers = QtAccFollow.dao.find(sql, tget_acc, (pager.getCurPage() - 1) * pager.getPageSize(), pager.getPageSize());
		}else{//已登录用户
			sql = QtAccFollow.sql_query_main_followers;
			flwers = QtAccFollow.dao.find(sql, tget_acc, cur_acc, (pager.getCurPage() - 1) * pager.getPageSize(), pager.getPageSize());
		}
		return flwers;
	}
	/**
	 * 查询用户正在关注的用户,以及文章数，粉丝的粉丝数
	 * @param flwd_acc
	 * @return
	 */
	public List<QtAccFollow> queryFollowing(PagerModel pager, Integer tget_acc, Integer cur_acc){
		String sql = null;
		List<QtAccFollow> flwings = null;
		if(cur_acc == null){//都是自己或者未登录用户
			sql = QtAccFollow.sql_query_nocurr_followings;
			flwings = QtAccFollow.dao.find(sql, tget_acc, (pager.getCurPage() - 1) * pager.getPageSize(), pager.getPageSize());
		}else{//已登录用户,且为个人中心
			sql = QtAccFollow.sql_query_withcurr_followings;
			flwings = QtAccFollow.dao.find(sql, tget_acc, cur_acc, (pager.getCurPage() - 1) * pager.getPageSize(), pager.getPageSize());
		}
		return flwings;
		/*String select = QtAccFollow.sql_query_follows_select;
		String sqlExceptSelect = QtAccFollow.sql_query_following_except;
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, true);
		sqlExceptSelect = (String) queryMap.get("sql");
		sqlExceptSelect = pager.getOrderSql(sqlExceptSelect);
		Object[] params = (String[]) queryMap.get("params");
		Page<QtAccFollow> page = null;
		if(params == null)
			page = QtAccFollow.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect);
		else
			page = QtAccFollow.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect, params);
		return page;*/
	}
}
