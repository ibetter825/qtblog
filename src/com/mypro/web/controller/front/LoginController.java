package com.mypro.web.controller.front;

import org.apache.commons.lang.StringUtils;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.mypro.annotation.Controller;
import com.mypro.common.DictKeys;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.front.validator.LoginValidator;
import com.mypro.model.ResultModel;
import com.mypro.model.front.QtNav;
import com.mypro.service.front.QtAccountService;
import com.mypro.service.front.QtNavService;
import com.mypro.web.admin.BaseController;
import com.mypro.web.render.MyCaptchaRender;
/**
 * 登录
 * @author ibett
 *
 */
@Controller(controllerKey = "/login")
public class LoginController extends BaseController {
	private final static String basePath = "/WEB-INF/view/front";
	@Clear(FrontContextInterceptor.class)
	public void index(){
		if(getCurrentAccount() == null){
			String redirectUrl = getPara("redirectUrl", null);
			if(redirectUrl == null)
				redirectUrl = getRefererURI();
			setAttr("redirectUrl", redirectUrl);
			setAttr("navs", QtNav.getHeaderNavHtml(QtNavService.service.queryNavs(), ""));
			renderFreeMarker(basePath+"/login.html");
		}else
			redirect(getCtx()+"/");
	}
	@Clear(FrontContextInterceptor.class)
	@Before(LoginValidator.class)
	public void dologin(){
		String acc_str = getPara("acc_str");
		String acc_pwd = getPara("acc_pwd");
		String captcha = getPara("captcha");//验证码
		Integer remember = getParaToInt("remember", 0);//是否在15天内记住账户
		boolean isRemember = remember == 1?true:false;
		int login_info = DictKeys.login_info_0;//默认用户不存在
		//String redirectUrl = getPara("redirectUrl");
		ResultModel model = null;
		if(StringUtils.isNotEmpty(captcha)){
			boolean vali = MyCaptchaRender.validate(this, captcha);
			if(!vali)//验证码错误
				login_info = DictKeys.login_info_5;
			else//这里需要判断是手机登录，用户名登录，还是邮箱登录
				login_info = QtAccountService.service.valiLogin(acc_str, acc_pwd, isRemember, this);
		}
		if(login_info == DictKeys.login_info_3){
			model = new ResultModel();
		}else{
			if(login_info == DictKeys.login_info_4 || login_info == DictKeys.login_info_0)//密码验证失败
				model = new ResultModel("用户名或密码错误");
			else if(login_info == DictKeys.login_info_5)
				model = new ResultModel("验证码错误");
			else if(login_info == DictKeys.login_info_1)
				model = new ResultModel("账号被暂停使用");
			else if(login_info == DictKeys.login_info_2)
				model = new ResultModel("登录失败次数过多，限制登录12个小时");
			else
				model = new ResultModel("未知错误");
		}
		renderJson(model);
	}
}
