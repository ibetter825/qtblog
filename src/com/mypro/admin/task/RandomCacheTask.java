package com.mypro.admin.task;

import com.jfinal.plugin.ehcache.CacheKit;
import com.mypro.service.front.QtHomePageService;

/**
 * 随机推荐文章，每天晚上12点更新缓存
 * @author ibett
 *
 */
public class RandomCacheTask implements Runnable {

	@Override
	public void run() {
		CacheKit.remove("commonCache", "random");//移除缓存
		QtHomePageService.service.queryRandomArticles();//再查询一次
		System.out.println("RandomCacheTask:--成功更新随机推荐文章");
	}

}
