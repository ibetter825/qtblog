package com.mypro.service.front;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import com.mypro.common.DictKeys;
import com.mypro.common.tools.MD5Utils;
import com.mypro.common.tools.StrUtils;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.model.front.QtAccount;
import com.mypro.model.front.QtAccountInfo;
import com.mypro.web.tools.ToolWeb;

public class QtAccountService {
	public final static QtAccountService service = new QtAccountService();
	
	public QtAccount queryAccountById(String acc_no){
		return QtAccount.dao.findById(acc_no);
	}
	public QtAccount queryAccAndInfoById(Integer acc_no){
		String sql = "select "+QtAccount.allwithacc+" from qt_account acc left join qt_account_info info on acc.acc_no = info.acc_no where acc.acc_state = 1 and acc.acc_no = ?";
		return QtAccount.dao.findFirst(sql, acc_no);
	}
	/**
	 * 用于登录验证拦截器
	 * @param acc_no
	 * @return
	 */
	public QtAccount queryAccAndInfoByIdForIntercepter(final Integer acc_no){
		//缓存已登录用户的数据，在用户修改资料后需要修改缓存
		QtAccount account = CacheKit.get("accountCache", acc_no, new IDataLoader(){
			public Object load(){
				String sql = "select "+QtAccount.allwithacc+" from qt_account acc left join qt_account_info info on acc.acc_no = info.acc_no where acc.acc_state = 1 and acc.acc_no = ?";
				return QtAccount.dao.findFirst(sql, acc_no);
			}
		});
		return account;
	}
	public QtAccount queryAccAndInfoWithoutPwdById(Integer acc_no){
		String sql = "select "+QtAccount.allwithaccwithout+" from qt_account acc left join qt_account_info info on acc.acc_no = info.acc_no where acc.acc_state = 1 and acc.acc_no = ?";
		return QtAccount.dao.findFirst(sql, acc_no);
	}
	public QtAccount queryAccAndInfoForSetting(Integer acc_no){
		String sql = "select "+QtAccount.allforsetting+" from qt_account acc left join qt_account_info info on acc.acc_no = info.acc_no where acc.acc_state = 1 and acc.acc_no = ?";
		return QtAccount.dao.findFirst(sql, acc_no);
	}
	public int valiLogin(String acc_str, String acc_pwd, boolean isRemember, Controller controller) {
		int type = 1;//用户名登录
		String sql = "select a.acc_no,a.acc_name,a.acc_pwd,a.acc_salt,a.acc_state,i.login_error_count,i.login_stop_time from qt_account a left join qt_account_info i on i.acc_no = a.acc_no where a.acc_name = ?";//默认用户名 
		if(type == 2)
			sql = "";
		QtAccount account = QtAccount.dao.findFirst(sql, acc_str);
		int login_info = DictKeys.login_info_0;//用户不存在
		boolean isSuccess = false;
		if(account != null){
			int login_error_count = account.getInt("login_error_count");
			long time = ToolDateTime.getDateByTime();
			if(account.getInt("acc_state") == 1){
				if(login_error_count >= 5){
					long login_stop_time = account.getLong("login_stop_time");
					if(time - login_stop_time < 1000 * 60 * 60 * 24)//限制登录24小时
						return DictKeys.login_info_2;//密码登录次数错误，24小时后再重试
				}
				
				String db_pwd = account.getStr("acc_pwd");
				String db_salt = account.getStr("acc_salt");
				Integer user_no = account.getInt("acc_no");
				String auth_pwd = MD5Utils.MD5(acc_pwd+db_salt);
				isSuccess = auth_pwd.equals(db_pwd);
				
				QtAccountInfo info = new QtAccountInfo().set("acc_no", user_no);
				if(isSuccess){
					login_info = DictKeys.login_info_3;//登录成功
					int maxAge = 0;
					if(isRemember)
						maxAge = 15*24*60*60;//保存15天
					String userLoginInfo = user_no+":"+time+":"+maxAge+":"+MD5Utils.MD5(user_no+":"+db_pwd+":"+db_salt+":"+time+":"+maxAge);
					ToolWeb.addCookie(controller, null, null, true, DictKeys.current_cookie_acc_front, userLoginInfo, maxAge);
					info.set("login_error_count", 0).set("login_stop_time", 0).set("last_login_time", time).set("last_login_ip", ToolWeb.getIpAddr(controller)).update();
				}else{
					login_info = DictKeys.login_info_4;//密码验证失败
					info.set("login_error_count", login_error_count + 1).set("login_stop_time", time).update();
				}
			}else
				login_info = DictKeys.login_info_1;//用户账户限制登录
		}
		return login_info;
	}
	/**
	 * 验证用户名或者昵称是否已经存在
	 * @param type
	 * @param val
	 * @param acc_no 当前用户的编号，如果查询出来的acc与此相等，表示可以使用
	 * @return
	 */
	public boolean vali(String type, String val, Integer acc_no){
		boolean res = false;
		if(type.equals("nick")){
			Record record = Db.findFirst("select acc_no from qt_account_info where nick_name = ?", val);
			if(record != null){
				if(record.get("acc_no").equals(acc_no))
					res = true;//传入的昵称本来就是该用户的昵称，可以使用
			}else
				res = true;
		}else if(type.equals("name")){
			QtAccount account = QtAccount.dao.findFirst("select acc_no from qt_account where acc_name = ?", val);
			if(account == null)
				res = true;
		}
		return res;
	}
	/**
	 * 查询用户密码，与盐
	 * @param acc_no
	 * @return
	 */
	public QtAccount queryAccountPwd(String acc_no){
		String sql = "select acc_pwd,acc_salt from qt_account where acc_no = ?";
		return QtAccount.dao.findFirst(sql, acc_no);
	}
	/**
	 * 修改用户和用户详细资料
	 * @param account
	 * @param info
	 * @return
	 */
	@Before(Tx.class)
	public boolean updateAccountAndInfo(QtAccount account, QtAccountInfo info){
		if(account == null)
			return info.update();
		if(info == null)
			return account.update();
		return account.update() && info.update();
	}
	public static void main(String[] args) {
		System.out.println(StrUtils.getUUID(false));
		System.out.println(MD5Utils.MD5("y825528yyang"));
	}
}
