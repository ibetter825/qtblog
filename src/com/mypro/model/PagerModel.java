package com.mypro.model;

import java.util.Enumeration;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

public class PagerModel{
	private Integer pageSize;
	private Integer curPage;
	private String sortName;
	private String sortOrder;
	
	public PagerModel(Controller controller) {
		this.pageSize = controller.getParaToInt("rowCount", 25);
		this.curPage = controller.getParaToInt("current", 1);
		this.sortName = controller.getPara("sortName");
		Enumeration<String> paraNames = controller.getParaNames();
		String name = null;
		for(Enumeration<String> e=paraNames;e.hasMoreElements();){
		       name=e.nextElement().toString();
		       if(name.contains("sort[")){
		    	   this.sortName = name.substring(5, name.length() - 1);
		    	   this.sortOrder = controller.getPara(name, "desc");
		    	   break;
		       }
		}
	}
	public PagerModel(){}
	
	public String getPagerSql(String sql){
		StringBuffer buffer = new StringBuffer("select * from ("); 
		buffer.append(sql);
		buffer.append(") as pager where 1 = 1 ");
		if(!StringUtils.isEmpty(sortName)){
			buffer.append(" order by ");
			String[] names = sortName.split(",");
			String[] orders = sortOrder.split(",");
			for (int i = 0; i < names.length; i++){
				buffer.append(names[i] + " " + orders[i]);
				if(i < names.length - 1)
					buffer.append(",");
			}
		}
		buffer.append(" limit ");
		buffer.append(((this.curPage - 1) * this.pageSize) + "," + this.pageSize);
		return buffer.toString();
	}
	
	public String getOrderSql(String sql){
		StringBuffer buffer = new StringBuffer(sql); 
		if(!StringUtils.isEmpty(sortName)){
			buffer.append(" order by ");
			String[] names = sortName.split(",");
			String[] orders = sortOrder.split(",");
			for (int i = 0; i < names.length; i++){
				buffer.append(names[i] + " " + orders[i]);
				if(i < names.length - 1)
					buffer.append(",");
			}
		}
		return buffer.toString();
	}

	/**
	 * 获取分页导航条
	 * @param url 地址栏
	 * @param page
	 * @param sept 地址栏分隔符
	 * @param isPjax 是否需要pajx支持
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getPaginationHtml(String url, Page page, char sept, boolean isPjax){
		StringBuffer buffer = new StringBuffer();
		String clz = "";
		if(isPjax) clz = "pjax";
		int total = page.getTotalPage();
		int pnum = page.getPageNumber();
		int left = 0, right = 0;
		boolean active = true;
		if(pnum > total){
			pnum = total;
			active = false;
		}
		if(total > 1){
			buffer.append("<div class=\"pagination pagination-multi\"><ul>");
			left = pnum - 3 <= 0 ? 1 : pnum - 3;
			right = pnum + 3 >= total ? total : pnum + 3;
			if(left > 1){
				buffer.append("<li><a class=\""+clz+"\" href=\""+url+"\">首页</a></li>");
				buffer.append("<li class=\"prev-page\"><a class=\""+clz+"\" href=\""+url+sept+(pnum-1)+"\">上一页</a></li>");
				buffer.append("<li><span> ... </span></li>");
			}
			for(int i = left; i < pnum; i++)
				buffer.append("<li><a class=\""+clz+"\" href=\""+url+sept+i+"\">"+i+"</a></li>");
			if(active)
				buffer.append("<li class=\"active\"><span>"+pnum+"</span></li>");
			else
				buffer.append("<li><a class=\""+clz+"\" href=\""+url+sept+pnum+"\">"+pnum+"</a></li>");
			for(int i = pnum + 1; i <= right; i++)
				buffer.append("<li><a class=\""+clz+"\" href=\""+url+sept+i+"\">"+i+"</a></li>");
			if(right < total){
				buffer.append("<li><span> ... </span></li>");
				buffer.append("<li class=\"prev-page\"><a class=\""+clz+"\" href=\""+url+sept+(pnum+1)+"\">下一页</a></li>");
				buffer.append("<li><a class=\""+clz+"\" href=\""+url+"\">尾页</a></li>");
			}
			buffer.append("<li><span>共 "+total+" 页</span></li>");
			buffer.append("</ul></div>");
		}
		return buffer.toString();
	}
	
	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getCurPage() {
		return curPage;
	}

	public void setCurPage(Integer curPage) {
		this.curPage = curPage;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
	
}
