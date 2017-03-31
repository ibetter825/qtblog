package com.mypro.admin.task;
/**
 * 爬取文章，每天早上7点爬取
 * @author ibett
 *
 */
public class CrawlerArticleTask implements Runnable {

	@Override
	public void run() {
		System.out.println("CollectArticleTask:--抓取文章开始");
	}

}
