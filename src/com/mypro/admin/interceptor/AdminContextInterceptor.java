package com.mypro.admin.interceptor;

import javax.servlet.http.Cookie;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.mypro.common.DictKeys;
import com.mypro.common.tools.MD5Utils;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.context.SysUserContext;
import com.mypro.model.admin.SysLogs;
import com.mypro.model.admin.SysOperate;
import com.mypro.model.admin.SysUser;
import com.mypro.service.admin.SysLogsService;
import com.mypro.service.admin.SysOperateService;
import com.mypro.service.admin.SysUserService;
import com.mypro.web.admin.BaseController;
import com.mypro.web.tools.ToolWeb;
/**
 * 登录拦截器
 * @author ibett
 *
 */
public class AdminContextInterceptor implements Interceptor {
	/**
	 * 本拦截器需要排除的Controller与Method
	 */
	//public static Set<String> excludes = new HashSet<String>();
	/*public static void setExcludes(String key){
		excludes.add(key);
	}*/
	public void intercept(Invocation inv) {
		//String action_key = inv.getActionKey();
		/*if(excludes.contains(action_key)){
			inv.invoke();
			return;
		}*/
		String admin = PropKit.get(DictKeys.config_default_admin_route);//默认的后台根目录
		BaseController controller = (BaseController) inv.getController();
		String action = inv.getActionKey();
		if(!action.startsWith(admin)){//该拦截器只拦截/admin下的请求
			inv.invoke();
			return;
		}
		//如果请求的地址为 后台设置的默认根目录则替换为/admin以便访问后台，这样可以随时更换后台地址，需要再扫描Controller注解时替换掉原来的/admin
		controller.setAttr("admin", admin);
		Cookie cookie = controller.getCookieObject(DictKeys.current_cookie_user_admin);
		if(cookie != null){
			String cookie_user_info = cookie.getValue();
			String[] cookie_values = cookie_user_info.split(":");
			//Cookie存入值 -> 用户ID:时间戳:res
			String user_no = cookie_values[0];
			long otime = Long.valueOf(cookie_values[1]);
			long rtime = ToolDateTime.getDateByTime();
			if(otime > rtime){//还没过期
				String cookie_login_info = cookie_values[2];
				//Cache redis_user = Redis.use();
				//SysUser sys_user = redis_user.get(user_no);
				//if(sys_user == null)
				SysUser sys_user = SysUserService.service.queryUserAndInfoAndRoleByUserNo(user_no);//SysUser.dao.findById(user_no);
				if(sys_user != null){
					byte[] db_pwd = sys_user.getBytes("user_pwd");
					byte[] db_salt = sys_user.getBytes("user_salt");
					String db_login_Info = MD5Utils.MD5(user_no+":"+new String(db_pwd).intern()+":"+new String(db_salt).intern()+":"+otime);
					if(cookie_login_info.equals(db_login_Info)){
						/*try (SysUserContext context = new SysUserContext(sys_user)){*/
							new SysUserContext(sys_user);
							inv.invoke();
							rtime += PropKit.getLong(DictKeys.config_session_maxAge);
							String new_cookie_user_info = user_no+":"+rtime+":"+MD5Utils.MD5(user_no+":"+new String(db_pwd).intern()+":"+new String(db_salt).intern()+":"+rtime);
							ToolWeb.addCookie(controller, null, null, true, DictKeys.current_cookie_user_admin, new_cookie_user_info, 0);
							
							//记录操作日志
							String action_key = inv.getActionKey();
							if(action_key.endsWith(admin+"/operate")){
								String menu_no = controller.getPara("mid");
								String opt_no = controller.getPara("opt", "list");
								if(!opt_no.equals("list")){
									SysOperate operate = SysOperateService.service.queryOperate(menu_no, opt_no);
									String menu_name = operate.getStr("menu_name");
									String opt_name = operate.getStr("opt_name");
									
									SysLogs log = new SysLogs();
									log.set("log_desc", menu_name+"-"+opt_name);
									log.set("log_uri", admin+"/operate?mid="+menu_no+"&opt="+opt_no);
									log.set("log_ip", ToolWeb.getIpAddr(controller));
									log.set("log_time", ToolDateTime.getDateByTime());
									log.set("log_user", user_no);
									SysLogsService.service.saveLogs(log);
								}
							}
							return;
						/*} catch (Exception e) {
							e.printStackTrace();
						}*/
					}
				}
			}
		}
		controller.redirect(admin+"/login");
	}
}
