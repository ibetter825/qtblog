/**
 * 
 */
package com.mypro.common.tools;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.StringUtils;

/**
 * @author jack.li
 * 
 */
@SuppressWarnings("unchecked")
public class IPUtils {

	private static final String IP_URL = "http://whois.pconline.com.cn/ip.jsp?ip=";// "http://www.youdao.com/smartresult-xml/search.s?type=ip&q=";
	//private static final String LOCATION_XPATH = "/smartresult/product/location";
	private static final String[] PROVINCE = "北京|上海|天津|重庆|香港|澳门|广东|河北|山西|内蒙古|辽宁|吉林|黑龙江|江苏|浙江|安徽|福建|江西|山东|河南|湖北|湖南|广西|海南|四川|贵州|云南|西藏|陕西|甘肃|青海|宁夏|新疆|台湾"
			.split("\\|");
	@SuppressWarnings("rawtypes")
	public static Map CACHE_MAP = new HashMap();
	static {
		CACHE_MAP.put("北京", "CN.BJ");
		CACHE_MAP.put("上海", "CN.SH");
		CACHE_MAP.put("天津", "CN.TJ");
		CACHE_MAP.put("重庆", "CN.CQ");
		CACHE_MAP.put("香港", "CN.HK");
		CACHE_MAP.put("澳门", "CN.MA");
		CACHE_MAP.put("广东", "CN.GD");
		CACHE_MAP.put("河北", "CN.HB");
		CACHE_MAP.put("山西", "CN.SX");
		CACHE_MAP.put("内蒙古", "CN.NM");
		CACHE_MAP.put("辽宁", "CN.LN");
		CACHE_MAP.put("吉林", "CN.JL");
		CACHE_MAP.put("黑龙江", "CN.HL");
		CACHE_MAP.put("江苏", "CN.JS");
		CACHE_MAP.put("浙江", "CN.ZJ");
		CACHE_MAP.put("安徽", "CN.AH");
		CACHE_MAP.put("福建", "CN.FJ");
		CACHE_MAP.put("江西", "CN.JX");
		CACHE_MAP.put("山东", "CN.SD");
		CACHE_MAP.put("河南", "CN.HE");
		CACHE_MAP.put("湖北", "CN.HU");
		CACHE_MAP.put("湖南", "CN.HN");
		CACHE_MAP.put("广西", "CN.GX");
		CACHE_MAP.put("海南", "CN.HA");
		CACHE_MAP.put("四川", "CN.SC");
		CACHE_MAP.put("贵州", "CN.GZ");
		CACHE_MAP.put("云南", "CN.YN");
		CACHE_MAP.put("西藏", "CN.XZ");
		CACHE_MAP.put("陕西", "CN.SA");
		CACHE_MAP.put("甘肃", "CN.GS");
		CACHE_MAP.put("青海", "CN.QH");
		CACHE_MAP.put("宁夏", "CN.NX");
		CACHE_MAP.put("新疆", "CN.XJ");
		CACHE_MAP.put("台湾", "CN.TA");
		Collections.unmodifiableMap(CACHE_MAP);
	}
	@SuppressWarnings("rawtypes")
	private static Map CACHE = new LRUMap(100);

	public static String[] getIPLocation(String ip) {
		String[] ret = (String[]) CACHE.get(ip);
		if (ret == null) {
			String str = HttpClientUtils.get(IP_URL + ip);
			if (StringUtils.isNotEmpty(str)) {
				ret = str.split("\\s");
				if(StringUtils.isEmpty(ret[0])) {
					ret[0] = ret[1];
					ret[1] = "未知";
				}
			} else {
				ret = new String[] { "未知", "未知" };
			}
		}
		CACHE.put(ip, ret);
		return ret;
	}

	public static String getProvince(String str) {
		String temp = str;
		String s = null;
		for (int i = 0; i < PROVINCE.length; i++) {
			s = PROVINCE[i];
			if (str.indexOf(s) > -1) {
				temp = s;
				break;
			}
		}
		return temp;
	}

	public static String getChinaMap(String str) {
		return (String) CACHE_MAP.get(str);
	}

	public static long ip2long(String ip) {
		String[] ips = ip.split("[.]");
		long num = 16777216L * Long.parseLong(ips[0]) + 65536L
				* Long.parseLong(ips[1]) + 256 * Long.parseLong(ips[2])
				+ Long.parseLong(ips[3]);
		return num;
	}

	/**
	 * 整数转成ip地址.
	 * 
	 * @param ipLong
	 * @return
	 */
	public static String long2ip(long ipLong) {
		// long ipLong = 1037591503;
		long mask[] = { 0x000000FF, 0x0000FF00, 0x00FF0000, 0xFF000000 };
		long num = 0;
		StringBuffer ipInfo = new StringBuffer();
		for (int i = 0; i < 4; i++) {
			num = (ipLong & mask[i]) >> (i * 8);
			if (i > 0)
				ipInfo.insert(0, ".");
			ipInfo.insert(0, Long.toString(num, 10));
		}
		return ipInfo.toString();
	}

	public static void main(String[] args) {
		String[] ip = IPUtils.getIPLocation("118.113.54.144");
		System.out.println(ip[0]);
		System.out.println(ip[1]);
	}

}
