package com.mypro.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mypro.model.front.QtNav;

/**
 * 初始化一些数据
 * @author ibett
 *
 */
public class StaticData {
	public static Map<String, String> navMapper = new HashMap<String, String>();//存放导航条的编号与名称数据
	public static Map<String, String> ruteMapper = new HashMap<String, String>();//存放导航条的本级以及父级编号
	public static void loadNavMapper(List<QtNav> navs){
		String nav_no = null, nav_name = null, nav_pno = null;
		for (QtNav n : navs){
			nav_no = n.getStr("nav_no");
			nav_name = n.getStr("nav_name");
			navMapper.put(nav_no, nav_name);
			nav_pno = n.getStr("nav_pno");
			if(StringUtils.isNotEmpty(nav_pno)){
				ruteMapper.put(nav_no, nav_no + "," + nav_pno);
			}else
				ruteMapper.put(nav_no, nav_no);
		}
	}
}
