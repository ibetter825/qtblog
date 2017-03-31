package com.mypro.web.controller.front;

import com.jfinal.aop.Clear;
import com.mypro.annotation.Controller;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.web.admin.BaseController;
import com.mypro.web.render.MyCaptchaRender;

@Controller(controllerKey = "/captcha")
public class CaptchaController extends BaseController {
	@Clear(FrontContextInterceptor.class)
	public void index(){
		render(new MyCaptchaRender(60,22,4,true));
	}
}
