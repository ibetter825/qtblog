(function () {
	"use strict"
    // 获取 wangEditor 构造函数和 jquery
    var E = window.wangEditor;
    var $ = window.jQuery;

    // 用 createMenu 方法创建菜单
    E.createMenu(function (check) {

        // 定义菜单id，不要和其他菜单id重复。编辑器自带的所有菜单id，可通过『参数配置-自定义菜单』一节查看
        var menuId = 'save';

        // check将检查菜单配置（『参数配置-自定义菜单』一节描述）中是否该菜单id，如果没有，则忽略下面的代码。
        if (!check(menuId)) {
            return;
        }

        // this 指向 editor 对象自身
        var editor = this;

        // 创建 menu 对象
        var menu = new E.Menu({
            editor: editor,  // 编辑器对象
            id: menuId,  // 菜单id
            title: '保存', // 菜单标题

            // 正常状态和选中装下的dom对象，样式需要自定义
            $domNormal: $('<a href="#" tabindex="-1" id="save-btn" style="color:#FFF;"><i class="glyphicon glyphicon-floppy-disk"></i></a>'),
            $domSelected: $('<a href="#" tabindex="-1" class="selected"><i class="glyphicon glyphicon-floppy-disk"></i></a>')
        });

        // 菜单正常状态下，点击将触发该事件
        menu.clickEvent = function (e) {
        	var saveBtn = $('#save-btn'); 
        	if(saveBtn.attr('data-is-sub') == 1)
        		return;
        	saveBtn.css('background','#9A9A9A').attr('data-is-sub', 1);
        	var title = $.trim($('#artilce-title').val());
        	var re = /^\s*$/;
    		if(re.test(title))
    			return layer.msg("请输入文章标题", {time:1000}, function(){saveBtn.css('background','').removeAttr('data-is-sub');});
    		if(title.replace(/[^\x00-\xff]/g,"01").length > 80)
    			return layer.msg("文章标题过长", {time:1000}, function(){saveBtn.css('background','').removeAttr('data-is-sub');});
    		var text = editor.$txt.formatText();
    		if(re.test(text) || text === '请输入内容...')
    			return layer.msg("请输入文章内容", {time:1000}, function(){saveBtn.css('background','').removeAttr('data-is-sub');});
    		
    		$('#artilce-title').val(filterXSS.escapeHtml(title));
    		$('#summary').val(filterXSS.escapeHtml($.trim(text.substring(0,134))));//摘要
    		var content = editor.$txt.html();
    		$('#editor').val(filterXSS(content));
    		var imgs = editor.$txt.find('img');//获取所有的图片
    		var article_imgs = '';
    		var face = 'thumb.gif';//需要判断图片是否是表情，这个就不好控制了
    		var src = null;
    		for(var i = 0; i < imgs.length; i++){
    			src = $(imgs[i]).attr('src');
    			if(src.indexOf(face) == -1)
    				article_imgs += src + ';';//文章中的图片要整理起来，有用处
    		}
    		$('#artimgs').val(article_imgs.substring(0, article_imgs.length - 1));
    		//获取标签
    		var tag_names = $('#article_tags').val().split(',');
    		var tag_ids = [], tag_id = null;
    		for(var i = 0, l = tag_names.length; i < l; i++){
    			tag_id = mapping[tag_names[i]];
    			if(tag_id == undefined)
    				tag_id = 0;
    			tag_ids[i] = tag_id;
    		}
    		$('#article_tag_ids').val(tag_ids.join(','));
    		var param = $('#articleForm').serialize();
    		$.post(jui.uri+'/article/save', param, function(data){
    			if(data.success){
    				layer.msg("操作成功", {time:800, icon:1}, function(){
    					location.href = jui.uri+'/zone';//redirctUrl;
					});
    			}else{
    				saveBtn.css('background','').removeAttr('data-is-sub');
    				var errs = data.data, isEmpty = true;
					for(var e in errs){
						isEmpty = false;
						layer.msg(errs[e], {time:1000, icon:5});
					}
					if(isEmpty)
						layer.msg(data.msg, {time:1000, icon:5});
    			}
    		},'json');
        };
        // 增加到editor对象中
        editor.menus[menuId] = menu;
    });

    //文章来源
    $('#article_source').change(function(){
    	var val = $(this).val();
    	if(val == 1)//转载
    		$('#article_from').removeAttr('disabled').parent().slideDown('fast');
    	else
    		$('#article_from').attr('disabled', 'disabled').parent().slideUp('fast');
    });
    //选中栏目
    $('#theme-choosed').on('click', function(){
    	$('#theme-options').slideToggle('fast');
    });
    $('.tm-opt').on('click', function(){
    	var obj = $(this);
    	var tm_no = obj.attr('data-no');
    	obj.addClass('selected').siblings().removeClass('selected');
    	$('#article_theme').val(tm_no);
    	$('#theme-choosed > span').html(obj.html());
    	obj.parent().slideUp('fast');
    });
    var tm_selected = $('#theme-options > .selected'); 
    if(tm_selected[0]){
    	$('#article_theme').val(tm_selected.attr('data-no'));
    	$('#theme-choosed > span').html(tm_selected.html());
    }else{
    	$('.tm-opt:first')[0].click();
    }
    //选择权限
    $('#flg-choosed').on('click', function(){
    	$('#flg-options').slideToggle('fast');
    });
    $('.flg-opt').on('click', function(){
    	var obj = $(this);
    	var flg_no = obj.attr('data-no');
    	obj.addClass('selected').siblings().removeClass('selected');
    	$('#article_flag').val(flg_no);
    	$('#flg-choosed > span').html(obj.html());
    	obj.parent().slideUp('fast');
    });
    var flg_selected = $('#flg-options > .selected'); 
    if(flg_selected[0]){
    	$('#flg_theme').val(tm_selected.attr('data-no'));
    	$('#flg-choosed > span').html(flg_selected.html());
    }else{
    	$('.flg-opt:first')[0].click();
    }
    //选择分类
	$('#type-choosed').on('click', function(){
		$('#type-options').slideToggle('fast');
	});
	$('#type-options').on('click','.t-option', function(e){
		if(e.target != this)
			return;
		var type_no = $(this).attr('data-no');
		$('#article_type').val(type_no);
		$(this).addClass('selected').siblings().removeClass('selected');
		$('#type-choosed span').html($(this).text());
		$(this).parent().slideUp('fast');
	});
	$('#type-options').on('mouseenter','.t-option', function(e){$(this).find('i').show();}).on('mouseleave','.t-option', function(e){$(this).find('i').hide();});
	$('#type-options').on('click','.type-opt', function(){
		var op = 'edit';
		var tar = $(this);
		var type_id = tar.parent().attr('data-no');
		var type_name = tar.parent().text();
		if(tar.hasClass('delete'))
			op = 'delete';
		else if(tar.hasClass('add'))
			op = 'add';
		if(op === 'edit' || op === 'add'){
			layer.open({
			  type: 1
			  ,title: '编辑分类' //不显示标题栏
			  ,closeBtn: false
			  ,area: '300px;'
			  ,shade: false
			  ,id: 'LAY_layuipro' //设定一个id，防止重复弹出
			  ,resize: false
			  ,btn: ['确定', '取消']
			  ,btnAlign: 'c'
			  ,moveType: 1 //拖拽模式，0或者1
			  ,content: '<p><input id="edit_type_name" class="qt-select" style="width:280px; margin:20px 10px 10px;" type="text" value="'+type_name+'" placeholder="输入分类名称"/></p>'
			  ,success: function(layero){
				  var btn = layero.find('.layui-layer-btn');
				    btn.find('.layui-layer-btn0').on('click', function(){
				    	var type_name = $('#edit_type_name').val();
				    	var reg = /^[a-zA-Z0-9\u4e00-\u9fa5]{1,10}$/;
				    	var param = {'type.type_name':type_name};
				    	if(type_id)//修改 否则为新增
				    		param['type.type_id'] = type_id;
				    	if(reg.test(type_name)){
				    		$.post(jui.uri+'/type/save',param, function(data){
	    						if(data.success){
	    							layer.msg('操作成功');
	    							if(type_id){
	    								var cont = type_name+'<i class="type-opt delete glyphicon glyphicon-trash" style="display: none;"></i><i class="type-opt edit glyphicon glyphicon-pencil" style="display: none;"></i>';
	    								tar.parent().html(cont);
	    								var choosed = $('#article_type').val();
	    								if(type_id == choosed)
	    									$('#type-choosed span').html(type_name);
	    							}else{
	    								var type = data.data.type;
	    								var li = '<li class="t-option" data-no="'+type.type_id+'">'+type.type_name+'<i class="type-opt delete glyphicon glyphicon-trash" style="display: none;"></i><i class="type-opt edit glyphicon glyphicon-pencil" style="display: none;"></i></li>';
	    								$('.t-add').before(li);
	    							}
	    						}else
	    							layer.msg('操作失败',{icon:5});
	    					},'json');
				    	}else
				    		layer.msg('名称不能超过10个字，可中英文混合');
				    });
			  }
			});
		}else{//删除
			layer.msg('确定删除该分类么？', {
				  time: 0, //不自动关闭
				  btn: ['确定', '取消'],
				  yes: function(index){
					$.post(jui.uri+'/type/del/'+type_id, function(data){
						if(data.success){
							var choosed = $('#article_type').val();
							if(type_id == choosed){
								$('#article_type').val(0);
								$('#type-choosed span').html('选择分类...');
							}
							layer.msg('删除成功');
							tar.parent().slideUp('fast', function(){tar.parent().remove();});
						}else
							layer.msg('删除失败',{icon:5});
					},'json');
				  }
			});
		}
	});
	
	// 阻止输出log
    wangEditor.config.printLog = false;
    var editor = new wangEditor(ed);
  	//表情
	editor.config.emotions = {
        'default': {
            title: '默认',
            data: jui.uri+'/static/data/emotions_default.data'
        }
	}
	// 插入代码时的默认语言
	editor.config.codeDefaultLang = 'javascript'
	// 上传图片
    editor.config.uploadImgUrl = jui.uri+'/upload/editor?upload_type=image';
    editor.config.uploadImgFileName = 'upload';
	// 普通的自定义菜单
    editor.config.menus = ['bold','underline','italic','strikethrough','eraser','forecolor','bgcolor','|','quote','fontfamily','fontsize','head','alignleft','aligncenter','alignright',
        '|','table','emotion','|','img','video','insertcode','|','fullscreen','|','save'
    ];
    editor.create();
    //编辑器保存菜单的位置修改，靠右
    $('.menu-group:last').css({'float':'right','borderRight':0,'padding':0,'background':'#FF766C'}).find('.menu-item').width('60px');
    //初始化标签
    var artTags = $('#article_tags');
    artTags.tagEditor({
        placeholder: '添加标签...',
        onChange: function(field, editor, tags) {
        	if(tags.length > 5){
        		layer.msg('不能超过5个标签!', {time:800})
            	artTags.tagEditor('removeTag', tags[5]);
            }
        }
    });
	$('.tag-editor').on('click', function(){$('.recmd-container').fadeIn('fast');});
	//给推荐的tag添加事件
	$('.rcmd-tag').on('click', function(){
		var span = $(this);
		var tagId = span.attr('data-tag-id');
		var tagName = span.html();
		artTags.tagEditor('addTag', tagName);
		var artTagIds = $('#article_tag_ids');
		artTagIds.val(artTagIds.val()+','+tagId);
	});
})();