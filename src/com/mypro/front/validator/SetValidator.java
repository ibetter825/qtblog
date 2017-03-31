package com.mypro.front.validator;

import java.util.Enumeration;

import com.jfinal.core.Controller;
import com.mypro.model.ResultModel;

/**
 * 当用户修改资料时的验证器
 * @author ibett
 *
 */
public class SetValidator extends BaseValidator {

	@Override
	protected void validate(Controller c) {
		validateNickNameString("info.nick_name", "error:nick_name", "请填写汉字、数字、字母、下划线，不能以下划线开头和结尾");
		validateAccPwdString("cur_pwd", "error:cur_pwd", "密码6-18位，包括数字字母下划线");
		validateAccPwdString("new_pwd", "error:new_pwd", "密码6-18位，包括数字字母下划线");
		validateAccPwdString("cfm_pwd", "error:cfm_pwd", "密码6-18位，包括数字字母下划线");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handleError(Controller c) {
		ResultModel model = new ResultModel("保存资料失败");
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
