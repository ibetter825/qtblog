package com.mypro.service.admin;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.mypro.common.DictKeys;
import com.mypro.common.tools.MD5Utils;
import com.mypro.common.tools.StrUtils;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.common.tools.ToolSecurityPbkdf2;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.admin.SysUser;
import com.mypro.model.admin.SysUserInfo;
import com.mypro.web.tools.ToolStrYt;
import com.mypro.web.tools.ToolWeb;

public class SysUserService {
	
	public final static SysUserService service = new SysUserService();
	
	public int valiLogin(String user_name, String user_pwd, HttpServletResponse response) {
		SysUser sysUser = SysUser.dao.findFirst("select "+SysUser.all+" from sys_user where user_name = ?", user_name);
		int login_info = DictKeys.login_info_0;//用户不存在
		boolean isSuccess = false;
		if(sysUser != null){
			try {
				byte[] db_pwd = sysUser.getBytes("user_pwd");
				byte[] db_salt = sysUser.getBytes("user_salt");
				String user_no = sysUser.getStr("user_no");
				isSuccess = ToolSecurityPbkdf2.authenticate(user_pwd, db_pwd, db_salt);
				if(isSuccess){
					login_info = DictKeys.login_info_3;//登录成功
					long time = ToolDateTime.getDateByTime() + PropKit.getLong(DictKeys.config_session_maxAge);
					String userLoginInfo = user_no+":"+time+":"+MD5Utils.MD5(user_no+":"+new String(db_pwd).intern()+":"+new String(db_salt).intern()+":"+time);
					ToolWeb.addCookie(response, null, null, true, DictKeys.current_cookie_user_admin, userLoginInfo, 0);
					
					//Cache redis_user = Redis.use();
					//redis_user.set(user_no, sysUser);
				}else
					login_info = DictKeys.login_info_4;//密码验证失败
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				login_info = DictKeys.login_info_4;//密码验证失败
			} catch (InvalidKeySpecException e) {
				e.printStackTrace();
				login_info = DictKeys.login_info_4;//密码验证失败				
			}
		}
		return login_info;
	}
	public Page<SysUser> queryUserList(PagerModel pager, QueryModel rq) {
		String select = "select "+SysUser.userwithinfo;
		String sqlExceptSelect = " from sys_user user left join sys_user_info info on user.user_no = info.user_no where user.user_state <> -1";
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, false);
		sqlExceptSelect = (String) queryMap.get("sql");
		sqlExceptSelect = pager.getOrderSql(sqlExceptSelect);
		Object[] params = (String[]) queryMap.get("params");
		Page<SysUser> page = null;
		if(params == null)
			page = SysUser.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect);
		else
			page = SysUser.dao.paginate(pager.getCurPage(), pager.getPageSize(), select, sqlExceptSelect, params);
		return page;
	}
	public List<SysUser> queryUserList(QueryModel rq){
		String select = "select "+SysUser.userwithinfobase;
		String sqlExceptSelect = " from sys_user user left join sys_user_info info on user.user_no = info.user_no where user.user_state <> -1";
		Map<String, Object> queryMap = rq.getQuerySql(sqlExceptSelect, false);
		sqlExceptSelect = (String) queryMap.get("sql");
		Object[] params = (String[]) queryMap.get("params");
		List<SysUser> users = null;
		if(params == null)
			users = SysUser.dao.find(select + sqlExceptSelect);
		else
			users = SysUser.dao.find(select + sqlExceptSelect, params);
		return users;
	}
	public SysUser queryUserByUserName(String user_name){
		return SysUser.dao.findFirst("select "+SysUser.simp+" from sys_user where user_name = ?", user_name);
	}
	/**
	 * 包括user_info的数据
	 * @param user_no
	 * @return
	 */
	public SysUser queryUserAndInfoByUserNo(String user_no){
		return SysUser.dao.findFirst("select " + SysUser.userwithinfo + " from sys_user user left join sys_user_info info on user.user_no = info.user_no where user.user_no = ? ", user_no);
	}
	public SysUser queryUserAndInfoAndRoleByUserNo(String user_no){
		return SysUser.dao.findFirst("select " + SysUser.allwithinfobase + " from sys_user user left join sys_user_info info on user.user_no = info.user_no where user.user_no = ? ", user_no);
	}
	/**
	 * 保存系统用户
	 * @param user
	 * @param info
	 * @return
	 */
	@Before(Tx.class)
	public boolean saveSysUser(SysUser user, SysUserInfo info) {
		boolean res = true;
		String user_no = StrUtils.getUUID(false);
		long add_time = ToolDateTime.getDateByTime();
		byte[] user_salt;
		try {
			user_salt = ToolSecurityPbkdf2.generateSalt();
			byte[] user_pwd  = ToolSecurityPbkdf2.getEncryptedPassword(PropKit.get(DictKeys.sys_user_init_pwd), user_salt);
			user.set("user_no", user_no).set("user_pwd", user_pwd).set("user_salt", user_salt).save();
			info.set("user_no", user_no).set("add_time", add_time).save();
		} catch (NoSuchAlgorithmException e) {
			res = false;
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			res = false;
			e.printStackTrace();
		}
		return res;
	}
	@Before(Tx.class)
	public boolean updateSysUser(SysUser user, SysUserInfo info){
		user.update();
		info.update();
		return true;
	}
	public boolean updateSysUser(SysUser user){
		return user.update();
	}
	/**
	 * 删除系统用户
	 * @param user_nos
	 * @return
	 */
	public int deleteSysMenu(String[] user_nos){
		try {
			int l = Db.update("delete from sys_user where user_no in ("+ToolStrYt.joinArray(user_nos, ",", "'")+")");
			return l; 
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
