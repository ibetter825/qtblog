package com.mypro.front.validator;

import java.util.Enumeration;

import com.jfinal.core.Controller;
import com.mypro.model.ResultModel;

/**
 * 当用户修改资料时的验证器
 * @author ibett
 *
 */
public class LoginValidator extends BaseValidator {

	@Override
	protected void validate(Controller c) {
		validateAccNameString("acc_str", "error:acc_str", "用户名输入错误");
		validateAccPwdString("acc_pwd", "error:acc_pwd", "密码输入有误");
		validateCaptchaString("captcha", "error:captcha", "验证码输入有误");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handleError(Controller c) {
		ResultModel model = new ResultModel("登录失败");
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
