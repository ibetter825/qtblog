var yt = {};
(function(me){
	"use strict";
	me.alert = function(params, func){
		var cls = 'yellow', tcls = 'text-warning', bcls = 'btn-warning', opt = '警告';
		var msg = typeof params == 'string'?params:params.msg;
		if(params.code == 0){
			cls = 'green'; tcls = 'text-success'; bcls = 'btn-success'; opt = '成功';
		}else if(params.code == 1){
			cls = 'blue'; tcls = 'text-info'; bcls = 'btn-info'; opt = '消息';
		}else if(params.code == -1){
			cls = 'red'; tcls = 'text-error'; bcls = 'btn-danger'; opt = '错误';
		}

		var dom = [];
	    dom.push('<div id="yt_alert" class="yt_alert modal hide fade widget '+cls+' in" tabindex="-1" role="dialog" aria-labelledby="ytmodallabelalert" aria-hidden="true" style="top:30%;">');
	    dom.push('<div class="modal-header widget-title">');
	    dom.push('<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>');
	    dom.push('<h4 id="ytmodallabelalert"><i class="icon-exclamation-sign"></i> '+opt+'</h4>');
	    dom.push('</div>');
	    dom.push('<div class="modal-body">');
	    dom.push('<p class="'+tcls+'">'+msg+'</p>');
	    dom.push('</div>');
	    dom.push('<div class="modal-footer">');
	    dom.push('<button data-dismiss="modal" class="btn '+bcls+'">确定</button>');
	    dom.push('</div></div>');
	    $("body").append(dom.join(""));
	    
	    $(".yt_alert").Tdrag({
	        handle:".modal-header"
	    });
	    
    	$('#yt_alert').on('hidden.bs.modal', function (e) {
    		$('#yt_alert').remove();
    		if(func)
    			func();
		});
	    $('#yt_alert').modal('show');
	};
	me.confirm = function(msg, ok){
		var dom = [];
	    dom.push('<div id="yt_confirm" class="yt_confirm modal hide fade widget yellow in" tabindex="-1" role="dialog" aria-labelledby="ytmodallabelalert" aria-hidden="true" style="top:30%;">');
	    dom.push('<div class="modal-header widget-title">');
	    dom.push('<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>');
	    dom.push('<h4 id="ytmodallabelalert"><i class="icon-exclamation-sign"></i> 提示 </h4>');
	    dom.push('</div>');
	    dom.push('<div class="modal-body">');
	    dom.push('<p class="text-warning">'+msg+'</p>');
	    dom.push('</div>');
	    dom.push('<div class="modal-footer">');
	    dom.push('<button id="yt_confirm_ok" class="btn btn-primary">确定</button><button data-dismiss="modal" class="btn btn-danger">取消</button>');
	    dom.push('</div></div>');
	    $("body").append(dom.join(""));
	    
	    $(".yt_confirm").Tdrag({
	        handle:".modal-header"
	    });
	    
    	$('#yt_confirm').on('hidden.bs.modal', function (e) {
    		$('#yt_confirm').remove();
		});
    	if(ok){
    		var fn = function(){
    			$('#yt_confirm').modal('hide');
    			ok();
    		};
    		$('#yt_confirm_ok').click(fn);
    	}
	    $('#yt_confirm').modal('show');
	};
	/**
	 * 消息框
	 */
	me.message = function(params){
		var dom = [];
		var cls = '',opt = '警告';
		if(params.code == 0){
			cls = 'alert-success';
			opt = '成功';
		}else if(params.code == 1){
			cls = 'alert-info';
			opt = '消息';
		}else if(params.code == -1){
			cls = 'alert-error';
			opt = '错误';
		}
		dom.push('<div id="yt_message" class="alert '+cls+'">');
	    dom.push('<button data-dismiss="alert" class="close">×</button>');
	    dom.push('<strong>'+opt+'!</strong> ' + params.msg);
	    dom.push('</div>');
	    $("body").append(dom.join(""));
	};
	/**
	 * 判断消息类型
	 */
	me.msgtype = function(code){
		if(code == 0)
			return 'success';
		else if(code == 1)
			return 'info';
		else if(code == -1)
			return 'error';
		return 'warning';
	};
	/**
	 * 判断字符串是否为空
	 */
	me.isEmpty = function(str){
		if(str == null || str == '' || str == undefined)
			return true;
		return false;
	};
	/**
	 * 填充表单
	 */
	me.fillForm = function(form, obj){
		var fobj = null;
		for(var o in obj){
			fobj = form.find('#'+o);
			if(fobj.is('select'))
				fobj.find('option[value="'+obj[o]+'"]').prop('selected', true).end().trigger('liszt:updated');
			else
				fobj.val(obj[o]);
		}
	};
	me.strToTime = function(str){
		return new Date(str.replace(/-/g,'/')).getTime();
	};
	me.formatDate = function(date, separator, hms){
		var year = date.getFullYear();     
        var month = date.getMonth()+1;     
        var day = date.getDate();     
        if(hms){
        	 var hour = date.getHours();     
             var minute = date.getMinutes();     
             var second = date.getSeconds();
        	return year+separator+(month<10?'0'+month:month)+separator+(day<10?'0'+day:day)+" "+(hour<10?'0'+hour:hour)+":"+(minute<10?'0'+minute:minute)+":"+(second<10?'0'+second:second);
        }else
        	return year+separator+(month<10?'0'+month:month)+separator+(day<10?'0'+day:day);
	};
	/**
	 * 判断节点是否存在，或者节点是否隐藏
	 */
	me.isExistOrHidden = function(type, selector){
		var res = false;
		if(type === 'exist'){
			if($(selector).length > 0)
				res = true;
		}else if(type === 'hidden'){
			if($(selector).is(":hidden"))
				res = true;
		}
		return res;
	};
})(yt);