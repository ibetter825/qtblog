package com.mypro.plugin;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.mypro.annotation.Model;
import com.mypro.common.DictKeys;
import com.mypro.common.tools.ToolClassSearcher;
import com.mypro.model.BaseModel;

public class ModelPlugin implements IPlugin {
	protected final Logger log = Logger.getLogger(getClass());
    
    private Map<String, ActiveRecordPlugin> arpMap;

	public ModelPlugin(Map<String, ActiveRecordPlugin> arpMap){
		this.arpMap = arpMap;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean start() {
		List<String> jars = (List<String>) PropertiesPlugin.getParamMapValue(DictKeys.config_scan_jar);
		List<Class<? extends BaseModel>> modelClasses = null;// 查询所有继承BaseModel的类
		if(jars.size() > 0){
			modelClasses = ToolClassSearcher.of(BaseModel.class).includeAllJarsInLib(ToolClassSearcher.isValiJar()).injars(jars).search();// 可以指定查找jar包，jar名称固定，避免扫描所有文件
		}else{
			modelClasses = ToolClassSearcher.of(BaseModel.class).search();
		}
		
		// 循环处理自动注册映射
		for (Class model : modelClasses) {
			// 获取注解对象
			Model tableBind = (Model) model.getAnnotation(Model.class);
			if (tableBind == null) {
				log.error(model.getName() + "继承了BaseModel，但是没有注解绑定表名 ！！！");
				break;
			}

			// 获取映射属性
			String dataSourceName = tableBind.dataSourceName().trim();
			String tableName = tableBind.tableName().trim();
			String pkName = tableBind.pkName().trim();
			if(dataSourceName.equals("") || tableName.equals("") || pkName.equals("")){
				log.error(model.getName() + "注解错误，数据源、表名、主键名为空 ！！！");
				break;
			}
			
			// 映射注册
			ActiveRecordPlugin arp = arpMap.get(dataSourceName);
			if(arp == null){
				log.error(model.getName() + "ActiveRecordPlugin不能为null ！！！");
				break;
			}
			arp.addMapping(tableName, pkName, model);
			log.debug("Model注册： model = " + model + ", tableName = " + tableName + ", pkName: " + pkName);
		}
		return true;
	}

	public boolean stop() {
		return true;
	}

}
