package com.mypro.admin.thread;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.commons.lang.StringUtils;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.mypro.common.DictKeys;
import com.mypro.common.tools.FileUtils;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.model.front.QtArticle;

/**
 * 删除文章图片
 * @author ibett
 *
 */
public class DelArticleThread {
	private String rootPath = PathKit.getWebRootPath();
	private static DelArticleThread thread = null;
	private ConcurrentLinkedQueue<QtArticle> articles;
	private DelArticleThread(){}
	public static DelArticleThread getInstance(){
		if(thread == null)
			thread = new DelArticleThread();
		return thread;
	}
	/**
	 * 执行删除
	 */
	public void run() {
		for(int i = 0; i < 5; i++){
			new Thread(new Runnable(){
				   public void run(){
					   delArticles();
				   }
			}).start();
		}
		System.out.println("DailyArticleTask:--执行删除完毕");
	}
	/**
	 * 删除文章
	 */
	private void delArticles(){
		List<String> imgsArray = null;
		String imgs = null;
		for (QtArticle art : articles) {
			long add_time = art.getLong("add_time");
			Integer[] node = ToolDateTime.getTimeNode(add_time);
			
			//删除文件对应的静态文件
			String htmlDefaultDir = File.separator + PropKit.get(DictKeys.html_dir_name);
			String htmlAbsoluteRootPath = PathKit.getWebRootPath();
			//目录结构为  /article/2016/10/23
			String htmlRelativePath = htmlDefaultDir + File.separator + (node[0] < 10 ? "0" + node[0] : node[0]) + File.separator + (node[1] < 10 ? "0" + node[1] : node[1]) + File.separator + (node[2] < 10 ? "0" + node[2] : node[2]);
			htmlAbsoluteRootPath += htmlRelativePath + File.separator;
			//名字为 时分秒毫秒.html
			String htmlName = node[3].toString()+node[4].toString()+node[5].toString()+node[6].toString()+".html";//这样的话可能有重复,管他的
			FileUtils.deleteFile(htmlAbsoluteRootPath+htmlName);
			
			//删除文件对应的图片
			imgs = art.getStr("article_imgs");
			if(StringUtils.isNotEmpty(imgs)){
				imgsArray = Arrays.asList(imgs.split(";"));
				for (String path : imgsArray) {
					if(!path.startsWith("http"))
						new File(rootPath+path).delete();
				}
			}
			articles.remove(art);
		}
	}
	
	public void setArticles(ConcurrentLinkedQueue<QtArticle> articles) {
		this.articles = articles;
	}
}
