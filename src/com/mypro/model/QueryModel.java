package com.mypro.model;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Model;

public class QueryModel extends Model<QueryModel> {
	private static final long serialVersionUID = 1L;
	public QueryModel(){}
	@SuppressWarnings("rawtypes")
	public QueryModel(Controller controller, String... modelNames){
		Enumeration<String> paramNames = controller.getParaNames();
		String key = null, value = null;
		int length = modelNames.length;
		for(Enumeration e = paramNames; e.hasMoreElements();){
			key = e.nextElement().toString();
			value = controller.getPara(key).trim();
			if(length == 1){
				if(key.startsWith(modelNames[0]+".")){
					key = key.substring(modelNames[0].length()+1);
					this.set(key.toLowerCase(), value);
				}
			}else if(length > 1){
				for (int i = 0; i < length; i++) {
					if(key.startsWith(modelNames[i]+"."))
						this.set(key.toLowerCase(), value);
				}
			}else
				this.set(key.toLowerCase(), value);
		}
	}

	/**
	 * 获取带参数查询语句
	 * @param sql
	 * @param isNeedWhere
	 * @return
	 */
	public Map<String, Object> getQuerySql(String sql, boolean isNeedWhere){
		StringBuffer buffer = new StringBuffer(sql);
		if(isNeedWhere)
			buffer.append(" where 1 = 1 ");
		Map<String, Object> params = getAttrs();
		Iterator<String> it = params.keySet().iterator();
		String key = null, value = null, symbol = null;
		List<String> paramList = null;
		while (it.hasNext()) {
			key = it.next();
			value = String.valueOf(params.get(key));
			if(StringUtils.isNotEmpty(value)){
				if(key.contains("[")){
					symbol = key.substring(key.indexOf("[")+1, key.indexOf("]"));
					key = key.substring(0, key.indexOf("["));
					if(symbol.equals("%")){
						buffer.append(" and " + key + " like '%" + value + "%'");
						continue;
					}else if(symbol.equalsIgnoreCase("eq"))
						symbol = "=";
					buffer.append(" and " + key + " " + symbol + " ?");
				}else
					buffer.append(" and " + key + " = ?");
				if(paramList == null)
					paramList = new ArrayList<String>();
				paramList.add(value);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sql", buffer.toString());
		map.put("params", paramList == null?null:paramList.toArray(new String[paramList.size()]));
		return map;
	}
}
