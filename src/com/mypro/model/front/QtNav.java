package com.mypro.model.front;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.kit.PropKit;
import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.model.BaseModel;

/**
 * 导航表
 * @author ibett
 *
 */
@Model(dataSourceName = DictKeys.db_dataSource_main, tableName = "qt_nav", pkName = "nav_no")
public class QtNav extends BaseModel<QtNav> {
	private static final long serialVersionUID = -836437769319422544L;
	public static final QtNav dao = new QtNav();
	
	public static final String all = "nav_no,nav_name,nav_pno,nav_uri,nav_icon,nav_state,nav_seq";
	public static final String simple = "nav_no,nav_name,nav_pno,nav_uri,nav_icon,nav_state";
	
	/**
	 * 获取导航条html
	 * @param navs 导航条集合
	 * @param nos 当前导航条no，目前就支持两级栏目
	 * @return
	 */
	public static String getHeaderNavHtml(List<QtNav> navs, String... nos){
		ConcurrentLinkedQueue<QtNav> cnavs = new ConcurrentLinkedQueue<QtNav>(navs);
		StringBuffer buffer = new StringBuffer();
		String pno = null, curs = ArrayUtils.toString(nos);
		HashMap<String, String> subsMap = null;
		for (QtNav n : cnavs) {
			pno = n.getStr("nav_pno");
			if(StringUtils.isNotEmpty(pno)){//二级导航条
				if(subsMap == null)
					subsMap = new HashMap<String, String>();
				subsMap.put(pno, getHeaderSubNavHtml(cnavs, pno, curs));
			}else{
				buffer.append(getSingleNavHtml(n, curs));
				cnavs.remove(n);
			}
		}
		String html = buffer.toString(), key = null, val = null;
		if(subsMap != null){
			Iterator<String> p = subsMap.keySet().iterator();
			while(p.hasNext()){
				key = p.next();
				val = subsMap.get(key);
				html = html.replace("<i p=\""+key+"\"></i>", val);
			}
		}
		return html;
	}
	/**
	 * 获取子导航条
	 * @param navs
	 * @param pno
	 * @param nos
	 * @return
	 */
	public static String getHeaderSubNavHtml(ConcurrentLinkedQueue<QtNav> navs, String pno, String curs){
		StringBuffer buffer = new StringBuffer("<ul class=\"sub-menu\">");
		String cpno = null;
		for (QtNav n : navs){
			cpno = n.getStr("nav_pno"); 
			if(cpno.equals(pno)){
				navs.remove(n);
				buffer.append(getSingleNavHtml(n, curs));
			}
		}
		buffer.append("</ul>"); 
		return buffer.toString();
	}
	/**
	 * 获取单个导航的li
	 * @param n
	 * @param curs
	 * @param pno
	 * @return
	 */
	public static String getSingleNavHtml(QtNav n, String curs){
		if(n.getInt("nav_state") != 1)
			return "";
		String no = n.getStr("nav_no"), uri = n.getStr("nav_uri"), curent = "", clz = "pjax";
		if(curs.contains(no))
			curent = " current-menu-item";
		String pa = "";
		if(StringUtils.isNotEmpty(uri))
			pa = " class=\""+clz+"\" href=\""+uri+"\"";
		
		return "<li class=\"menu-item"+curent+"\" data-title=\""+n.getStr("nav_name")+" - "+PropKit.get(DictKeys.web_site_name)+"\"><a"+pa+"><span class=\"glyphicon "+n.getStr("nav_icon")+"\"></span> "+n.getStr("nav_name")+"</a><i p=\""+no+"\"></i></li>";
	}
	/**
	 * header中用户操作中心
	 * @param account
	 * @param ctx
	 * @return
	 */
	public static String getHeaderOptsHtml(QtAccount account){
		String img = "/static/f/images/default.jpg";
		StringBuffer buffer = new StringBuffer();
		if(account != null){
			img = account.getStr("acc_avatar");
			buffer.append("<img title=\""+account.getStr("nick_name")+"\" class=\"log-info-avatar\" src=\""+img+"\" data-event=\"opts\"/>");
			buffer.append("<a href=\"javascript:void(0);\" onclick=\"$.LG.ot();\" class=\"log-info-icon\"><i class=\"glyphicon glyphicon-log-out\"></i>登出</a>");
			buffer.append("<ul id=\"user-opts\" class=\"user-opts\">");
			buffer.append("<li><a href=\"/article/write/\"><i class=\"glyphicon glyphicon-pencil\"></i> 写文章</a></li>");
			buffer.append("<li><a href=\"/zone/\"><i class=\"glyphicon glyphicon-home\"></i> 主页</a></li>");
			buffer.append("<li><a href=\"/zone/collect/\"><i class=\"glyphicon glyphicon-heart\"></i> 收藏</a></li>");
			buffer.append("<li><a href=\"/zone/following/\"><i class=\"glyphicon glyphicon-user\"></i> 关注</a></li>");
			buffer.append("<li><a href=\"/zone/notice/\"><i class=\"glyphicon glyphicon-bell\"></i> 消息</a></li>");
			buffer.append("<li><a href=\"/zone/setting/\"><i class=\"glyphicon glyphicon-cog\"></i> 设置</a></li>");
			buffer.append("<li><a href=\"javascript:void(0);\" onclick=\"$.LG.ot();\"><i class=\"glyphicon glyphicon-log-out\"></i> 登出</a></li></ul>");
		}else{
			buffer.append("<a href=\"/reg/\" class=\"log-info-icon\"><i class=\"glyphicon glyphicon-user\"></i>注册</a>");
			buffer.append("<a href=\"/login/\" class=\"log-info-icon\"><i class=\"glyphicon glyphicon-log-in\"></i>登录</a>");
		}
		return buffer.toString();
	}
}
