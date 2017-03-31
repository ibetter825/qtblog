package com.mypro.web.controller.admin;

import redis.clients.jedis.Jedis;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.mypro.admin.thread.MqThread;
import com.mypro.annotation.Controller;
import com.mypro.web.admin.BaseController;
/**
 * redis消息队列测试
 * @author ibett
 *
 */
@Controller(controllerKey = "/admin/mq")
public class MessageQueueController extends BaseController {
	public void index(){
		String msg = getPara("msg");
		Cache redis = Redis.use("mq");
		//redis.rpush("msgs", msg);//将消息加入到redis的msgs集合中，再另外一个线程中取出来单独处理
		Jedis jedis = redis.getJedis();
		jedis.rpush("msgs", msg);
		//开启一个线程处理消息
		//MqThread.getInstance().begin();
		renderJson("成功");
	}
}
