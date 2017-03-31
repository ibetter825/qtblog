$(function(){
	if(type !== 'reg')
		$('#acc_str')[0].focus();
	$('.sub-menu').parent().hover(function(){$(this).find('.sub-menu').css('visibility','visible');},function(){$(this).find('.sub-menu').css('visibility','hidden');});
	$('#acc_pwd').on('focus', function(){
		var ipt = $(this);
		var type = ipt.attr('type');
		if(type !== 'password')
			ipt.attr('type', 'password');
	});
	$('#captcha-img').on('click', function(){
		$(this).attr('src', jui.uri+'/captcha?'+new Date().getTime());
	});
	$(".signup-form input").on("focus",function(){
		$(this).parent().addClass("border");
	});
	$(".signup-form input").on("blur",function(){
		$(this).parent().removeClass("border");
	})
	//注册方式切换
	$(".signup-select").on("click",function(){
		var _text=$(this).text();
		var $_input=$(this).prev();
		$_input.val('');
		if(_text=="手机注册"){
			$(".signup-tel").fadeIn(200);
			$(".signup-email").fadeOut(180);
			$(this).text("邮箱注册");
			$_input.attr("placeholder","手机号码");
		}
		if(_text=="邮箱注册"){
			$(".signup-tel").fadeOut(180);
			$(".signup-email").fadeIn(200);
			$(this).text("手机注册");
			$_input.attr("placeholder","邮箱");
		}
	});
	//步骤切换
	var _boxCon=$(".box-con");
	$(".move-login").on("click",function(){
		$('.active').removeClass('active');
		$(this).addClass('active');
		$(_boxCon).css({
			'marginLeft':0
		});
	});
	$(".move-signup").on("click",function(){
		$('.active').removeClass('active');
		$(this).addClass('active');
		$(_boxCon).css({
			'marginLeft':-320
		});
	});
	$(".move-reset").on("click",function(){
		$(_boxCon).css({
			'marginLeft':-640
		});
	});
	$("body").on("click",".move-addinf",function(){
		$(_boxCon).css({
			'marginLeft':-960
		});
	});
	if(type === 'reg')
		$('.move-signup')[0].click();
	//获取短信验证码
	var messageVerify=function (){
		$(".get-message").on("click",function(){
			if(_handle){
				$("#message-inf").fadeIn(100)
				$(this).html('<a href="javascript:;">下一步</a><img class="loading" src='+jui.uri+'"/static/f/images/loading.gif">').addClass("move-addinf");
			}
		});
	}();
	
	var valiObj = {
		  promptPosition: 'centerRight:0, -2',
		  onSuccess: false,
		  scroll: false,
		  ajaxFormValidation: true,
		  maxErrorsPerField: 1,
		  showOneMessage: true,
		  ajaxFormValidationURL: jui.uri+'/login/dologin',
		  ajaxFormValidationMethod: 'post',
		  onBeforeAjaxFormValidation: function(form, options){
			  layer.load();
		  },
		  onAjaxFormComplete: function(status, form, json, options){
			  	layer.closeAll('loading');
			  	$('#captcha').val('');
				if(json.success){//修改成功
					layer.msg("登录成功，将自动跳转", {time:800, icon:1}, function(){
						var redirectUrl = $('#redirectUrl').val();
						if(redirectUrl && redirectUrl.indexOf('/login') == -1)
							location.href = redirectUrl;
						else
							location.href = jui.uri+'/';
					});
				}else{
					$('#captcha-img').attr('src', jui.uri+'/captcha?'+new Date().getTime());
					var errs = json.data, isEmpty = true;
					for(var e in errs){
						isEmpty = false;
						$('#'+e).validationEngine('showPrompt',errs[e],'error');
					}
					if(isEmpty)
						layer.msg(json.msg, {time:1000, icon:5});
				}
	      }
	};
    $("#login-form").validationEngine('attach', valiObj);
	
});
function reg(){
	layer.msg('暂不提供注册功能...', {icon: 5, time: 1000});
}
