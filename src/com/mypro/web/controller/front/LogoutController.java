package com.mypro.web.controller.front;

import javax.servlet.http.Cookie;

import com.mypro.annotation.Controller;
import com.mypro.common.DictKeys;
import com.mypro.model.ResultModel;
import com.mypro.model.front.QtAccount;
import com.mypro.web.admin.BaseController;
import com.mypro.web.tools.ToolWeb;
/**
 * 登出
 * @author ibett
 *
 */
@Controller(controllerKey = "/logout")
public class LogoutController extends BaseController {
	public void index(){
		QtAccount account = getCurrentAccount();
		boolean res = false;
		if(account != null){
			Cookie cookie = getCookieObject(DictKeys.current_cookie_acc_front);
			if(cookie != null){
				ToolWeb.addCookie(this, null, null, true, DictKeys.current_cookie_acc_front, null, 0);
				res = true;
			}
		}
		ResultModel model = null;
		if(res)
			model = new ResultModel();
		else
			model = new ResultModel("登出失败");
		renderJson(model);
	}
}
