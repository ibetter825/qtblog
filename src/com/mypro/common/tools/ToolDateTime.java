package com.mypro.common.tools;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 日期时间相关
 * 
 * @author 董华健 2012-9-7 下午1:58:46
 */
public class ToolDateTime {

	private static Logger log = Logger.getLogger(ToolDateTime.class);
	
	public static final String pattern_ymd = "yyyy-MM-dd"; // pattern_ymd
	public static final String pattern_ymd_hms = "yyyy-MM-dd HH:mm:ss"; // pattern_ymdtime
	public static final String pattern_ymd_hms_s = "yyyy-MM-dd HH:mm:ss:SSS"; // pattern_ymdtimeMillisecond
	private static final long ONE_MINUTE = 60;  
	private static final long ONE_HOUR = 3600;  
	private static final long ONE_DAY = 86400;  
	private static final long ONE_MONTH = 2592000;  
	private static final long ONE_YEAR = 31104000;  
	
	/**
	 * 主要是给jfinal使用，数据库只认java.sql.*
	 * @param date
	 * @return
	 */
	public static Timestamp getSqlTimestamp(Date date){
		if(null == date){
			date = new Date();
		}
		return getSqlTimestamp(date.getTime());
	}

	/**
	 * 主要是给jfinal使用，数据库只认java.sql.*
	 * @param time
	 * @return
	 */
	public static Timestamp getSqlTimestamp(long time){
		return new java.sql.Timestamp(time);
	}
	
	/**
	 * 获取当前时间
	 * @return
	 */
	public static Date getDate(){
		return new Date();
	}

	/**
	 * 获取当前时间的时间戳
	 * @return
	 */
	public static long getDateByTime(){
		return new Date().getTime();
	}
	
	/**
	 * 格式化
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(Date date, String pattern) {
		DateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

	/**
	 * 格式化
	 * @param date
	 * @param parsePattern
	 * @param returnPattern
	 * @return
	 */
	public static String format(String date, String parsePattern, String returnPattern) {
		return format(parse(date, parsePattern), returnPattern);
	}
	
	/**
	 * 解析
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Date parse(String date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		try {
			return format.parse(date);
		} catch (ParseException e) {
			log.error("ToolDateTime.parse异常：date值" + date + "，pattern值" + pattern);
			return null;
		}
	}

	/**
	 * 解析
	 * @param dateStr
	 * @return
	 */
	public static Date parse(String dateStr) {
		Date date = null;
		try {
			date = DateFormat.getDateTimeInstance().parse(dateStr);
		} catch (ParseException e) {
			log.error("ToolDateTime.parse异常：date值" + date);
			return null;
		}
		return date;
	}
	
	/**
	 * 两个日期的时间差，返回"X天X小时X分X秒"
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public static String getBetween(Date begin, Date end) {
		long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒
		long day = between / (24 * 3600);
		long hour = between % (24 * 3600) / 3600;
		long minute = between % 3600 / 60;
		long second = between % 60 / 60;

		StringBuilder sb = new StringBuilder();
		sb.append(day);
		sb.append("天");
		sb.append(hour);
		sb.append("小时");
		sb.append(minute);
		sb.append("分");
		sb.append(second);
		sb.append("秒");

		return sb.toString();
	}
	
	/**
	 * 返回两个日期之间隔了多少小时
	 * 
	 * @param date1
	 * @param end
	 * @return
	 */
	public static int getDateHourSpace(Date start, Date end) {
		int hour = (int) ((start.getTime() - end.getTime()) / 3600 / 1000);
		return hour;
	}
	
	/**
	 * 返回两个日期之间隔了多少天
	 * 
	 * @param date1
	 * @param end
	 * @return
	 */
	public static int getDateDaySpace(Date start, Date end) {
		int day = (int) (getDateHourSpace(start, end) / 24);
		return day;
	}
	
	/**
	 * 得到某一天是星期几
	 * @param strDate 日期字符串
	 * @return String 星期几
	 */
	@SuppressWarnings("static-access")
	public static String getDateInWeek(Date date) {
		String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayIndex = calendar.get(calendar.DAY_OF_WEEK) - calendar.SUNDAY;
		if (dayIndex < 0){
			dayIndex = 0;
		}
		return weekDays[dayIndex];
	}
	
	/**
	 * 日期减去多少个小时
	 * @param date
	 * @param hourCount	多少个小时
	 * @return
	 */
	public static Date getDateReduceHour(Date date, long hourCount){
		long time = date.getTime() - 3600 * 1000 * hourCount;
		Date dateTemp = new Date();
		dateTemp.setTime(time);
		return dateTemp;
	}
	
	/**
	 * 日期区间分割
	 * @param start
	 * @param end
	 * @param splitCount
	 * @return
	 */
	public static List<Date> getDateSplit(Date start, Date end, long splitCount){
		long startTime = start.getTime();
		long endTime = end.getTime();
		long between = endTime - startTime;

		long count = splitCount - 1l;
		long section = between / count;
		
		List<Date> list = new ArrayList<Date>();
		list.add(start);
		
		for (long i = 1l ; i < count; i++) {
			long time = startTime + section * i;
			Date date = new Date();
			date.setTime(time);
			list.add(date);
		}
		
		list.add(end);
		
		return list;
	}
	
	/**
	 * 返回两个日期之间隔了多少天，包含开始、结束时间
	 * @param start
	 * @param end
	 * @return
	 */
	public static List<String> getDaySpaceDate(Date start, Date end) {
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTime(start);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 0);
		fromCalendar.set(Calendar.MILLISECOND, 0);

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(end);
		toCalendar.set(Calendar.HOUR_OF_DAY, 0);
		toCalendar.set(Calendar.MINUTE, 0);
		toCalendar.set(Calendar.SECOND, 0);
		toCalendar.set(Calendar.MILLISECOND, 0);

		List<String> dateList = new LinkedList<String>();

		long dayCount = (toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24);
		if(dayCount < 0){
			return dateList;
		}

		dateList.add(format(fromCalendar.getTime(), pattern_ymd));
		
		for (int i = 0; i < dayCount; i++) {
			fromCalendar.add(Calendar.DATE, 1);// 增加一天
			dateList.add(format(fromCalendar.getTime(), pattern_ymd));
		}

		return dateList;
	}
	
	/**
	 * 获取开始时间
	 * @param start
	 * @param end
	 * @return
	 */
	public static Date startDateByDay(Date start, int end){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start);
		calendar.add(Calendar.DATE, end);// 明天1，昨天-1
		calendar.set(Calendar.HOUR_OF_DAY, 0);   
		calendar.set(Calendar.MINUTE, 0);   
		calendar.set(Calendar.SECOND, 0);   
		calendar.set(Calendar.MILLISECOND, 0);   
		Date date = calendar.getTime();
		return date;
	}
	
	/**
	 * 获取结束时间
	 * @param start
	 * @param end
	 * @return
	 */
	public static Date endDateByDay(Date start){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start);
		calendar.set(Calendar.HOUR_OF_DAY, 23);   
		calendar.set(Calendar.MINUTE, 59);   
		calendar.set(Calendar.SECOND, 59);   
		calendar.set(Calendar.MILLISECOND, 999);   
		Date date = calendar.getTime();
		return date;
	}

	/**
	 * 获取开始时间
	 * @param start
	 * @param end
	 * @return
	 */
	public static Date startDateByHour(Date start, int end){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start); 
		calendar.set(Calendar.MINUTE, end);   
		Date date = calendar.getTime();
		return date;
	}
	
	/**
	 * 获取结束时间
	 * @param start
	 * @param end
	 * @return
	 */
	public static Date endDateByHour(Date end){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(end); 
		calendar.set(Calendar.SECOND, 59);   
		calendar.set(Calendar.MILLISECOND, 999);   
		Date date = calendar.getTime();
		return date;
	}
	
	/**
	 * 根据年份和周得到周的开始和结束日期
	 * @param year
	 * @param week
	 * @return
	 */
	public static Map<String, Date> getStartEndDateByWeek(int year, int week){
		Calendar weekCalendar = new GregorianCalendar();
		weekCalendar.set(Calendar.YEAR, year);
		weekCalendar.set(Calendar.WEEK_OF_YEAR, week);
		weekCalendar.set(Calendar.DAY_OF_WEEK, weekCalendar.getFirstDayOfWeek());
		
		Date startDate = weekCalendar.getTime(); // 得到周的开始日期

		weekCalendar.roll(Calendar.DAY_OF_WEEK, 6);
		Date endDate = weekCalendar.getTime(); // 得到周的结束日期
		
		// 开始日期往前推一天
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		startCalendar.add(Calendar.DATE, 1);// 明天1，昨天-1
		startCalendar.set(Calendar.HOUR_OF_DAY, 0);   
		startCalendar.set(Calendar.MINUTE, 0);   
		startCalendar.set(Calendar.SECOND, 0);   
		startCalendar.set(Calendar.MILLISECOND, 0);   
		startDate = startCalendar.getTime();

		// 结束日期往前推一天
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);
		endCalendar.add(Calendar.DATE, 1);// 明天1，昨天-1
		endCalendar.set(Calendar.HOUR_OF_DAY, 23);   
		endCalendar.set(Calendar.MINUTE, 59);   
		endCalendar.set(Calendar.SECOND, 59);   
		endCalendar.set(Calendar.MILLISECOND, 999);   
		endDate = endCalendar.getTime();
		
		Map<String, Date> map = new HashMap<String, Date>();
		map.put("start", startDate);
		map.put("end", endDate);
		return map;
	}
	
	/**
	 * 根据日期月份，获取月份的开始和结束日期
	 * @param date
	 * @return
	 */
	public static Map<String, Date> getMonthDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);   
		calendar.set(Calendar.MINUTE, 0);   
		calendar.set(Calendar.SECOND, 0);   
		calendar.set(Calendar.MILLISECOND, 0);
		
		// 得到前一个月的第一天
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		Date start = calendar.getTime();
		
		// 得到前一个月的最后一天
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date end = calendar.getTime();
		
		Map<String, Date> map = new HashMap<String, Date>();
		map.put("start", start);
		map.put("end", end);
		return map;
	}

	/**
	 * 获取指定日期
	 * @param date
	 * @param hour
	 * @param minute
	 * @param second
	 * @param millisecond
	 * @return
	 */
	public static Date getDate(int date, int hour, int minute, int second, int millisecond) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, date); 
		calendar.set(Calendar.HOUR_OF_DAY, hour);   
		calendar.set(Calendar.MINUTE, minute);   
		calendar.set(Calendar.SECOND, second);   
		calendar.set(Calendar.MILLISECOND, millisecond);  
		return calendar.getTime();
	}
	/**
	 * 根据时间戳获取时间字符串
	 * @param time
	 * @param sep
	 * @param format
	 * @return
	 */
	public static String getDate(Long time, String format){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		String date = null;
		if(pattern_ymd.equalsIgnoreCase(format))
			date = year + "-" + (month < 10?("0"+month):(month+"")) + "-" + (day < 10?("0"+day):(day+""));
		else if(pattern_ymd_hms.equalsIgnoreCase(format)){
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int mini = calendar.get(Calendar.MINUTE);
			int seco = calendar.get(Calendar.SECOND);
			date = year + "-" + (month < 10?("0"+month):(month+"")) + "-" + (day < 10?("0"+day):(day+""))
				+ " " + (hour < 10?("0"+hour):(hour+"")) + ":" + (mini < 10?("0"+mini):(mini+"")) + ":" + (seco < 10?("0"+seco):(seco+""));
		}
		return date;
	}
	/**
	 * 友好的时间显示格式
	 * @param date
	 * @return
	 */
	public static String fromToday(long date){  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTimeInMillis(date);
        long time = date / 1000;  
        long now = new Date().getTime() / 1000;  
        long ago = now - time;  
        if(ago <= ONE_HOUR){  
            return ago / ONE_MINUTE + "分钟前";  
        }  
        else if(ago <= ONE_DAY){  
            return ago / ONE_HOUR + "小时" + (ago % ONE_HOUR / ONE_MINUTE)+ "分钟前";  
        }  
        else if(ago <= ONE_DAY * 2){  
            return "昨天" + calendar.get(Calendar.HOUR_OF_DAY) + "点"+ calendar.get(Calendar.MINUTE) + "分";  
        }  
        else if (ago <= ONE_DAY * 3){  
            return "前天" + calendar.get(Calendar.HOUR_OF_DAY) + "点" + calendar.get(Calendar.MINUTE) + "分";  
        }  
        else if (ago <= ONE_MONTH){  
            long day = ago / ONE_DAY;  
            if(day < 7)  
               return day + "天前" ;  
            else if (day >= 7 && day <14)  
                return "1周前";  
            else if (day >=14 && day <21)  
                return "2周前";  
            else if (day >= 21 && day <28)  
                return "3周前";  
            else   
                return "4周前";  
        }  
        else if (ago <= ONE_YEAR){  
            long month = ago / ONE_MONTH;  
               long day = ago % ONE_MONTH / ONE_DAY;  
               return month + "个月" + day + "天前";  
        }  
        else {  
               long year = ago / ONE_YEAR;  
               int month = calendar.get(Calendar.MONTH) + 1;// JANUARY which is 0 so month+1  
               return year + "年前" + month + "月" + calendar.get(Calendar.DATE)  
                       + "日";  
        }  
    }  
 
	/**
	 * 获取时间节点，结果为数组 [年，月，日，时，分，秒, 毫秒]
	 * @return
	 */
	public static Integer[] getTimeNode(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		Integer[] node = new Integer[7];
		node[0] = calendar.get(Calendar.YEAR);
		node[1] = calendar.get(Calendar.MONTH) + 1;
		node[2] = calendar.get(Calendar.DAY_OF_MONTH);
		node[3] = calendar.get(Calendar.HOUR_OF_DAY);
		node[4] = calendar.get(Calendar.MINUTE);
		node[5] = calendar.get(Calendar.SECOND);
		node[6] = calendar.get(Calendar.MILLISECOND);
		return node;
	}
	public static void main(String[] args) throws ParseException{
//		System.out.println(format("2013-07-01", pattern_ymd, "MM-dd"));
		
//		Date start = parse("2013-07-01 01:00:00", pattern_ymdtime);
//		Date end = parse("2013-07-01 12:00:00", pattern_ymdtime);
//		long splitCount = 12l;
//		List<Date> list = getDateSplit(start, end, splitCount);
//		for (Date date : list) {
//			System.out.println(format(date, pattern_ymdtime));
//		}
		
//		Date start = parse("2013-07-01 01:00:00", pattern_ymdtime);
//		Date end = parse("2013-07-05 12:00:00", pattern_ymdtime);
//		List<String> list = getDaySpaceDate(start, end);
//		for (String str : list) {
//			System.out.println(str);
//		}
		
//		Date start = parse("2013-07-01 01:00:00", pattern_ymdtime);
//		Date end = endDate(start, 7);
//		System.out.println(format(end, pattern_ymdtime));

//		Date endDate = ToolDateTime.endDate(new Date());
//		Date startDate = ToolDateTime.startDate(endDate, -14);
//		System.out.println(format(startDate, pattern_ymdtimeMillisecond));
//		System.out.println(format(endDate, pattern_ymdtimeMillisecond));
		
//		Date endDate = ToolDateTime.endDateByHour(new Date());
//		Date startDate = ToolDateTime.startDateByHour(endDate, -24);
//		
//		System.out.println(format(startDate, pattern_ymdtimeMillisecond));
//		System.out.println(format(endDate, pattern_ymdtimeMillisecond));
		
	}

}
