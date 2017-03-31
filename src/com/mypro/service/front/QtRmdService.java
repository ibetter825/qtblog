package com.mypro.service.front;

import java.util.List;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import com.mypro.model.admin.QtRmd;

public class QtRmdService {
	public final static QtRmdService service = new QtRmdService();
	/**
	 * 根据推荐分类查询文章，需要手动清除缓存
	 * @param article_no
	 * @return
	 */
	public List<QtRmd> queryQtRmdByType(final String rmd_type){
		List<QtRmd> rmds = CacheKit.get("commonCache", "rmd-"+rmd_type, new IDataLoader(){
			public Object load(){
				return QtRmd.dao.find(QtRmd.sql_query_rmd_by_type, rmd_type);
			}
		});
		return rmds;
	}
}
