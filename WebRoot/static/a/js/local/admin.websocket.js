/*(function(){*/
	"use strict";
	function toggleChatDialog(){
		$('#chat_dialog_tool')[0].click();
		$('.unread-msg').html('0').fadeOut();
	}
	function setMsgTag(from_client_id){
		//首先判断 聊天框是否显示
		//如果显示则不修改unread-msg内容，再判断对应某人的聊天框是否显示，未显示则修改对应unread-msg-target的内容
		//如果未显示则修改unread-msg内容，以及对应unread-msg-target的内容
		//还有很多问题不想再写了，再缓一缓
		if(yt.isExistOrHidden('hidden', '#chat_container')){
			if(yt.isExistOrHidden('hidden', '.unread-msg')){
				 $('.unread-msg').html(1).fadeIn();
				 if(yt.isExistOrHidden('hidden', '#chat_content_'+from_client_id)){
					 var target = $('#'+from_client_id).find('.unread-msg-target');
					 if(yt.isExistOrHidden('hidden', target))
						 target.html(1).fadeIn();
					 else{
						 if(val < 99)
							 target.html(val++);
						 else
							 target.html('99+');
					 }
				 }
			}else{
				var val = parseFloat($('.unread-msg').html());
				if(val < 99)
					$('.unread-msg').html(val++);
				else
					$('.unread-msg').html('99+');
			}
		}else{
			if(yt.isExistOrHidden('hidden', '#chat_content_'+from_client_id)){
				 var target = $('#'+from_client_id).find('.unread-msg-target');
				 if(yt.isExistOrHidden('hidden', target))
					 target.html(1).fadeIn();
				 else{
					 if(val < 99)
						 target.html(val++);
					 else
						 target.html('99+');
				 }
			 }
		}
		
	}
	function commit(){
   		var content = $('#commit-content').val();
   		if(content === '')
   			return false;
   		var chat_target = $('#online_users').find('a.active').parent().attr('data-chat-target');
   		var to_client_id = $('#online_users').find('a.active').parent().attr('id');
   		var message = {
		    "type": "message",//get_online_user,user_join,user_leave
		    "target": chat_target,//one - 发给某一个人,all - 发给所有人, group - 发给讨论组
		    "from_client_id": user_no,
		    "from_client_name": real_name,
		    "from_client_avatar": user_avatar,
		    "to_client_id": to_client_id,
		    "to_group_id":"",
		    "content": content,
		    "time": ""
		};
   		websocket.send(JSON.stringify(message));
   		$('#commit-content').val('');
   		return false;
   }
	function target(){
		$('#online_users').find('.active').removeClass('active');
		$(this).addClass('active');
		var to_client_id = $(this).parent().attr('id');
		$('.timeline-messages').hide();
		if(!yt.isExistOrHidden('exist', '#chat_content_'+to_client_id))//如果节点不存在创建节点
			 $('#chat_content_container').append('<div class="timeline-messages span12" id="chat_content_'+to_client_id+'" style="margin-left:0;"></div>');
		else
			$('#chat_content_'+to_client_id).show();
		$(this).find('.unread-msg-target').html('0').fadeOut();
   }
	if (window.WebSocket) { 
	         websocket = new ReconnectingWebSocket('ws://52nan3.com/socket.do?real_name='+real_name); //new WebSocket(encodeURI('ws://52nan3.com/socket.do'));  
	         websocket.onopen = function() {  
	             //连接成功  
	             console.log('服务器' +  '  (已连接)'); 
	             //获取当前在线用户
	             var message = {
				    "type": "get_online_user",//get_online_user,user_join,user_leave
				    "from_client_id": user_no,
				    "from_client_name": real_name
				};
	             websocket.send(JSON.stringify(message)); 
	         };
	         websocket.onerror = function() {  
	             //连接失败  
	             console.log('服务器' +  '  (连接发生错误)');  
	         };
	         websocket.onclose = function() {  
	             //连接断开  
	             console.log('服务器' +  '  (已经断开连接)');  
	         };
	         //消息接收  
	         websocket.onmessage = function(message) {  
	             var message = JSON.parse(message.data); 
	             var from_client_id = message.from_client_id;
	             var to_client_id = message.to_client_id;
	             var chart_content_id = 'chat_content_';
	             //接收用户发送的消息  
	             if (message.type == 'message') {
					 if(from_client_id === user_no){
	                 	outin = 'out';
	                 	chart_content_id += to_client_id;
					 }else{
	                 	outin = 'in';
	                 	chart_content_id += from_client_id;
					 }
					 //判断message.target来确认应该放到哪一个chat_content之内
					 if(message.target === 'all')
						 $('#chat_content_all').append(receive(message, outin)).scrollTo($('.msg-time-chat:last'),100);
					 else if(message.target === 'one'){
						 if(!yt.isExistOrHidden('exist', '#'+chart_content_id))//如果节点不存在创建节点
							 $('#chat_content_container').append('<div class="timeline-messages span12" id="'+chart_content_id+'" style="display:none; margin-left:0;"></div>');
						 $('#'+chart_content_id).append(receive(message, outin)).scrollTo($('.msg-time-chat:last'),100);
					 }
					 if(outin === 'in') setMsgTag(from_client_id);
	             } else if (message.type == 'get_online_user') {  
	                 //获取在线用户列表  
	                 var online_users = message.online_users;
	                 var length = online_users.length;
	                 var users_li = ['<li style="position:relative;" data-chat-target="all" id="all"><a class="active" href="javascript:void(0)"><span class="icon-user"></span>&nbsp;所有<span class="badge badge-warning unread-msg-target" style="display:none;">0</span></a></li>'];
	                 for(var i = 0; i < length; i++)//需要再判断一下如果用户已经存在的话不用重新添加了，也就是说不能全部清空节点了，得改一下
	                 	users_li.push('<li style="position:relative;" data-chat-target="one" id="'+online_users[i].online_user_id+'"><a href="javascript:void(0)" class="element text-overflow" data-placement="left" data-toggle="tooltip" href="#" data-original-title="'+online_users[i].online_user_name+'"><span class="icon-user"></span>&nbsp;'+online_users[i].online_user_name+'<span class="badge badge-warning unread-msg-target" style="display:none;">0</span></a></li>');
	                 $('#online_users').empty().append(users_li.join('')).find('a').on('click', target);
	             } else if (message.type == 'user_join') { 
	                 //用户上线  
	            	 if(!yt.isExistOrHidden('exist', '#'+from_client_id)){
	            		 $('#online_users').append('<li style="position:relative;" data-chat-target="one" id="'+from_client_id+'"><a href="javascript:void(0)" class="element text-overflow" data-placement="left" data-toggle="tooltip" href="#" data-original-title="'+message.from_client_name+'"><span class="icon-user"></span>&nbsp;'+message.from_client_name+'<span class="badge badge-warning unread-msg-target" style="display:none;">0</span></a></li>');
	            		 $('#'+from_client_id).find('a').on('click', target);
	            	 }
	             } else if (message.type == 'user_leave') {  
	            	 if(yt.isExistOrHidden('exist', '#'+from_client_id))
	            		 $('#'+from_client_id).remove();
	             }  
	         };
	     }
   function receive(message, clz){
   		var msg = [];
   		msg.push('<div class="msg-time-chat">');
        msg.push('<a class="message-img" href="#"><img alt="" src="'+message.from_client_avatar+'" class="avatar"></a>');
        msg.push('<div class="message-body msg-'+clz+'">');
        msg.push('<span class="arrow"></span>');
        msg.push('<div class="text">');
        msg.push('<p class="attribution"> <a href="javascript:void(0);">'+message.from_client_name+'</a> [ '+message.time+' ]</p>');
        msg.push('<p>'+message.content+'</p>');
        msg.push('</div></div></div>');
        return msg.join('');
   }
/*})();*/