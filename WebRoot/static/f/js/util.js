"use strict";
var util = {};
(function(me){
	me.timeInterval = function(rtime, otime){
		var interval = Number(rtime) - Number(otime);
		var one = 1000 * 60 * 60 * 60 * 12;//一天
		var odate = new Date(otime);
		var oy = odate.getFullYear();
		var om = odate.getMonth() + 1;
		om = om < 10 ? '0' + om : om;
		var od = odate.getDate();
		od = od < 10 ? '0' + od : od;
		var oh = odate.getHours();
		oh = oh < 10 ? '0' + oh : oh;
		var omi = odate.getMinutes();
		omi = omi < 10 ? '0' + omi : omi;
		var res = oh+':'+omi;
		if(interval > 3 * one){
			var ry = new Date(rtime).getFullYear();
			if(ry > oy)
				res = oy + ' 年 ' + om + ' 月 ' + od + ' 日 ' + res;
			else
				res = om + ' 月 ' + od + ' 日 ' + res;
		}else if(interval > 2 * one)
			res = '前天 ' + res;
		else if(interval > one)
			res = '昨天 ' + res;
		return res;
	};
	me.getDateTime = function(time, t, rt){
		var date = new Date(Number(time));
		var year = date.getFullYear();
		year = year < 10 ? ('0' + year) : year;
		var month = date.getMonth() + 1;
		month = month < 10 ? ('0' + month) : month;
		var day = date.getDate();
		day = day < 10 ? ('0' + day) : day;
		var hour = date.getHours();
		hour = hour < 10 ? ('0' + hour) : hour;
		var min = date.getMinutes();
		min = min < 10 ? ('0' + min) : min;
		var sec = date.getSeconds();
		sec = sec < 10 ? ('0' + sec) : sec;
		if(rt == 0){//返回单个值
			if(t === 'year')
				return year;
			else if(t === 'month')
				return month;
			else if(t === 'day')
				return day;
		}else//返回数组
			return [year, month, day, hour, min, sec]; 
	};
})(util);



















