package com.mypro.plugin;

import java.util.List;
import org.apache.log4j.Logger;
import com.jfinal.config.Routes;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.IPlugin;
import com.mypro.annotation.Controller;
import com.mypro.common.DictKeys;
import com.mypro.common.tools.ToolClassSearcher;
import com.mypro.web.admin.BaseController;

/**
 * 扫描Controller上的注解，绑定Controller和controllerKey
 * @author 董华健
 */
public class ControllerPlugin implements IPlugin {
	private final Logger log = Logger.getLogger(ControllerPlugin.class);
	
	private Routes me;
	
	public ControllerPlugin(Routes me) {
		this.me = me;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean start() {
		// 查询所有继承BaseController的类
		List<String> jars = (List<String>) PropertiesPlugin.getParamMapValue(DictKeys.config_scan_jar);
		
		List<Class<? extends BaseController>> controllerClasses = null;
		if(jars.size() > 0){
			controllerClasses = ToolClassSearcher.of(BaseController.class).includeAllJarsInLib(ToolClassSearcher.isValiJar()).injars(jars).search();// 可以指定查找jar包，jar名称固定，避免扫描所有文件
		}else{
			controllerClasses = ToolClassSearcher.of(BaseController.class).search();
		}
		String admin = PropKit.get(DictKeys.config_default_admin_route);
		// 循环处理自动注册映射
		for (Class controller : controllerClasses) {
			// 获取注解对象
			Controller controllerBind = (Controller) controller.getAnnotation(Controller.class);
			if (controllerBind == null) {
				log.error(controller.getName() + "继承了BaseController，但是没有注解绑定映射路径");
				continue;
			}

			// 获取映射路径数组
			String[] controllerKeys = controllerBind.controllerKey();
			for (String controllerKey : controllerKeys) {
				controllerKey = controllerKey.trim();
				if(controllerKey.equals("")){
					log.error(controller.getName() + "注解错误，映射路径为空");
					continue;
				}
				//替换掉开发时的admin后台
				if(controllerKey.startsWith("/admin"))
					controllerKey = controllerKey.replace("/admin", admin);
				// 注册映射
				me.add(controllerKey, controller);
				log.debug("Controller注册： controller = " + controller + ", controllerKey = " + controllerKey);
			}
		}
		return true;
	}

	public boolean stop() {
		return true;
	}

}
