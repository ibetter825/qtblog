package com.mypro.admin.task;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.jfinal.plugin.ehcache.CacheKit;
import com.mypro.admin.thread.DelArticleThread;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.model.front.QtArticle;
import com.mypro.service.admin.QtArticleService;
import com.mypro.service.front.QtTagService;

/**
 * 删除状态为-1且修改时间超过15天的文章，每天早上5点执行
 * 更新热门标签
 * @author ibett
 *
 */
public class DailyTask implements Runnable {

	@Override
	public void run() {
		//1.跟新htags
		CacheKit.remove("commonCache", "htags");
		QtTagService.service.queryHotTags(24);
		//2.删除文章
		long time = ToolDateTime.getDateByTime();
		List<QtArticle> articles = QtArticleService.service.queryArticleList(time);
		if(articles != null && articles.size() > 0){
			QtArticleService.service.deleteArticlesByTime(time);
			ConcurrentLinkedQueue<QtArticle> carts = new ConcurrentLinkedQueue<>(articles);
			DelArticleThread thread = DelArticleThread.getInstance();
			thread.setArticles(carts);
			System.out.println("DailyArticleTask:--开始执行删除");
			thread.run();
		}
	}

}
