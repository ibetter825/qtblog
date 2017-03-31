package com.mypro.admin.thread;

import java.util.List;

import redis.clients.jedis.Jedis;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;

public class MqThread extends Thread {
	private static MqThread thread = null;
	private MqThread(){}
	public static MqThread getInstance(){
		if(thread == null)
			thread = new MqThread();
		return thread;
	}
	//有问题
	public void begin() {
		if(!thread.isInterrupted())
			thread.start();
	}

	@Override
	public void run() {
		while(!isInterrupted()){
			Cache redis = Redis.use("mq");
			//String msg = redis.lpop("msgs");//这个地方不对，当没有消息的时候没有阻塞线程，该换用blpop方法，但是会报错
			Jedis jedis = redis.getJedis();
			List list = jedis.blpop(0, "msgs");//	.blpop(0, "msgs");
			jedis.close();
			//List list = redis.blpop(10, "msgs");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			/*if(msg == null)
				interrupt();
			else*/
				System.err.println(list.get(1));
		}
	}
}
