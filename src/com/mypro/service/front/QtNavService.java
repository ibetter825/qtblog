package com.mypro.service.front;

import java.util.List;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import com.mypro.model.front.QtNav;

public class QtNavService {
	public final static QtNavService service = new QtNavService();
	/**
	 * 查询前台所有正常的navs
	 * @param pager
	 * @return
	 */
	public List<QtNav> queryNavs(){
		List<QtNav> navs = CacheKit.get("commonCache", "navs", new IDataLoader(){
			public Object load(){
				return QtNav.dao.find("select " + QtNav.simple + " from qt_nav where nav_state > -1 order by nav_seq");
			}
		});
		return navs;
	}
}
