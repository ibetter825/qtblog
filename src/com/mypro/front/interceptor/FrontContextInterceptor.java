package com.mypro.front.interceptor;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.PropKit;
import com.mypro.common.DictKeys;
import com.mypro.context.QtAccountContext;
import com.mypro.model.ResultModel;
import com.mypro.model.front.QtAccount;
import com.mypro.web.admin.BaseController;
import com.mypro.web.tools.ToolStrYt;
/**
 * 判断当前用户是否登录
 * @author ibett
 *
 */
public class FrontContextInterceptor implements Interceptor {
	public void intercept(Invocation inv) {
		BaseController controller = (BaseController) inv.getController();
		String action = inv.getActionKey();
		String admin = PropKit.get(DictKeys.config_default_admin_route);//默认的后台根目录
		if(action.startsWith(admin)){//该拦截器不拦截/admin下的请求
			inv.invoke();
			return;
		}
		QtAccount account = QtAccountContext.getCurrentQtAccount();
		if(account == null){
			//需要判断是否为ajax请求，是的话返回弹出登录框
			HttpServletRequest request = controller.getRequest();
			String header = request.getHeader("X-Requested-With");  
			boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(header);  
			if(isAjax){
				ResultModel model = new ResultModel("请先登录后再操作!");
				controller.renderJson(model);
			}else{
				String redirctUrl = request.getRequestURI();  //被拦截的请求路径
		        if(StringUtils.isBlank(redirctUrl))
		            redirctUrl = request.getHeader("referer"); 
				redirctUrl = ToolStrYt.UrlEncode(redirctUrl);
				controller.redirect("/login?redirectUrl="+redirctUrl);
			}
		}else
			inv.invoke();
	}
}
