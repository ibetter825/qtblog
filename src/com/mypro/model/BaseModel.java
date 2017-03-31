package com.mypro.model;

import java.util.Iterator;
import java.util.Map;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

public class BaseModel <M extends Model<M>> extends Model<M> {
	private static final long serialVersionUID = 1L;
	/*private Class<M> type;
	public BaseModel(Class<M> type) {
		this.type = type;
	}*/
	public Record convertModelToRecord(){
		Map<String, Object> map = getAttrs();
		Iterator<String> it = map.keySet().iterator();
		String key = null; Object value = null;
		Record record = null;
		while (it.hasNext()) {
			key = it.next();
			value = map.get(key);
			if(record == null)
				record = new Record();
			record.set(key, value);
		}
		return record;
	}
	protected static String getPrefixFields(String[] fieldArr, String[] prefixArr){
		StringBuffer buffer = new StringBuffer();
		String[] fields = null;
		for (int i = 0; i < fieldArr.length; i++) {
			fields = fieldArr[i].split(",");
			for (String f : fields)
				buffer.append(prefixArr[i]+"."+f+",");
		}
		String res = buffer.toString(); 
		return res.substring(0, res.length() - 1);
	}
}
