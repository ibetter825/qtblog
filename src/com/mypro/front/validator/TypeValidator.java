package com.mypro.front.validator;

import java.util.Enumeration;

import com.jfinal.core.Controller;
import com.mypro.model.ResultModel;

/**
 * 用户自定义分类验证器
 * @author ibett
 *
 */
public class TypeValidator extends BaseValidator {

	@Override
	protected void validate(Controller c) {
		validateTypeNameString("type.type_name", "error:type_name", "分类名输入无效");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handleError(Controller c) {
		ResultModel model = new ResultModel("操作失败");
		Enumeration<String> attrNames = c.getAttrNames();
		String key = null, value = null;
		for(Enumeration<String> e = attrNames; e.hasMoreElements();){
			key = e.nextElement().toString();
			value = c.getAttr(key);
			if(key.startsWith("error:"))
				model.getData().put(key.split(":")[1], value);
		}
		c.renderJson(model);
	}

}
