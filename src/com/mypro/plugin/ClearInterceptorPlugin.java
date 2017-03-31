package com.mypro.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.apache.log4j.Logger;
import com.jfinal.plugin.IPlugin;
import com.mypro.annotation.ClearInterceptor;
import com.mypro.annotation.Controller;
import com.mypro.common.DictKeys;
import com.mypro.common.tools.ToolClassSearcher;
import com.mypro.web.admin.BaseController;

/**
 * 扫描Controller上的注解，绑定Controller和controllerKey
 * @author 董华健
 */
public class ClearInterceptorPlugin implements IPlugin {
	@SuppressWarnings("unused")
	private final Logger log = Logger.getLogger(ClearInterceptorPlugin.class);
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
		
		// 循环处理自动注册映射
		for (Class controller : controllerClasses) {
			// 获取注解对象
			Controller contro = (Controller) controller.getAnnotation(Controller.class);
			String[] controllerKeys = contro.controllerKey();
			Method[] methods = controller.getDeclaredMethods();
			Method inteMethod = null;
			ClearInterceptor clear = (ClearInterceptor) controller.getAnnotation(ClearInterceptor.class);
			if(clear == null)
				continue;
			Class[] intes = clear.interceptor();
			int controIntesLength = intes.length;
			for (Method method : methods) {
				if(controIntesLength == 0){//在方法中过滤了拦截器
					clear = (ClearInterceptor) method.getAnnotation(ClearInterceptor.class);//这里还有问题，如果方法存在多个注解就会出问题，尤其是方法存在ActionKey这个注解时
					if(clear == null)
						continue;
					intes = clear.interceptor();
					if(intes.length == 0)//需要过滤的拦截器没有配置具体的类
						continue;
				}
				for (Class clz : intes) {
					try {
						inteMethod = clz.getDeclaredMethod("setExcludes", String.class);
						for (String ckey : controllerKeys) {
							if(method.getName().equalsIgnoreCase("index"))
								inteMethod.invoke(clz.newInstance(), ckey);
							else
								inteMethod.invoke(clz.newInstance(), ckey+"/"+method.getName());
						}
					} catch (NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return true;
	}

	public boolean stop() {
		return true;
	}

}
