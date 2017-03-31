package com.mypro.web.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.jfinal.kit.PropKit;
import com.mypro.common.DictKeys;

/**
 * 编码工具类
 * @author ibett
 *
 */
public class ToolStrYt {
	private static final String[] strArr = {"1","2","3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y","Z"};
	/**
	 * 编码
	 * @param str
	 * @return
	 */
	public static String UrlEncode(String str){
		try {
			return URLEncoder.encode(str, PropKit.get(DictKeys.config_encoding));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 解码
	 * @param str
	 * @return
	 */
	public static String UrlDecode(String str){
		try {
			return URLDecoder.decode(str, PropKit.get(DictKeys.config_encoding));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 数组转换成字符串
	 * @param array 字符串数组
	 * @param separator 分隔符1
	 * @param mark 包裹符
	 * @return
	 */
	public static String joinArray(String[] array, String separator, String mark){
		StringBuffer str = new StringBuffer();
		int lth = array.length;
		for (int i = 0; i < lth; i++) {
			if(i < lth - 1)
				str.append((mark==null?"":mark) + array[i] + (mark==null?"":mark) + separator);
			else
				str.append((mark==null?"":mark) + array[i] + (mark==null?"":mark));
		}
		return str.toString();
	}
	/**
	 * 将字符串数组转为数字数组
	 * @param arr
	 * @return
	 */
	public static Integer[] strArrToIntArr(String[] arr){
		int l = arr.length;
		Integer[] res = new Integer[l];
		try {
			for(int i = 0; i < l; i++)
				res[i] = Integer.valueOf(arr[i]);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 生成随机数
	 * @param lth
	 * @param upper
	 * @return
	 */
	public static String getRdmStr(int lth, boolean upper){
		StringBuffer buffer = new StringBuffer();
		Random random = new Random();
		for(int i = 0, l = strArr.length; i < lth; i++){
			buffer.append(strArr[random.nextInt(l)]);
		}
		if(upper)
			return buffer.toString();
		else
			return buffer.toString().toLowerCase();
	}
	/**
	 * 转义html标签
	 * @param str
	 * @return
	 */
	public static String escapeHtml(String content){
		String html = content;
		html = StringUtils.replace(html, "'", "&apos;");
		html = StringUtils.replace(html, "\"", "&quot;");
		html = StringUtils.replace(html, "\t", "&nbsp;&nbsp;");// 替换跳格
		//html = StringUtils.replace(html, " ", "&nbsp;");// 替换空格
		html = StringUtils.replace(html, "<", "&lt;");
		html = StringUtils.replace(html, ">", "&gt;");
		return html;
	}
}
