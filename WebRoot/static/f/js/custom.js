// Lazy Load - jQuery plugin for lazy loading images Version: 1.9.0
!function(a,b,c,d){var e=a(b);a.fn.lazyload=function(f){function g(){var b=0;i.each(function(){var c=a(this);if(!j.skip_invisible||c.is(":visible"))if(a.abovethetop(this,j)||a.leftofbegin(this,j));else if(a.belowthefold(this,j)||a.rightoffold(this,j)){if(++b>j.failure_limit)return!1}else c.trigger("appear"),b=0})}var h,i=this,j={threshold:0,failure_limit:0,event:"scroll",effect:"show",container:b,data_attribute:"original",skip_invisible:!0,appear:null,load:null,placeholder:"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAANSURBVBhXYzh8+PB/AAffA0nNPuCLAAAAAElFTkSuQmCC"};return f&&(d!==f.failurelimit&&(f.failure_limit=f.failurelimit,delete f.failurelimit),d!==f.effectspeed&&(f.effect_speed=f.effectspeed,delete f.effectspeed),a.extend(j,f)),h=j.container===d||j.container===b?e:a(j.container),0===j.event.indexOf("scroll")&&h.bind(j.event,function(){return g()}),this.each(function(){var b=this,c=a(b);b.loaded=!1,(c.attr("src")===d||c.attr("src")===!1)&&c.attr("src",j.placeholder),c.one("appear",function(){if(!this.loaded){if(j.appear){var d=i.length;j.appear.call(b,d,j)}a("<img />").bind("load",function(){var d=c.data(j.data_attribute);c.hide(),c.is("img")?c.attr("src",d):c.css("background-image","url('"+d+"')"),c[j.effect](j.effect_speed),b.loaded=!0;var e=a.grep(i,function(a){return!a.loaded});if(i=a(e),j.load){var f=i.length;j.load.call(b,f,j)}}).attr("src",c.data(j.data_attribute))}}),0!==j.event.indexOf("scroll")&&c.bind(j.event,function(){b.loaded||c.trigger("appear")})}),e.bind("resize",function(){g()}),/iphone|ipod|ipad.*os 5/gi.test(navigator.appVersion)&&e.bind("pageshow",function(b){b.originalEvent&&b.originalEvent.persisted&&i.each(function(){a(this).trigger("appear")})}),a(c).ready(function(){g()}),this},a.belowthefold=function(c,f){var g;return g=f.container===d||f.container===b?(b.innerHeight?b.innerHeight:e.height())+e.scrollTop():a(f.container).offset().top+a(f.container).height(),g<=a(c).offset().top-f.threshold},a.rightoffold=function(c,f){var g;return g=f.container===d||f.container===b?e.width()+e.scrollLeft():a(f.container).offset().left+a(f.container).width(),g<=a(c).offset().left-f.threshold},a.abovethetop=function(c,f){var g;return g=f.container===d||f.container===b?e.scrollTop():a(f.container).offset().top,g>=a(c).offset().top+f.threshold+a(c).height()},a.leftofbegin=function(c,f){var g;return g=f.container===d||f.container===b?e.scrollLeft():a(f.container).offset().left,g>=a(c).offset().left+f.threshold+a(c).width()},a.inviewport=function(b,c){return!(a.rightoffold(b,c)||a.leftofbegin(b,c)||a.belowthefold(b,c)||a.abovethetop(b,c))},a.extend(a.expr[":"],{"below-the-fold":function(b){return a.belowthefold(b,{threshold:0})},"above-the-top":function(b){return!a.belowthefold(b,{threshold:0})},"right-of-screen":function(b){return a.rightoffold(b,{threshold:0})},"left-of-screen":function(b){return!a.rightoffold(b,{threshold:0})},"in-viewport":function(b){return a.inviewport(b,{threshold:0})},"above-the-fold":function(b){return!a.belowthefold(b,{threshold:0})},"right-of-fold":function(b){return a.rightoffold(b,{threshold:0})},"left-of-fold":function(b){return!a.rightoffold(b,{threshold:0})}})}(jQuery,window,document);
/*!
 * Copyright 2012, Chris Wanstrath
 * Released under the MIT License
 * https://github.com/defunkt/jquery-pjax
 */
(function(h){function l(H,I,J){var K=this;return this.on("click.pjax",H,function(M){var L=h.extend({},u(I,J));if(!L.container){L.container=h(this).attr("data-pjax")||K}m(M,L)})}function m(M,I,J){J=u(I,J);var L=M.currentTarget;if(L.tagName.toUpperCase()!=="A"){throw"$.fn.pjax or $.pjax.click requires an anchor element"}if(M.which>1||M.metaKey||M.ctrlKey||M.shiftKey||M.altKey){return}if(location.protocol!==L.protocol||location.hostname!==L.hostname){return}if(L.href.indexOf("#")>-1&&z(L)==z(location)){return}if(M.isDefaultPrevented()){return}var N={url:L.href,container:h(L).attr("data-pjax"),target:L};var K=h.extend({},N,J);var H=h.Event("pjax:click");h(L).trigger(H,[K]);if(!H.isDefaultPrevented()){C(K);M.preventDefault();h(L).trigger("pjax:clicked",[K])}}function s(K,H,I){I=u(H,I);var J=K.currentTarget;if(J.tagName.toUpperCase()!=="FORM"){throw"$.pjax.submit requires a form element"}var L={type:J.method.toUpperCase(),url:J.action,container:h(J).attr("data-pjax"),target:J};if(L.type!=="GET"&&window.FormData!==undefined){L.data=new FormData(J);L.processData=false;L.contentType=false}else{if(h(J).find(":file").length){return}L.data=h(J).serializeArray()}C(h.extend({},L,I));K.preventDefault()}function C(H){H=h.extend(true,{},h.ajaxSettings,C.defaults,H);if(h.isFunction(H.url)){H.url=H.url()}var M=H.target;var L=r(H.url).hash;var I=H.context=t(H.container);if(!H.data){H.data={}}if(h.isArray(H.data)){H.data.push({name:"_pjax",value:I.selector})}else{H.data._pjax=I.selector}function K(Q,O,P){if(!P){P={}}P.relatedTarget=M;var R=h.Event(Q,P);I.trigger(R,O);return !R.isDefaultPrevented()}var J;H.beforeSend=function(Q,P){if(P.type!=="GET"){P.timeout=0}Q.setRequestHeader("X-PJAX","true");Q.setRequestHeader("X-PJAX-Container",I.selector);if(!K("pjax:beforeSend",[Q,P])){return false}if(P.timeout>0){J=setTimeout(function(){if(K("pjax:timeout",[Q,H])){Q.abort("timeout")}},P.timeout);P.timeout=0}var O=r(P.url);if(L){O.hash=L}H.requestUrl=q(O)};H.complete=function(O,P){if(J){clearTimeout(J)}K("pjax:complete",[O,P,H]);K("pjax:end",[O,H])};H.error=function(R,S,P){var O=y("",R,H);var Q=K("pjax:error",[R,S,P,H]);if(H.type=="GET"&&S!=="abort"&&Q){A(O.url)}};H.success=function(S,R,Z){var V=C.state;var Y=(typeof h.pjax.defaults.version==="function")?h.pjax.defaults.version():h.pjax.defaults.version;var aa=Z.getResponseHeader("X-PJAX-Version");var Q=y(S,Z,H);var P=r(Q.url);if(L){P.hash=L;Q.url=P.href}if(Y&&aa&&Y!==aa){A(Q.url);return}if(!Q.contents){A(Q.url);return}C.state={id:H.id||n(),url:Q.url,title:Q.title,container:I.selector,fragment:H.fragment,timeout:H.timeout};if(H.push||H.replace){window.history.replaceState(C.state,Q.title,Q.url)}try{document.activeElement.blur()}catch(X){}if(Q.title){document.title=Q.title}K("pjax:beforeReplace",[Q.contents,H],{state:C.state,previousState:V});I.html(Q.contents);var U=I.find("input[autofocus], textarea[autofocus]").last()[0];if(U&&document.activeElement!==U){U.focus()}a(Q.scripts);var T=H.scrollTo;if(L){var O=decodeURIComponent(L.slice(1));var W=document.getElementById(O)||document.getElementsByName(O)[0];if(W){T=h(W).offset().top}}if(typeof T=="number"){h(window).scrollTop(T)}K("pjax:success",[S,R,Z,H])};if(!C.state){C.state={id:n(),url:window.location.href,title:document.title,container:I.selector,fragment:H.fragment,timeout:H.timeout};window.history.replaceState(C.state,document.title)}F(C.xhr);C.options=H;var N=C.xhr=h.ajax(H);if(N.readyState>0){if(H.push&&!H.replace){k(C.state.id,D(I));window.history.pushState(null,"",H.requestUrl)}K("pjax:start",[N,H]);K("pjax:send",[N,H])}return C.xhr}function x(H,I){var J={url:window.location.href,push:false,replace:true,scrollTo:false};return C(h.extend(J,u(H,I)))}function A(H){window.history.replaceState(null,"",C.state.url);window.location.replace(H)}var j=true;var G=window.location.href;var E=window.history.state;if(E&&E.container){C.state=E}if("state" in window.history){j=false}function c(J){if(!j){F(C.xhr)}var O=C.state;var I=J.state;var P;if(I&&I.container){if(j&&G==I.url){return}if(O){if(O.id===I.id){return}P=O.id<I.id?"forward":"back"}var H=f[I.id]||[];var K=h(H[0]||I.container),M=H[1];if(K.length){if(O){v(P,O.id,D(K))}var N=h.Event("pjax:popstate",{state:I,direction:P});K.trigger(N);var Q={id:I.id,url:I.url,container:K,push:false,fragment:I.fragment,timeout:I.timeout,scrollTo:false};if(M){K.trigger("pjax:start",[null,Q]);C.state=I;if(I.title){document.title=I.title}var L=h.Event("pjax:beforeReplace",{state:I,previousState:O});K.trigger(L,[M,Q]);K.html(M);K.trigger("pjax:end",[null,Q])}else{C(Q)}K[0].offsetHeight}else{A(location.href)}}j=false}function e(I){var H=h.isFunction(I.url)?I.url():I.url,M=I.type?I.type.toUpperCase():"GET";var K=h("<form>",{method:M==="GET"?"GET":"POST",action:H,style:"display:none"});if(M!=="GET"&&M!=="POST"){K.append(h("<input>",{type:"hidden",name:"_method",value:M.toLowerCase()}))}var L=I.data;if(typeof L==="string"){h.each(L.split("&"),function(N,O){var P=O.split("=");K.append(h("<input>",{type:"hidden",name:P[0],value:P[1]}))})}else{if(h.isArray(L)){h.each(L,function(N,O){K.append(h("<input>",{type:"hidden",name:O.name,value:O.value}))})}else{if(typeof L==="object"){var J;for(J in L){K.append(h("<input>",{type:"hidden",name:J,value:L[J]}))}}}}h(document.body).append(K);K.submit()}function F(H){if(H&&H.readyState<4){H.onreadystatechange=h.noop;H.abort()}}function n(){return(new Date).getTime()}function D(I){var H=I.clone();H.find("script").each(function(){if(!this.src){jQuery._data(this,"globalEval",false)}});return[I.selector,H.contents()]}function q(H){H.search=H.search.replace(/([?&])(_pjax|_)=[^&]*/g,"");return H.href.replace(/\?($|#)/,"$1")}function r(I){var H=document.createElement("a");H.href=I;return H}function z(H){return H.href.replace(/#.*/,"")}function u(H,I){if(H&&I){I.container=H}else{if(h.isPlainObject(H)){I=H}else{I={container:H}}}if(I.container){I.container=t(I.container)}return I}function t(H){H=h(H);if(!H.length){throw"no pjax container for "+H.selector}else{if(H.selector!==""&&H.context===document){return H}else{if(H.attr("id")){return h("#"+H.attr("id"))}else{throw"cant get selector for pjax container!"}}}}function o(I,H){return I.filter(H).add(I.find(H))}function w(H){return h.parseHTML(H,document,true)}function y(L,N,P){var K={},H=/<html/i.test(L);var I=N.getResponseHeader("X-PJAX-URL");K.url=I?q(r(I)):P.requestUrl;if(H){var M=h(w(L.match(/<head[^>]*>([\s\S.]*)<\/head>/i)[0]));var J=h(w(L.match(/<body[^>]*>([\s\S.]*)<\/body>/i)[0]))}else{var M=J=h(w(L))}if(J.length===0){return K}K.title=o(M,"title").last().text();if(P.fragment){if(P.fragment==="body"){var O=J}else{var O=o(J,P.fragment).first()}if(O.length){K.contents=P.fragment==="body"?O:O.contents();if(!K.title){K.title=O.attr("title")||O.data("title")}}}else{if(!H){K.contents=J}}if(K.contents){K.contents=K.contents.not(function(){return h(this).is("title")});K.contents.find("title").remove();K.scripts=o(K.contents,"script[src]").remove();K.contents=K.contents.not(K.scripts)}if(K.title){K.title=h.trim(K.title)}return K}function a(H){if(!H){return}var I=h("script[src]");H.each(function(){var L=this.src;var M=I.filter(function(){return this.src===L});if(M.length){return}var J=document.createElement("script");var K=h(this).attr("type");if(K){J.type=K}J.src=h(this).attr("src");document.head.appendChild(J)})}var f={};var g=[];var i=[];function k(I,H){f[I]=H;i.push(I);b(g,0);b(i,C.defaults.maxCacheLength)}function v(J,L,I){var K,H;f[L]=I;if(J==="forward"){K=i;H=g}else{K=g;H=i}K.push(L);if(L=H.pop()){delete f[L]}b(K,C.defaults.maxCacheLength)}function b(H,I){while(H.length>I){delete f[H.shift()]}}function B(){return h("meta").filter(function(){var H=h(this).attr("http-equiv");return H&&H.toUpperCase()==="X-PJAX-VERSION"}).attr("content")}function p(){h.fn.pjax=l;h.pjax=C;h.pjax.enable=h.noop;h.pjax.disable=d;h.pjax.click=m;h.pjax.submit=s;h.pjax.reload=x;h.pjax.defaults={timeout:650,push:true,replace:false,type:"GET",dataType:"html",scrollTo:0,maxCacheLength:20,version:B};h(window).on("popstate.pjax",c)}function d(){h.fn.pjax=function(){return this};h.pjax=e;h.pjax.enable=p;h.pjax.disable=h.noop;h.pjax.click=h.noop;h.pjax.submit=h.noop;h.pjax.reload=function(){window.location.reload()};h(window).off("popstate.pjax",c)}if(h.inArray("state",h.event.props)<0){h.event.props.push("state")}h.support.pjax=window.history&&window.history.pushState&&window.history.replaceState&&!navigator.userAgent.match(/((iPod|iPhone|iPad).+\bOS\s+[1-4]\D|WebApps\/.+CFNetwork)/);h.support.pjax?p():d()})(jQuery);

!function(){var a=jQuery.event.special,b="D"+ +new Date,c="D"+(+new Date+1);a.scrollstart={setup:function(){var c,d=function(b){var d=this,e=arguments;c?clearTimeout(c):(b.type="scrollstart",jQuery.event.dispatch.apply(d,e)),c=setTimeout(function(){c=null},a.scrollstop.latency)};jQuery(this).bind("scroll",d).data(b,d)},teardown:function(){jQuery(this).unbind("scroll",jQuery(this).data(b))}},a.scrollstop={latency:300,setup:function(){var b,d=function(c){var d=this,e=arguments;b&&clearTimeout(b),b=setTimeout(function(){b=null,c.type="scrollstop",jQuery.event.dispatch.apply(d,e)},a.scrollstop.latency)};jQuery(this).bind("scroll",d).data(c,d)},teardown:function(){jQuery(this).unbind("scroll",jQuery(this).data(c))}}}();

jQuery.cookie = function(name, value, options) {
    if (typeof value != 'undefined') { // name and value given, set cookie
        options = options || {};
        if (value === null) {
            value = '';
            options.expires = -1;
        }
        var expires = '';
        if (options.expires && (typeof options.expires == 'number' || options.expires.toUTCString)) {
            var date;
            if (typeof options.expires == 'number') {
                date = new Date();
                date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000));
            } else {
                date = options.expires;
            }
            expires = '; expires=' + date.toUTCString(); // use expires attribute, max-age is not supported by IE
        }
        var path = options.path ? '; path=' + options.path : '';
        var domain = options.domain ? '; domain=' + options.domain : '';
        var secure = options.secure ? '; secure' : '';
        document.cookie = [name, '=', encodeURIComponent(value), expires, path, domain, secure].join('');
    } else { // only name given, get cookie
        var cookieValue = null;
        if (document.cookie && document.cookie != '') {
            var cookies = document.cookie.split(';');
            for (var i = 0; i < cookies.length; i++) {
                var cookie = jQuery.trim(cookies[i]);
                // Does this cookie string begin with the name we want?
                if (cookie.substring(0, name.length + 1) == (name + '=')) {
                    cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                    break;
                }
            }
        }
        return cookieValue;
    }
};
jQuery.OP = {
	sc:function(form){
		var skey = $(form).find('[name="s"]').val();
		var re = /^\s*$/;
		if(re.test(skey))
			return false;
		else
			return true;
	},
	//显示导航条
	ms:function(d){
		var header = $('.header');
		var content = $('.content');
		var userinfo = $('.user-info');
		var cl = content.offset().left;
		var cd = 0;
		if(cl == 0 || d == -1){
			cd = 160;
			header.css('zIndex', 0);
		}
		//header.animate({left: hd}, 'fast');
		content.animate({left: cd}, 'fast');
		if(userinfo[0])
			userinfo.animate({left: cd}, 'fast');
	}
};
jQuery.LG = {
	nl:function(){
		layer.msg('需登录才能操作，点击<a class="link-to-login" target="_blank" href='+jui.uri+'"/login">登录</a>', {icon:0});
	},
	ot:function(){
		var islogin = false;
	    if( $.CK.get("IS_LOGIN") == 1) islogin = true;
	    if(!islogin)
	    	return $.LG.nl();
	    $.post(jui.uri+"/logout/", function(data){
	    	if(data.success)
	    		location.href = jui.uri+'/';
	    	else
	    		layer.msg("登出失败", {time:800, icon:5});
	    }).error(function(x,t,e){
			$.ER.handle(x,t,e);
		});
	}
};
jQuery.LS = {
    get:function(dataKey){
        if(window.localStorage){
            return localStorage.getItem(dataKey);
        }else{
            return $.cookie(dataKey);  
        }
    },
    set:function(key,value){            
        if(window.localStorage){
            localStorage[key]=value;
        }else{
            $.cookie(key,value);
        }
    },
    remove:function(key){
        if(window.localStorage){
            localStorage.removeItem(key);
        }else{
            $.cookie(key,undefined);
        }
    }
};
jQuery.SS = {
    get:function(dataKey){
        if(window.sessionStorage){
            return sessionStorage.getItem(dataKey);
        }else{
            return $.cookie(dataKey);  
        }
    },
    set:function(key,value){            
        if(window.sessionStorage){
        	sessionStorage[key]=value;
        }else{
            $.cookie(key,value);
        }
    },
    remove:function(key){
        if(window.sessionStorage){
        	sessionStorage.removeItem(key);
        }else{
            $.cookie(key,undefined);
        }
    }
};
jQuery.CK = {
    get:function(dataKey){
        return $.cookie(dataKey);  
    },
    set:function(key,value){            
        $.cookie(key,value);
    },
    remove:function(key){
        $.cookie(key,undefined);
    }
};
jQuery.ER = {
    handle:function(xhr, textStatus, exception){
    	var status = xhr.status; 
    	if(status){
    		switch (status) {
    		case 403:
    			layer.msg("错误信息:访问被拒绝", {icon:5});
    			break;
    		case 404:
    			layer.msg("错误信息:访问地址不存在或已删除", {icon:5});
    			break;
    		case 504:
    			layer.msg("错误信息:请求超时", {icon:5});
    			break;
    		case 0:
    			layer.msg("错误信息:网络异常", {icon:5});
    			break;
    		default:
    			layer.msg("错误信息:错误代码["+status+"]", {icon:5});
    			break;
    		}
    	}
    }
};
+(function($) {
	$.post(jui.uri+'/acc', function(data){
    	$('.log-info').html(data);
    	if($('body').hasClass('detail'))
    		$('#art-acc-avatar').attr('src', $('.log-info-avatar').attr('src'));
    }).error(function(x,t,e){
		$.ER.handle(x,t,e);
	});
	
    var OP=function(op,_ta){
    	if(!islogin){
    		return $.LG.nl();
    	}
    	
    	var flag = 1;//0:取消,1:操作
        var pid = _ta.attr('data-pid');
        if (!pid) return;
        
        if(_ta.hasClass('action-actived'))
        	flag = 0;//已点收藏，当前操作未取消收藏
        _ta.removeClass('action-before').addClass('action-actived');
    	
    	//请求数据操作数据
    	$.get(jui.uri + '/'+op+'/toggle/'+pid+'-'+flag,function(data){
    		if(data.success){
    			var _span = _ta.find('span');
    			var _count = Number(_span.html());
    			if(flag === 1){//数字加1
    				_span.html(_count+1);
    			}else{//数字-1
    				if(_count > 0)
    					_span.html(_count-1);
    				_ta.removeClass('action-actived').addClass('action-before');
    			}
    		}
    	},'json').error(function(x,t,e){
    		$.ER.handle(x,t,e);
    	});
    };
    var bd = $('body');
    var el_carousel = $('.carousel');

    el_carousel.carousel({
        interval: 4000
    });

    if( el_carousel.length && bd.hasClass('focusslide_s_m') ){
        var mc = new Hammer(el_carousel[0]);

        mc.on("panleft panright swipeleft swiperight", function(ev) {
            if( ev.type == 'swipeleft' || ev.type == 'panleft' ){
                el_carousel.carousel('next');
            }else if( ev.type == 'swiperight' || ev.type == 'panright' ){
                el_carousel.carousel('prev');
            }
            // el_carousel[0].textContent = ev.type +" gesture detected.";
        });
    }

    /**
     * 当不是使用module这个页面的模板，不能使用pjax
     */
    if(!bd.hasClass('module') && !bd.hasClass('home')){
    	$('.menu-item a').removeClass('pjax');
    }
    /**
     * pjax 请求
     */
    $(document).pjax('.pjax', '#pjax-container',{timeout:10*1000}); 
     $(document).on('pjax:send', function(){
    	var pcr = $('#pjax-container')
         layer.load(2, {time: 10*1000, offset: ['200px', (pcr.offset().left + pcr.width()/2)+'px']});
    }) ;
    $(document).on('pjax:complete', function(xhr, textStatus, options) {
    	 layer.closeAll('loading');
    	 $('.content .thumb').lazyload({
	        placeholder: jui.uri + '/static/f/images/thumbnail.gif',
	        event: 'scrollstop'
	    });
    });
    $(document).on('pjax:success', function(data, status, xhr, options) {
    	if(status === "PJAX ERROR"){//获取页面出错，将自动返回
    		history.back();
    		layer.msg("页面加载出错", {time:800});
    	}
    });
    /* 
     * 
     * ====================================================================================================
    */
    $('.m-search').on('click', function(){
        $('.search-form').slideToggle(200, function(){
            if( $('.m-search').css('display') == 'block' ){
                $('.search-form .form-control').focus();
            }
        });
    });

    $('.user-info a').on('click', function(){
    	$('.user-active').removeClass('user-active');
    	$(this).addClass('user-active');
    });
    $('.menu-item').on('click', function(e){
    	e = e || window.event;
        var target = e.target || e.srcElement,
            _ta = $(target)
        var menu = _ta.parents('li');
    	$('.current-menu-item').removeClass('current-menu-item');
        
    	$('title').html(menu.attr('data-title'));
    	menu.addClass('current-menu-item');
    	
    	if(_ta.siblings('ul').length == 0)
    		$.OP.ms();
    });

    bd.append('<div class="rollto"><a href="javascript:;"></a></div>');
    
    // lazy avatar
    $('.content .avatar').lazyload({
        placeholder: jui.uri + '/static/f/images/default.jpg',
        event: 'scrollstop'
    });

    $('.sidebar .avatar').lazyload({
        placeholder: jui.uri + '/static/f/images/default.jpg',
        event: 'scrollstop'
    });

    $('.content .thumb').lazyload({
        placeholder: jui.uri + '/static/f/images/thumbnail.gif',
        event: 'scrollstop'
    });

    $('.sidebar .thumb').lazyload({
        placeholder: jui.uri + '/static/f/images/thumbnail.gif',
        event: 'scrollstop'
    });

    $('.content .wp-smiley').lazyload({
        event: 'scrollstop'
    });

    $('.sidebar .wp-smiley').lazyload({
        event: 'scrollstop'
    });


    var elments = {
        sidebar: $('.sidebar'),
        footer: $('.footer')
    };

    $('.feed-weixin').popover({
        placement: bd.hasClass('ui-navtop')?'bottom':'right',
        trigger: 'hover',
        container: 'body',
        html: true
    });

    /* 
     * page search
     * ====================================================
    */
    if( bd.hasClass('search-results') ){
        var val = $('.search-form .form-control').val();
        var reg = eval('/'+val+'/i');
        $('.excerpt h2 a, .excerpt .note').each(function(){
            $(this).html( $(this).text().replace(reg, function(w){ return '<span style="color:#FF5E52;">'+w+'</span>';}) );
        });
    }

    if( elments.sidebar.length && jui.roll ){
        jui.roll = jui.roll.split(' ');
    	var h1 = 20, h2 = 40, h3 = 20;
    	if( bd.hasClass('ui-navtop') ){
    		h1 = 100, h2 = 120;
    	}
        var rollFirst = elments.sidebar.find('.widget:eq('+(Number(jui.roll[0])-1)+')');
        var sheight = rollFirst[0].offsetHeight;
        rollFirst.on('affix-top.bs.affix', function(){
            rollFirst.css({top: 0});
            sheight = rollFirst[0].offsetHeight;
            for (var i = 1; i < jui.roll.length; i++) {
                var item = Number(jui.roll[i])-1;
                var current = elments.sidebar.find('.widget:eq('+item+')');
                current.removeClass('affix').css({top: 0});
            };
        });
        rollFirst.on('affix.bs.affix', function(){
            rollFirst.css({top: h1});
            for (var i = 1; i < jui.roll.length; i++) {
                var item = Number(jui.roll[i])-1
                var current = elments.sidebar.find('.widget:eq('+item+')');
                current.addClass('affix').css({top: sheight+h2});
                sheight += current[0].offsetHeight + h3;
            };
        });
        rollFirst.affix({
            offset: {
                top: elments.sidebar.height(),
                bottom: (elments.footer.height()||0) + 10
            }
        });
    }

    $('.excerpt header small').each(function() {
        $(this).tooltip({
            container: 'body',
            title: '此文有 ' + $(this).text() + '张 图片'
        });
    });

    $('.article-tags a, .post-tags a').each(function() {
        $(this).tooltip({
            container: 'body',
            placement: 'bottom',
            title: '查看关于 ' + $(this).text() + ' 的文章'
        })
    });

    $('.cat').each(function() {
        $(this).tooltip({
            container: 'body',
            title: '查看关于 ' + $(this).text() + ' 的文章'
        })
    });

    $('.widget_tags a').tooltip({
        container: 'body'
    });

    $('.readers a, .widget_comments a').tooltip({
        container: 'body',
        placement: 'top'
    });

    $('.article-meta li:eq(1) a').tooltip({
        container: 'body',
        placement: 'bottom'
    });
    $('.post-edit-link').tooltip({
        container: 'body',
        placement: 'right',
        title: '去后台编辑此文章'
    });


    if ($('.article-content').length){
        $('.article-content img').attr('data-tag', 'bdshare')

        video_ok()
        $(window).resize(function(event) {
            video_ok()
        });
    }

    function video_ok(){
        $('.article-content embed, .article-content video, .article-content iframe').each(function(){
            var w = $(this).attr('width'),
                h = $(this).attr('height')
            if( h ){
                $(this).css('height', $(this).width()/(w/h))
            }
        });
    }


    $('.rollto a').on('click', function() {
        scrollTo();
    });

    $(window).scroll(function() {
        var scroller = $('.rollto');
        document.documentElement.scrollTop + document.body.scrollTop > 200 ? scroller.fadeIn() : scroller.fadeOut();
    });

    /* functions
     * ====================================================
     */
    function scrollTo(name, speed) {
        if (!speed) speed = 300
        if (!name) {
            $('html,body').animate({
                scrollTop: 0
            }, speed)
        } else {
            if ($(name).length > 0) {
                $('html,body').animate({
                    scrollTop: $(name).offset().top
                }, speed);
            }
        }
    };
    

    var islogin = false;
    if( $.CK.get("IS_LOGIN") == 1) islogin = true;
    /* event click
     * ====================================================
     */
    $(document).on('click', function(e) {
        e = e || window.event;
        var target = e.target || e.srcElement,
            _ta = $(target)

        if (_ta.hasClass('disabled')) return
        if (_ta.parent().attr('data-event')) _ta = $(_ta.parent()[0])
        if (_ta.parent().parent().attr('data-event')) _ta = $(_ta.parent().parent()[0])

        var type = _ta.attr('data-event');
        $('#user-opts').hide();
        if($('body').hasClass('write')){
        	$('.recmd-container').fadeOut('fast');
	        if(_ta.attr('id')!='type-choosed' && !_ta.hasClass('t-add') && !_ta.parents('#type-choosed')[0] && !_ta.hasClass('type-opt') && !_ta.parent().hasClass('layui-layer-btn') && !_ta.parents('#LAY_layuipro')[0])
	        	$('#type-options').slideUp('fast');
	        if(!_ta.parents('#theme-choosed')[0] && _ta.attr('id')!='theme-choosed')
	        	$('#theme-options').slideUp('fast');
	        if(!_ta.parents('#flg-choosed')[0] && _ta.attr('id')!='flg-choosed')
	        	$('#flg-options').slideUp('fast');
        }
        switch (type) {
            case 'like':
            	OP('like',_ta);
                break;
            case 'colet':
            	OP('colet',_ta);
                break;
            case 'comment-user-change':
                $('#comment-author-info').slideDown(300);
                $('#comment-author-info input:first').focus();
                break;
            case 'opts':
            	$('#user-opts').show();
            	break;
            case 'login':
                $('#modal-login').modal('show');
                break;
        }
    });

    $('.commentlist .url').attr('target','_blank');

    //jsonp
    jsonp = function (url, func) {
        	var callback = "jsonpCallback_" + new Date().getTime() + Math.floor(Math.random() * 1000000);
            url = url + (url.indexOf( '?' ) + 1 ? '&' : '?') + 'callback=' + callback;
        window[callback] = function (data) {
            func(data);
            window[callback] = null;
        };
        script = document.createElement('script');
	    script.setAttribute('src', url);
        document.body.appendChild(script);
    };
})(jQuery);