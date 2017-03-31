package com.mypro.front.interceptor;

import javax.servlet.http.Cookie;
import org.apache.commons.lang.StringUtils;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.PropKit;
import com.mypro.common.DictKeys;
import com.mypro.common.tools.MD5Utils;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.context.QtAccountContext;
import com.mypro.model.front.QtAccount;
import com.mypro.service.front.QtAccountService;
import com.mypro.web.admin.BaseController;
import com.mypro.web.tools.ToolWeb;
/**
 * 前台登录拦截器
 * @author ibett
 *
 */
public class FrontCurrentInterceptor implements Interceptor {
	public void intercept(Invocation inv) {
		BaseController controller = (BaseController) inv.getController();
		String action = inv.getActionKey();
		String admin = PropKit.get(DictKeys.config_default_admin_route);//默认的后台根目录
		if(action.startsWith(admin)){//该拦截器不拦截/admin下的请求
			inv.invoke();
			return;
		}
		Cookie cookie = controller.getCookieObject(DictKeys.current_cookie_acc_front);
		//user_no+":"+time+":"+maxAge+":"+MD5Utils.MD5(user_no+":"+db_pwd+":"+db_salt+":"+time+":"+maxAge);
		int is_login = 0;
		if(cookie != null){
			String cookie_user_info = cookie.getValue();
			String[] cookie_values = cookie_user_info.split(":");
			if(StringUtils.isNotEmpty(cookie_user_info) && cookie_values.length > 3){
				Integer acc_no = Integer.parseInt(cookie_values[0]);
				long otime = Long.valueOf(cookie_values[1]);//如果otime==0的话，不手动判断过期时间，不是0的话，需要重新计算剩余cookie剩余时间
				int maxAge = Integer.valueOf(cookie_values[2]);
				long rtime = ToolDateTime.getDateByTime();
				String cookie_login_info = cookie_values[3];
				QtAccount account = QtAccountService.service.queryAccAndInfoByIdForIntercepter(acc_no);
				if(account != null){
					String db_pwd = account.getStr("acc_pwd");
					String db_salt = account.getStr("acc_salt");
					String db_login_Info = MD5Utils.MD5(acc_no+":"+db_pwd+":"+db_salt+":"+otime+":"+maxAge);
					if(cookie_login_info.equals(db_login_Info)){//已登录的用户
						is_login = 1;
						new QtAccountContext(account);
						String new_cookie_acc_info = acc_no+":"+rtime+":"+maxAge+":"+MD5Utils.MD5(acc_no+":"+db_pwd+":"+db_salt+":"+rtime+":"+maxAge);
						if(maxAge != 0)
							maxAge = (int) (maxAge - (rtime - otime) / 1000);//maxAge-(当前时间戳-老时间戳)/1000得到秒数
						ToolWeb.addCookie(controller, null, null, true, DictKeys.current_cookie_acc_front, new_cookie_acc_info, maxAge);
					}
				}
			}else
				new QtAccountContext(null);
		}else
			new QtAccountContext(null);
		//不是ajax请求的话需要加上导航
		/*String header = controller.getRequest().getHeader("X-Requested-With");  
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(header);
        if(!isAjax){
        	//只有这个action才需要设置 已选中的导航条
        	String theme = null;
        	if(action.equals("/"))
        		theme = controller.getPara(0, "home");
        	controller.setAttr("navs", QtNav.getHeaderNavHtml(QtNavService.service.queryNavs(), theme, controller.getCtx()));
        }*/
        
		cookie = controller.getCookieObject(DictKeys.front_cookie_is_login);
		if(cookie == null || !cookie.getValue().equals(is_login+""))
			ToolWeb.addCookie(controller, null, null, false, DictKeys.front_cookie_is_login, is_login+"", 0);
		inv.invoke();
	}
}
