package com.mypro.admin.task;

import com.jfinal.plugin.ehcache.CacheKit;
import com.mypro.service.front.QtHomePageService;

/**
 * 首页最新文章，名人名言缓存，每天早上8点中午12点和晚上6点更新
 * @author ibett
 *
 */
public class HpCacheTask implements Runnable {

	@Override
	public void run() {
		CacheKit.remove("commonCache", "articles");//移除首页最新文章缓存
		CacheKit.remove("commonCache", "motto");//更新缓存
		QtHomePageService.service.queryRandomMotto();
		System.out.println("HpCacheTask:--首页最新文章以及名人名言缓存更新");
	}

}
