package com.mypro.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.plugin.scheduler.SchedulerPlugin;
import com.mypro.admin.interceptor.AdminContextInterceptor;
import com.mypro.admin.interceptor.ExceptionInterceptor;
import com.mypro.common.DictKeys;
import com.mypro.common.StaticData;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.front.interceptor.FrontCurrentInterceptor;
import com.mypro.handler.GlobalHandler;
import com.mypro.model.front.QtNav;
import com.mypro.plugin.ControllerPlugin;
import com.mypro.plugin.ModelPlugin;
import com.mypro.plugin.PropertiesPlugin;
import com.mypro.service.front.QtNavService;

public class MyJFinalConfig extends JFinalConfig {
	private static Logger log = Logger.getLogger(MyJFinalConfig.class);
	/**
	 * 配置常量
	 */
	@Override
	public void configConstant(Constants me) {
		log.info("configConstant 缓存 properties");
		new PropertiesPlugin(loadPropertyFile("config.properties")).start();//此处还要修改，可以不需要这个，需要修改一下
		
		log.info("configConstant 缓存 properties");
		PropKit.use("config.properties");
		
		me.setDevMode(PropKit.getBoolean(DictKeys.config_devMode,false));
		me.setEncoding(PropKit.get(DictKeys.config_encoding));
		me.setBaseViewPath("/WEB-INF/view");
		me.setError404View("/WEB-INF/view/common/error/404.html");//404
		me.setError500View("/WEB-INF/view/common/error/500.html");//500
	}

	/**
	 * 配置处理器
	 */
	@Override
	public void configHandler(Handlers me) {
		//me.add(new FakeStaticHandler(".html"));//伪静态处理器
		me.add(new GlobalHandler());//全局处理器
	}

	/**
	 * 配置拦截器
	 */
	@Override
	public void configInterceptor(Interceptors me) {
		//me.add(new SessionInViewInterceptor(true));//开启session
		me.addGlobalActionInterceptor(new ExceptionInterceptor());//全局错误解析器
		me.addGlobalActionInterceptor(new AdminContextInterceptor());//后台全局拦截器
		me.addGlobalActionInterceptor(new FrontCurrentInterceptor());//前台登录用户获取
		me.addGlobalActionInterceptor(new FrontContextInterceptor());//前台全局拦截器
	}

	/**
	 * 配置路由
	 */
	@Override
	public void configRoute(Routes me) {
		log.info("configRoute 路由扫描注册");
		new ControllerPlugin(me).start();
	}
	
	/**
	 * 配置插件
	 */
	@Override
	public void configPlugin(Plugins me) {
		log.info("configPlugin 配置Druid数据库连接池连接属性");
		DruidPlugin druidPlugin = new DruidPlugin(PropKit.get("mysql.jdbcUrl").trim(), PropKit.get("mysql.userName").trim(), PropKit.get("mysql.passWord").trim());

		log.info("configPlugin 配置Druid数据库连接池大小");
		druidPlugin.set(PropKit.getInt(("db.initialSize")), PropKit.getInt("db.minIdle"), PropKit.getInt("db.maxActive"));
		
		log.info("配置ActiveRecord插件");
		ActiveRecordPlugin arp = new ActiveRecordPlugin(DictKeys.db_dataSource_main, druidPlugin);
		arp.setDevMode(PropKit.getBoolean(DictKeys.config_devMode, false)); // 设置开发模式
		arp.setShowSql(PropKit.getBoolean(DictKeys.config_devMode, false)); // 是否显示SQL
		
		log.info("configPlugin 使用数据库类型是 mysql");
		arp.setDialect(new MysqlDialect());
		arp.setContainerFactory(new CaseInsensitiveContainerFactory(true));
		log.info("configPlugin 添加druidPlugin插件");
		me.add(druidPlugin); // 多数据源继续添加
		
		log.info("configPlugin 表扫描注册");
		Map<String, ActiveRecordPlugin> arpMap = new HashMap<String, ActiveRecordPlugin>();
		arpMap.put(DictKeys.db_dataSource_main, arp); // 多数据源继续添加
		new ModelPlugin(arpMap).start();//Model
		me.add(arp); // 多数据源继续添加
		
		//启用缓存
		me.add(new EhCachePlugin());
		
		//配置定时任务
		SchedulerPlugin schedulerPlugin = SchedulerPlugin.builder().scheduledThreadPoolSize(10)
        .enableAnnotationScan("com.mypro.admin.task")
        .enableConfigFile("job.properties")
        .build();
		me.add(schedulerPlugin);
	}

	@Override
	public void afterJFinalStart() {
		//MqThread.getInstance().begin();
		List<QtNav> navs = QtNavService.service.queryNavs();//将导航内容，存到一个静态变量中
		StaticData.loadNavMapper(navs);
		//缓存一些数据
	}

	/**
	 * 建议使用 JFinal 手册推荐的方式启动项目
	 * 运行此 main 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 */
	public static void main(String[] args) {
		JFinal.start("WebRoot", 80, "/", 5);
	}

}
