"use strict";
var sys = {};
(function(me){
	me.bindDateTimePicker = function(selecter){
		$(selecter).daterangepicker({
	    	"singleDatePicker": true,
	    	"autoUpdateInput": false,
	    	"showDropdowns": true
	    },function(start, end, label) {
	    	var time = start.format('YYYY-MM-DD');
		  	$(selecter).val(time);
		  	$(selecter+'_hidden').val(yt.strToTime(time));
		});
	};
})(sys);
(function(){
	//自动绑定查询时间输入框
	$('.auto-bind-timepicker').each(function(i){
		var id = this.id;
		var constraint = $(this).attr('data-constraint');
		$(this).after('<input id="'+id+'_hidden" name="'+constraint+'" type="text" style="display:none;"/>');
		sys.bindDateTimePicker('#'+id);
	});
})();