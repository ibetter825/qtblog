package com.mypro.admin.task;

import com.jfinal.plugin.ehcache.CacheKit;
import com.mypro.service.front.QtHomePageService;

/**
 * 每周热门文章，每周日晚上12点更新缓存
 * @author ibett
 *
 */
public class RankCacheTask implements Runnable {

	@Override
	public void run() {
		CacheKit.remove("commonCache", "rank");//移除缓存
		QtHomePageService.service.queryRankArticles();//再查询一次
		System.out.println("RankCacheTask:--成功更新每周热门文章");
	}

}
