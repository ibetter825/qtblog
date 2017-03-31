//timeago
(function (factory) { 
	 if (typeof define === 'function' && define.amd) { 
	  // AMD. Register as an anonymous module. 
	  define(['jquery'], factory); 
	 } else { 
	  // Browser globals 
	  factory(jQuery); 
	 } 
	}(function ($) { 
	 $.timeago = function(timestamp) { 
	  if (timestamp instanceof Date) { 
	   return inWords(timestamp); 
	  } else if (typeof timestamp === "string") { 
	   return inWords($.timeago.parse(timestamp)); 
	  } else if (typeof timestamp === "number") { 
	   return inWords(new Date(timestamp)); 
	  } else { 
	   return inWords($.timeago.datetime(timestamp)); 
	  } 
	 }; 
	 var $t = $.timeago; 
	 $.extend($.timeago, { 
	  settings: { 
	   refreshMillis: 60000, 
	   allowFuture: false, 
	   localeTitle: false, 
	   cutoff: 0, 
	   strings: { 
	    prefixAgo: null, 
	    prefixFromNow: null, 
	    suffixAgo: "前", 
	    suffixFromNow: "from now", 
	    seconds: "1分钟", 
	    minute: "1分钟", 
	    minutes: "%d分钟", 
	    hour: "1小时", 
	    hours: "%d小时", 
	    day: "1天", 
	    days: "%d天", 
	    month: "1月", 
	    months: "%d月", 
	    year: "1年", 
	    years: "%d年", 
	    wordSeparator: "", 
	    numbers: [] 
	   } 
	  }, 
	  inWords: function(distanceMillis) { 
	   var $l = this.settings.strings; 
	   var prefix = $l.prefixAgo; 
	   var suffix = $l.suffixAgo; 
	   if (this.settings.allowFuture) { 
	    if (distanceMillis < 0) { 
	     prefix = $l.prefixFromNow; 
	     suffix = $l.suffixFromNow; 
	    } 
	   } 
	   var seconds = Math.abs(distanceMillis) / 1000; 
	   var minutes = seconds / 60; 
	   var hours = minutes / 60; 
	   var days = hours / 24; 
	   var years = days / 365; 
	   function substitute(stringOrFunction, number) { 
	    var string = $.isFunction(stringOrFunction) ? stringOrFunction(number, distanceMillis) : stringOrFunction; 
	    var value = ($l.numbers && $l.numbers[number]) || number; 
	    return string.replace(/%d/i, value); 
	   } 
	   var words = seconds < 45 && substitute($l.seconds, Math.round(seconds)) || 
	    seconds < 90 && substitute($l.minute, 1) || 
	    minutes < 45 && substitute($l.minutes, Math.round(minutes)) || 
	    minutes < 90 && substitute($l.hour, 1) || 
	    hours < 24 && substitute($l.hours, Math.round(hours)) || 
	    hours < 42 && substitute($l.day, 1) || 
	    days < 30 && substitute($l.days, Math.round(days)) || 
	    days < 45 && substitute($l.month, 1) || 
	    days < 365 && substitute($l.months, Math.round(days / 30)) || 
	    years < 1.5 && substitute($l.year, 1) || 
	    substitute($l.years, Math.round(years)); 
	   var separator = $l.wordSeparator || ""; 
	   if ($l.wordSeparator === undefined) { separator = " "; } 
	   return $.trim([prefix, words, suffix].join(separator)); 
	  }, 
	  parse: function(iso8601) { 
	   var s = $.trim(iso8601); 
	   s = s.replace(/\.\d+/,""); // remove milliseconds 
	   s = s.replace(/-/,"/").replace(/-/,"/"); 
	   s = s.replace(/T/," ").replace(/Z/," UTC"); 
	   s = s.replace(/([\+\-]\d\d)\:?(\d\d)/," $1$2"); // -04:00 -> -0400
	   return new Date(s); 
	  }, 
	  datetime: function(elem) { 
	   var iso8601 = $t.isTime(elem) ? $(elem).attr("datetime") : $(elem).attr("title"); 
	   return $t.parse(iso8601); 
	  }, 
	  isTime: function(elem) { 
	   // jQuery's `is()` doesn't play well with HTML5 in IE 
	   return $(elem).get(0).tagName.toLowerCase() === "time"; // $(elem).is("time"); 
	  } 
	 }); 
	 // functions that can be called via $(el).timeago('action') 
	 // init is default when no action is given 
	 // functions are called with context of a single element 
	 var functions = { 
	  init: function(){ 
	   var refresh_el = $.proxy(refresh, this); 
	   refresh_el(); 
	   var $s = $t.settings; 
	   if ($s.refreshMillis > 0) { 
	    setInterval(refresh_el, $s.refreshMillis); 
	   } 
	  }, 
	  update: function(time){ 
	   $(this).data('timeago', { datetime: $t.parse(time) }); 
	   refresh.apply(this); 
	  }, 
	  updateFromDOM: function(){ 
	   $(this).data('timeago', { datetime: $t.parse( $t.isTime(this) ? $(this).attr("datetime") : $(this).attr("title") ) }); 
	   refresh.apply(this); 
	  } 
	 }; 
	 $.fn.timeago = function(action, options) { 
	  var fn = action ? functions[action] : functions.init; 
	  if(!fn){ 
	   throw new Error("Unknown function name '"+ action +"' for timeago"); 
	  } 
	  // each over objects here and call the requested function 
	  this.each(function(){ 
	   fn.call(this, options); 
	  }); 
	  return this; 
	 }; 
	 function refresh() { 
	  var data = prepareData(this); 
	  var $s = $t.settings; 
	  if (!isNaN(data.datetime)) { 
	   if ( $s.cutoff == 0 || distance(data.datetime) < $s.cutoff) { 
	    $(this).text(inWords(data.datetime)); 
	   } 
	  } 
	  return this; 
	 } 
	 function prepareData(element) { 
	  element = $(element); 
	  if (!element.data("timeago")) { 
	   element.data("timeago", { datetime: $t.datetime(element) }); 
	   var text = $.trim(element.text()); 
	   if ($t.settings.localeTitle) { 
	    element.attr("title", element.data('timeago').datetime.toLocaleString()); 
	   } else if (text.length > 0 && !($t.isTime(element) && element.attr("title"))) { 
	    element.attr("title", text); 
	   } 
	  } 
	  return element.data("timeago"); 
	 } 
	 function inWords(date) { 
	  return $t.inWords(distance(date)); 
	 } 
	 function distance(date) { 
	  return (new Date().getTime() - date.getTime()); 
	 } 
	 // fix for IE6 suckage 
	 document.createElement("abbr"); 
	 document.createElement("time"); 
	}));
//detail
getComtsByAjax();
function getComtsByAjax(){
	var mc = $('.more-comts'); 
	mc.addClass('more-comts-loading').find('a').hide();
	$.post(jui.uri+'/comt/'+artno+'-'+Number(jui.ajaxpager)+'-10', function(data){
    	var comts = data.comts;
    	if(!comts || comts.length == 0){
    		if(Number(jui.ajaxpager) > 1)
    			layer.msg("没有更多评论了", {time:800, icon:5});
    		mc.hide();
    		return;
    	}
    	mc.removeClass('more-comts-loading').find('a').show();
    	jui.ajaxpager = Number(jui.ajaxpager)+1;
    	var replies = data.replies;
    	var rtime = data.rtime;
    	var raccno = data.raccno;//当前登录的用户
    	var dom = [], dto = null;
    	for(var i = 0; i < comts.length; i++){
    		dto = comts[i];
    		dom.push(createDetailComtOrReplyHtml(rtime, raccno, dto, 1).join(''));
    	}
    	$('#comment-list').append(dom.join(''));
    	dto = null;
    	for(var i = 0; i < replies.length; i++){
    		dto = replies[i];
    		$('#comment-'+dto.comt_no+'-children').append(createDetailComtOrReplyHtml(rtime, raccno, dto, 2).join(''));
    	}
    },'json').error(function(x,t,e){
    	mc.removeClass('more-comts-loading').find('a').show();
		$.ER.handle(x,t,e);
	});
}

function createDetailComtOrReplyHtml(rtime, raccno, dto, type){
	var dto_no = type == 1 ? dto.comt_no:dto.reply_no;
	var comt_no = dto.comt_no;
	var li_id = type == 1 ? ('li-comment-'+dto_no):('li-reply-'+dto_no);
	var div_id = type == 1 ? ('comment-'+dto_no):('reply-'+dto_no);
	var dto_content = type == 1 ? dto.comt_content:dto.reply_content;
	var reply_tocom = type == 1 ? 23:24;
	var i_del = '';
	if(raccno == toacc || dto.from_acc == raccno)
		i_del = '<a href="javascript:void(0);" onclick="return addComment.D(\''+dto_no+'\','+type+');"><i title="删除" class="glyphicon glyphicon-trash"></i></a>';
	
	var dom = [];
	dom.push('<li class="comment depth-'+type+'" id="'+li_id+'">');
	dom.push('<div class="c-avatar"><a href="'+jui.uri+'/main/'+dto.from_acc+'"><img data-original="'+jui.uri+dto.from_avatar+'" class="avatar avatar-100" src="'+jui.uri+dto.from_avatar+'"></a></div>');
	dom.push('<div class="c-main" id="'+div_id+'" data-comt-no="'+comt_no+'"><span class="c-author from_name">'+dto.from_name+(type==1?'</span>：':'</span><i class="text-split">回复</i><span class="c-receiver to_name">'+dto.to_name+'</span>：')+'<span class="comt-reply-cont">'+dto_content+'</span>');
	dom.push('<time class="c-time">'+$.timeago(dto.add_time)+'</time>');
	dom.push('<span class=\'comment-reply-link\'><a rel=\'nofollow\' href=\'replytocom='+reply_tocom+'#respond\' onclick=\'return addComment.moveForm("'+div_id+'", "2", "respond", "'+dto.from_acc+'")\' title="回复"><i class="glyphicon glyphicon-comment"></i></a>'+i_del+'<a href="javascript:void(0);" onclick="return art.report('+type+','+dto_no+',this);"><i title="举报" class="glyphicon glyphicon-exclamation-sign"></i></a></span></div>');
	dom.push('<ul class="children" id="comment-'+dto_no+'-children"></ul>');
	dom.push('</li>');
	return dom;
}
+(function($) {
	/*$('.article-content img').each(function(i){
		var face = 'thumb.gif';//判断是否为表情，这个不好弄，权宜之计
		var art_img = $(this);
		var src = art_img.attr('src');
		if(src.indexOf(face) == -1){
			art_img.attr('data-original', src).removeAttr('src').lazyload({
			    placeholder: jui.uri + '/static/f/images/thumbnail.gif',
			    event: 'scrollstop'
			});
		}
	});*/
	
	var islogin = false;
	if( $.CK.get("IS_LOGIN") == 1) islogin = true;
	$('#comment').on('click', function(){
		if(!islogin){
    		$.LG.nl();
    		$('#comment').blur();
    		return false;
    	}
	});
    var edit_mode = '0',
        txt1 = '<div class="comt-tip comt-loading">正在提交, 请稍候...</div>',
        txt2 = '<div class="comt-tip comt-error">#</div>',
        txt3 = '">',
        cancel_edit = '取消编辑',
        edit,
        num = 1,
        comm_array = [];
    comm_array.push('');

    $comments = $('#comments-title');
    $cancel = $('#cancel-comment-reply-link');
    cancel_text = $cancel.text();
    $submit = $('#commentform #submit');
    $submit.attr('disabled', false);
    $('.comt-tips').append(txt1 + txt2);
    $('.comt-loading').hide();
    $('.comt-error').hide();
    $body = (window.opera) ? (document.compatMode == "CSS1Compat" ? $('html') : $('body')) : $('html,body');
    $('#commentform').submit(function() {
    	if(!islogin){
    		$.LG.nl();
    		return false;
    	}
    	var ct = $('#comment').val();
    	var re = /^\s*$/;
		if(re.test(ct))
			return false;
        $('.comt-loading').show();
        $submit.attr('disabled', true).fadeTo('slow', 0.5);
        var form = $(this);
        var t = $('#post_type').val();
        $.ajax({
            url: form.attr('action'),
            data: form.serialize(),
            type: form.attr('method'),
            error: function(request) {
                $('.comt-loading').hide();
                $('.comt-error').show().html(request.responseText);
                setTimeout(function() {
                        $submit.attr('disabled', false).fadeTo('slow', 1);
                        $('.comt-error').fadeOut();
                    },
                    3000);
            },
            success: function(data) {
                $('.comt-loading').hide();
                $submit.attr('disabled', false).fadeTo('slow', 1);
                if(data.success){
	                var dto = data.data.dto;
					var comt_no = dto.comt_no;
					dto.from_name = data.data.from_name;
					dto.from_avatar = data.data.from_avatar;
					var rtime = data.data.rtime;
					form[0].reset();
					if(t == 1){//评论
						if($('#comment-list>li').length == 0)
							$('#comment-list').append(createDetailComtOrReplyHtml(rtime, dto.from_acc, dto, t).join(''));
						else
							$('#comment-list>li').first().before(createDetailComtOrReplyHtml(rtime, dto.from_acc, dto, t).join(''));
						$("html,body").animate({scrollTop:$("#comment-list").offset().top-50},'fast');
					}else if(t == 2){//回复
						var root = $('#'+form.attr('data-root-id'));
						dto.to_name = root.find('.from_name').html();
						$('#comment-'+comt_no+'-children').append(createDetailComtOrReplyHtml(rtime, dto.from_acc, dto, t).join(''));
					}
					addComment.C();
                }else
                	layer.msg(data.msg, {icon:0});
            }
        });
        return false;
    });
    addComment = {
    		/**
    		 * @param tId 被回复的评论或回复id
    		 * @param ty 1 评论，2 回复
    		 * @param f 表单id
    		 * @param toNo 本次被回复的用户
    		 * @returns {Boolean}
    		 */
        moveForm: function(tId, ty, f, toNo) {
             //post_type 类型：1 评论，2 回复
             //post_to_acc 回复时使用,被回复的人
             //post_comt_no 回复时使用，被回复的评论
        	if(!islogin){
        		$.LG.nl();
        		return false;
        	}
            var t = this,
                div, comm = t.I(tId),
                respond = t.I(f),
                cancel = t.I('cancel-comment-reply-link'),
                type = t.I('post_type'),
                comt = t.I('post_comt_no'),
            	to = t.I('post_to_acc');
            t.respondId = f;

            div = document.createElement('div');
            div.id = 'wp-temp-form-div';
            div.style.display = 'none';
            respond.parentNode.insertBefore(div, respond);
            !comm ? (temp = t.I('wp-temp-form-div'), t.I('comment_parent').value = '0', temp.parentNode.insertBefore(respond, temp), temp.parentNode.removeChild(temp)) : comm.parentNode.insertBefore(respond, comm.nextSibling);
            
            $body.animate({scrollTop: $('#respond').offset().top - 180},400);
            type.value = ty;
            if(ty == '2'){//回复
            	t.I("commentform").setAttribute('data-root-id',tId);
    			comt.value = t.I(tId).getAttribute('data-comt-no');
    			respond.setAttribute('data-root-id', tId);
    			to.value = toNo;
    		}
            cancel.style.display = '';
            cancel.onclick = function() {
            	addComment.C();
            };
            try {
                t.I('comment').focus();
            } catch (e) {}
            return false;
        },
        I: function(e) {
            return document.getElementById(e);
        },
        C:function (){//取消回复
    		var m=this,n=addComment,e=n.I("wp-temp-form-div"),o=n.I(n.respondId),t=m.I("post_to_acc"),l=m.I("cancel-comment-reply-link");
    		if(!e||!o){
    			return;
    		}
    		n.I("post_type").value="1";
    		t.value = toacc;
    		e.parentNode.insertBefore(o,e);
    		e.parentNode.removeChild(e);
    		l.style.display="none";
    		l.onclick=null;
    		return false;
    	},
    	D:function (no, type){
    		var url = jui.uri+'/comt/del/'+no+'-'+type;
    		$.post(url,function(data){
    			if(data.success){//添加成功，将记录添加到页面
    				//删除该字段
    				if(type == 1)
    					$('#li-comment-'+no).slideUp('fast',function(){$(this).remove();});
    				else if(type == 2)
    					$('#li-reply-'+no).slideUp('fast',function(){$(this).remove();});
    			}else
    				layer.msg("操作失败,请稍后重试", {icon:5});
    		},'json');
    	}
    };
    
    function exit_prev_edit() {
        $new_comm.show();
        $new_sucs.show();
        $('textarea').each(function() {
            this.value = '';
        });
        edit = '';
    }
    var wait = 15,
        submit_val = $submit.val();

    function countdown() {
        if (wait > 0) {
            $submit.val(wait);
            wait--;
            setTimeout(countdown, 1000);
        } else {
            $submit.val(submit_val).attr('disabled', false).fadeTo('slow', 1);
            wait = 15;
        }
    }
    
    //浏览量，编辑与删除等操作
    $.get(jui.uri+'/article/info', {'artno':artno,'author':toacc}, function(data){
    	if(data != '')
    		$('#article-meta').append(data);
    });
    //点赞等内容
    jsonp(jui.uri+'/article/opts?artno='+artno, function(data){
    	$('#article-social').html(data);
    });
    //作者昵称与头像
    /*jsonp(jui.uri+'/acc/info?accno='+toacc, function(data){
    	$('#art-acc-name').html(data.name);
    	//$('#art-acc-avatar').attr('src', data.avatar);
    });*/
    //相关推荐 
    var art_theme = $('#param-art-theme').val();
    if(art_theme !== 'other'){
    	jsonp(jui.uri+'/rmd/'+art_theme, function(data){
    		if(data === ''){
    			$('#relate-rmd-container').hide();
    		}else{
	        	$('#relate-rmd').html(data);
	        	$('#relate-rmd .thumb').lazyload({
	                placeholder: jui.uri + '/static/f/images/thumbnail.gif',
	                event: 'scrollstop'
	            });
    		}
        });
    }else{
    	$('#relate-rmd-container').hide();
    }
    //热门推荐文章
    jsonp(jui.uri+'/rmd', function(data){
    	$('#hot-rmd').html(data);
    	$('#hot-rmd .thumb').lazyload({
            placeholder: jui.uri + '/static/f/images/thumbnail.gif',
            event: 'scrollstop'
        });
    });
})(jQuery);