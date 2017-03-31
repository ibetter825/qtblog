package com.mypro.service.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.mypro.common.DictKeys;
import com.mypro.common.tools.FileUtils;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.front.QtArticle;

public class QtArticleService {
	public final static QtArticleService service = new QtArticleService();
	/**
	 * 查询文章列表
	 * @param pager
	 * @param rq
	 * @return
	 */
	public Page<QtArticle> queryArticleList(PagerModel pager, QueryModel rq) {
		String select = "select "+QtArticle.allwithacc;
		String sqlExceptSelect = " from qt_article art left join qt_account acc on art.acc_no = acc.acc_no left join sys_dict dict on dict_no = art.article_theme  where art.article_state > -1";
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, false);
		sqlExceptSelect = (String) queryMap.get("sql");
		sqlExceptSelect = pager.getOrderSql(sqlExceptSelect);
		Object[] params = (String[]) queryMap.get("params");
		Page<QtArticle> page = null;
		if(params == null)
			page = QtArticle.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect);
		else
			page = QtArticle.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect, params);
		return page;
	}
	/**
	 * 查询需要删除的过期的文章
	 * @param time
	 * @return
	 */
	public List<QtArticle> queryArticleList(long time){
		String sql = "select article_no, article_imgs, add_time from qt_article where article_state = -1 and update_time + 1000 * 60 * 60 * 24 * 15 < ? ";
		return QtArticle.dao.find(sql, time);
	}
	/**
	 * 删除需要删除的过期文章
	 * @param time
	 * @return
	 */
	public int deleteArticlesByTime(long time){
		String sql = "delete from qt_article where article_state = -1 and update_time + 1000 * 60 * 60 * 24 * 15 < ? ";
		return Db.update(sql, time);
	}
	public boolean updateQtArticle(QtArticle article){
		try {
			return article.update();
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 静态化文章
	 * @param art_nos
	 * @return
	 */
	public int staticArticle(String[] art_nos, String domain){
		int c = 0;
		QtArticle article = null;
		for (String no : art_nos) {
			article = QtArticle.dao.findFirst("select article_no, add_time from qt_article where article_no = ?", no);
			if(article != null){
				if(getAndWriteHtml(article, domain))
					c++;
			}
		}
		return c;
	}
	public int staticArticle(Integer art_no, String domain){
		QtArticle article = QtArticle.dao.findFirst("select article_no, add_time from qt_article where article_no = ? and article_state = 1 and article_flag = 0", art_no);
		if(article == null)
			return 0;
		if(getAndWriteHtml(article, domain))
			return 1;
		else
			return 0;
	}
	/**
	 * 抓取并将内容写出
	 * @param art_no
	 * @return
	 */
	public boolean getAndWriteHtml(QtArticle article, String domain){
		Integer art_no = article.getInt("article_no");
		long add_time = article.getLong("add_time");
		Integer[] node = ToolDateTime.getTimeNode(add_time);
		String sUrl = domain+"article/detail/"+art_no;//文章的请求地址
		
		String htmlDefaultDir = File.separator + PropKit.get(DictKeys.html_dir_name);
		String htmlAbsoluteRootPath = PathKit.getWebRootPath();
		//目录结构为  /article/2016/10
		String htmlRelativePath = htmlDefaultDir + File.separator + (node[0] < 10 ? "0" + node[0] : node[0]) + File.separator + (node[1] < 10 ? "0" + node[1] : node[1]);
		htmlAbsoluteRootPath += htmlRelativePath + File.separator;
		//名字为 文章id.html
		String htmlName = art_no+".html";//这样的话可能有重复,管他的
		URL url;  
		boolean res = false;
        try {  
            url = new URL(sUrl);  
            URLConnection urlconnection = url.openConnection();  
            urlconnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");  
            InputStream is = url.openStream();  
            BufferedReader bReader = new BufferedReader(new InputStreamReader(is));  
            StringBuffer sb = new StringBuffer();//sb为爬到的网页内容  
            String rLine = null;  
            while((rLine=bReader.readLine())!=null)
                sb.append(rLine + "\r\n");
            if(FileUtils.writeFile(sb.toString(), htmlAbsoluteRootPath, htmlName, PropKit.get(DictKeys.config_encoding)))
            	article.set("article_static", 1).update();//修改为已静态化
            res = true;
        } catch (IOException e) { 
            e.printStackTrace();
        }
		return res;
	}
}
