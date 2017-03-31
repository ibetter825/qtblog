package com.mypro.model.front;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.model.BaseModel;

/**
 * 消息表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_notice", pkName = "nt_id")
public class QtNotice extends BaseModel<QtNotice> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtNotice dao = new QtNotice();
	public static final String all = "nt_id,nt_content,add_time,nt_from_no,nt_to_no,nt_type,is_read,art_no,reply_no,comt_no";
	
	public static final String sql_query_notcie_select = "select n.add_time,n.art_no,art.article_title,n.comt_no,n.is_read,n.nt_content,n.nt_from_no,n.nt_id,n.nt_to_no,n.nt_type,ai.nick_name as from_name ";
	public static final String sql_query_notcie_except = " from qt_notice n left join qt_article art on art.article_no = n.art_no left join qt_account_info ai on ai.acc_no = n.nt_from_no";
	
	/**
	 * 获取个人中心通知消息列表
	 * @param notices
	 * @return
	 */
	public static String getNoticesHtml(List<QtNotice> notices){
		StringBuffer buffer = new StringBuffer();
		if(notices.size() == 0)
			buffer.append("<div id=\"content-main\"><article class=\"excerpt excerpt-one\"><p>没有消息!</p></article></div>");
		else{
			Map<String, StringBuffer> res = new LinkedHashMap<String, StringBuffer>();
			buffer.append("<div id=\"content-main\"><article class=\"archives\">");
			int year = 0, month = 0;
			String key = null;
			for (QtNotice n : notices){
				Integer[] node = ToolDateTime.getTimeNode(n.getLong("add_time"));
				year = node[0]; month = node[1];
				key = year + "-" + month;
				if(res.containsKey(key))
					res.get(key).append(getSingleNotcieHtml(n, node));
				else
					res.put(key, new StringBuffer(getSingleNotcieHtml(n, node)));
			}
			Iterator<String> it = res.keySet().iterator();
			while(it.hasNext()){
				key = it.next();
				buffer.append("<div class=\"item\"><h3>"+key+"</h3>");
				buffer.append("<ul class=\"archives-list\">");
				buffer.append(res.get(key).toString());
				buffer.append("</ul></div>");
			}
			buffer.append("</article></div>");
		}
		return buffer.toString();
	}
	/**
	 * 获取单个节点
	 * @param notice
	 * @return
	 */
	public static String getSingleNotcieHtml(QtNotice notice, Integer[] node){
		int type = notice.getInt("nt_type");//0-系统消息，1-评论消息，2-回复消息，3-关注消息，4-点赞，5-收藏
		String content = null;
		String a = "<a href=\"/main/"+notice.getInt("nt_from_no")+"\">"+notice.getStr("from_name")+"</a>";
		String title = notice.getStr("article_title");
		String sub_title = title;
		if(title != null && title.length() > 20)
			sub_title = title.substring(0,20) + "...";
		String art = "<a href=\"/article/detail/"+notice.getInt("art_no")+"\" title=\""+title+"\">"+sub_title+"</a>";;
		switch (type) {
			case 0:
				content = "<i class=\"text-split\">系统消息</i>"+notice.getStr("nt_content");
				break;
			case 1:
				content = a + "<i class=\"text-split\">评论文章</i>" + art; 
				break;
			case 2:
				content = a + "<i class=\"text-split\">回复了您</i>" + art; 
				break;
			case 3:
				content = a + "<i class=\"text-split\">关注了您</i>"; 
				break;
			case 4:
				content = a + "<i class=\"text-split\">赞了您的文章</i>" + art;
				break;
			case 5:
				content = a + "<i class=\"text-split\">收藏了您的文章</i>" + art;
				break;
			default:
				content = "";
				break;
		}
		StringBuffer buffer = new StringBuffer("<li><time>"+(node[2] < 10 ? "0"+node[2] : node[2])+"日</time>");
		buffer.append("<span>"+content+"</span>");
		buffer.append("<span class=\"text-muted\">"+(node[3] < 10 ? "0"+node[3] : node[3])+":"+(node[4] < 10 ? "0"+node[4] : node[4])+"</span></li>");
		return buffer.toString();
	}
}
