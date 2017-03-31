"use strict";
var art = {};
(function(me){
	/**
	 * 删除文章
	 */
	me.remove = function(art_no, obj){
		layer.msg('确定要删除吗？', {
		  time: 0, //不自动关闭
		  btn: ['确定', '取消'],
		  yes: function(index){
		    layer.close(index);
		    $.post(jui.uri+"/article/del/"+art_no, function(data){
				if(data.success){
					if(obj){
						$(obj).parents('article').slideUp('fast', function(){
							$(this).remove();
						});
					}else{
						layer.msg(data.msg, {time:800, icon:1}, function(){
							location.href = jui.uri+"/zone";
						});
					}
				}else
					layer.msg('操作失败', {icon: 5});
			}).error(function(x,t,e){
				$.ER.handle(x,t,e);
			});
		  }
		});
	};
	/**
	 * 根据用户自定义类型获取用户文章列表
	 */
	me.type = function(acc, type){
		$.get(jui.uri+"/article/arts/"+acc+'-'+type, function(data){
			$('#content-main').hide().empty().append(data).slideDown('fast').find('.thumb').lazyload({
		        placeholder: jui.uri + '/static/f/images/thumbnail.gif',
		        event: 'scrollstop'
		    });
		}).error(function(x,t,e){
			$.ER.handle(x,t,e);
		});
	};
	/**
	 * 收藏,fg:0取消收藏,1:添加收藏
	 */
	me.colet = function(art, fg, obj){
    	//请求数据操作数据
    	$.get(jui.uri + '/colet/toggle/'+art+'-'+fg,function(data){
    		if(data.success){
    			if(fg === 0)//收藏文章页面，取消收藏
    				$(obj).parents('article').slideUp('fast', function(){$(this).remove();});
    		}
    	},'json').error(function(x,t,e){
			$.ER.handle(x,t,e);
		});
	};
	/**
	 * 修改文章状态
	 */
	me.state = function(art, state, obj){
		layer.msg('确定要恢复文章吗？', {
			  time: 0, //不自动关闭
			  btn: ['确定', '取消'],
			  yes: function(index){
			    layer.close(index);
			    $.get(jui.uri + '/article/state/'+art+'-'+state,function(data){
		    		if(data.success)
		    			$(obj).parents('article').slideUp('fast', function(){$(this).remove();});
		    		else
		    			layer.msg(data.msg, {icon: 5});
		    	},'json').error(function(x,t,e){
					$.ER.handle(x,t,e);
				});
			  }
			});
	};
	/**
	 * 举报文章，用户，回复，评论等
	 * type:1 评论, 2 回复, 3 文章, 4 用户
	 */
	me.report = function(type, no, obj){
		layer.prompt({
			  formType: 2,
			  maxlength: 20,
			  title: '举报原因'
			}, function(value, index, elem){
			  layer.close(index);
			  $.get(jui.uri+"/report/"+type+'-'+no+'-'+value, function(data){
				  if(data.success){
					  $(obj).remove();
					  layer.msg("举报成功，正在处理", {icon: 1});
				  }else
					  layer.msg(data.msg, {icon: 5});
			  }).error(function(x,t,e){
					$.ER.handle(x,t,e);
			  });
		});
	};
})(art);
//acc
(function(){
	var acc = {};
	acc.follow = function(acc, obj){
		var islogin = false;
	    if( $.CK.get("IS_LOGIN") == 1) islogin = true;
	    if(!islogin)
	    	return $.LG.nl();
	    var btn = $(obj);
	    var type = btn.attr('data-status');
	    var follow = $(obj).parent();
	    $.post(jui.uri+"/follow/toggle/"+acc+'-'+type, function(data){
	    	follow.empty().append(data);
		}).error(function(x,t,e){
			$.ER.handle(x,t,e);
		});
	};
	acc.follower = function(acc){
		$.post(jui.uri+"/follow/follower/"+acc, function(data){
			$('.title').hide();
			$('#content-follows').show();
	    	$('#content-main').empty().append(data);
		}).error(function(x,t,e){
			$.ER.handle(x,t,e);
		});
	};
	acc.following = function(acc){
		$.post(jui.uri+"/follow/following/"+acc, function(data){
			$('.title').hide();
			$('#content-follows').show();
	    	$('#content-main').empty().append(data);
		}).error(function(x,t,e){
			$.ER.handle(x,t,e);
		});
	};
	acc.setup = function(target){
		$('.setting-content').hide();
		if(target === 'avatar'){
			if(!$('#myxiuxiu')[0]){
				var script = document.createElement('script');
			    script.setAttribute('src', 'http://open.web.meitu.com/sources/xiuxiu.js');
			    script.id = 'myxiuxiu';
			    script.addEventListener('load', function(){
			    	  var host = window.location.host;
			    	  if(host.indexOf('http://') == -1)
			    		  host = 'http://'+host;
			    	  //第1个参数是加载编辑器div容器，第2个参数是编辑器类型，第3个参数是div容器宽，第4个参数是div容器高
					  xiuxiu.embedSWF("altContent",5,"100%","100%");
					  //修改为您自己的图片上传接口
					  xiuxiu.setUploadURL(host+jui.uri+"/upload?upload_type=avatar");
					  xiuxiu.setUploadType(2);
					  xiuxiu.setUploadDataFieldName("file");
					  xiuxiu.onInit = function (){
					    	xiuxiu.loadPhoto(host+jui.uri+acc_avatar);//修改为要处理的图片url
					  };
					  xiuxiu.onUploadResponse = function (data){
					  		var res = JSON.parse(data);
					  		if(res.success){
					  			var path = res.data.path;
					  			var url = jui.uri+'/acc/set';
					  			var param = {'info.acc_avatar':path,'info.acc_no':$('#acc_no').val(),'type':2,'prev_avatar':acc_avatar};
					  			$.post(url,param,function(data){
					  				if(data.success){
					  					$('.log-info-avatar').attr('src', path);
					  					acc_avatar = path;
					  				}
					  				layer.msg(res.msg, {time:800, icon:1});
					  			},'json').error(function(x,t,e){
					  				$.ER.handle(x,t,e);
					  			});
					  		}else
					  			layer.msg(res, {time:800, icon:5});
					  };
			    });
		        document.body.appendChild(script);
			}
		}else if(target === 'pwd'){
			$('.form-pwd').focus(function(){
				var ipt = $(this);
				var type = ipt.attr('type');
				if(type !== 'password')
					ipt.attr('type', 'password');
			});
		}
		$('#'+target+"-setting").show();
	};
	window['acc'] = acc;
})();


















