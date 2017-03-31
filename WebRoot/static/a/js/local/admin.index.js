(function(){
	"use strict";
	if (window != top)
		top.location.href = location.href; //跳出框架
		/* 设置main-content的高度 START*/
		resetMainContentHeight();
		$(window).resize(function(){
	  	 resetMainContentHeight();
	});
	function resetMainContentHeight(){
		$("#main-content").height($(window).height() - 100);
	}
	/* 设置main-content的高度 END*/
	
	$.post(rute.admin+'/index/menus',function(data){
	     var menus = data.data.menus;
	     $('#my-menus').append(getMenus(menus));
	     jQuery('#sidebar .sub-menu > a').click(function () {
	        var last = jQuery('.sub-menu.open', $('#sidebar'));
	        last.removeClass("open");
	        jQuery('.arrow', last).removeClass("open");
	        jQuery('.sub', last).slideUp(200);
	        var sub = jQuery(this).next();
	        if (sub.is(":visible")) {
	            jQuery('.arrow', jQuery(this)).removeClass("open");
	            jQuery(this).parent().removeClass("open");
	            sub.slideUp(200);
	        } else {
	            jQuery('.arrow', jQuery(this)).addClass("open");
	            jQuery(this).parent().addClass("open");
	            sub.slideDown(200);
	        }
	        /*var o = ($(this).offset());
	        diff = 200 - o.top;
	        if(diff>0)
	            $(".sidebar-scroll").scrollTo("-="+Math.abs(diff),500);
	        else
	            $(".sidebar-scroll").scrollTo("+="+Math.abs(diff),500);*/
	    });
	   //菜单点击后背景改变
		$('.sub-menu').find('a').on('click',function(){
			$('#my-menus').find('.active').removeClass('active');
			$(this).parent().addClass('active');
			$(this).parents('.sub-menu').addClass('active');
		});
	},'json');
	/**
	 * 获取用户菜单
	 */
	function getMenus(menus){
	   	var str = '', menu = null, l = menus.length;
	   	for(var i = 0; i < l; i++){
	   		menu = menus[i];
			if(menu.level == 0){//一级菜单
				str += '<li class="sub-menu"><a href="javascript:;" class="menu"><i class="'+menu.icon+'"></i><span>'+menu.name+'</span><span class="arrow"></span></a>';
				if(menu.children.length > 0){
					str += '<ul class="sub" style="display:block;">';//初始化不打开菜单时设置为none既可
					str +=  getMenus(menu.children);
					str += '</ul>';
				}
				str += '</li>';
			}else if(menu.level == 1)//二级菜单
				str += '<li><a class="menu" href='+rute.admin+'/operate?mid='+menu.id+' target="myFrame">'+menu.name+'</a></li>';
		}
		return str;	
	};
	/**
	 * 获取未读信息
	 */
	function getUnreadLi(list, count){
		var level = {'0':'bullhorn', '1':'warning', '2':'bolt'};
		var li = [];
		for(var i = 0; i < count; i++){
			li.push('<li><a href='+rute.admin+'/notice/detail?notice_id='+list[i].notice_id+'" target="myFrame"><span class="label label-info"><i class="icon-'+level[list[i].notice_level]+'"></i></span>&emsp;');
        li.push(list[i].notice_title);
        //li.push('</br><span class="small italic">'+yt.formatDate(new Date(list[i].add_time),'-',true)+'</span></a></li>');
		}
		li.push('<li><a href="#">查看所有系统通知</a></li>');
		return li.join('');
	};
	
	$(function(){
		$.post(rute.admin+'/notice/unread',function(data){
   			if(data.success){
   				var notices = data.data.notices;
   				var count = notices.length;
   				$('.unread-count').html(count);
   				$('.unread-notice').append(getUnreadLi(notices, count));
   			}
   		},'json');
	});
})();