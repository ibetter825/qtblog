package com.mypro.web.controller.front;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Duang;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mypro.annotation.Controller;
import com.mypro.common.tools.MD5Utils;
import com.mypro.enums.ResultMessageEnum;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.front.validator.SetValidator;
import com.mypro.model.ResultModel;
import com.mypro.model.front.QtAccount;
import com.mypro.model.front.QtAccountInfo;
import com.mypro.model.front.QtNav;
import com.mypro.service.front.QtAccountService;
import com.mypro.web.admin.BaseController;
/**
 * 用户
 * @author ibett
 *
 */
@Controller(controllerKey = "/acc")
public class AccountController extends BaseController {
	/**
	 * 返回header用户条
	 */
	@Clear(FrontContextInterceptor.class)
	public void index(){
		renderHtml(QtNav.getHeaderOptsHtml(getCurrentAccount()));
	}
	/**
	 * 获取用户信息，昵称，头像
	 */
	@Clear(FrontContextInterceptor.class)
	public void info(){
		Integer acc_no = getParaToInt("accno");
		Map<String, String> res = new HashMap<String, String>();
		if(acc_no != null){
			QtAccount account = QtAccountService.service.queryAccAndInfoWithoutPwdById(acc_no);
			if(account != null){
				res.put("name", account.getStr("nick_name"));
				res.put("avatar", account.getStr("acc_avatar"));
			}
		}
		renderJsonp(JsonKit.toJson(res));
	}
	/**
	 * 验证用户昵称称或者用户名是否存在
	 */
	public void vali(){
		String type = getPara("type");//name验证用户名，nick验证用户昵称
		String val = getPara("fieldValue");//传入的值
		String id = getPara("fieldId");
		boolean res = QtAccountService.service.vali(type, val, getCurrentAccount().getInt("acc_no"));
		if(res)
			renderJson("[\""+id+"\",true]");
		else
			renderJson("[\""+id+"\",false]");
	}
	/**
	 * 用户资料修改
	 */
	@SuppressWarnings("unchecked")
	@Before(SetValidator.class)
	public void set(){
		//通过一个字段来判断type是基础设置修改1，还是修改头像2，还是修改密码3,还是
		Integer type = getParaToInt("type");
		QtAccountService service = Duang.duang(QtAccountService.class);
		ResultModel model = null;
		QtAccount account =	getCurrentAccount();//service.queryAccountPwd(acc_no);
		Integer acc_no = account.getInt("acc_no");
		if(type == 1 || type == 2){//基础资料
			QtAccountInfo info = getModel(QtAccountInfo.class, "info");
			boolean res = service.updateAccountAndInfo(null, info);
			/*if(type == 2){//上传头像，上传失败还需要将上传的图片删除，上传成功后还要将之前的头像删除
				String prev_avatar = getPara("prev_avatar", "");
				//可以新建一张表，每天定时清理需要删除的文件
			}*/
			if(res)
				model = new ResultModel();
			else
				model = new ResultModel(ResultMessageEnum.OPT_ERROR);
		}else if(type == 3){//修改密码
			//cur_pwd new_pwd confirm_pwd
			String cur_pwd = getPara("cur_pwd");
			String new_pwd = getPara("new_pwd");
			String cfm_pwd = getPara("cfm_pwd");
			String acc_pwd = account.get("acc_pwd");
			String acc_salt = account.getStr("acc_salt");
			
			if(!new_pwd.equals(cfm_pwd)){//输入的新密码与确认密码不相等
				model = new ResultModel("修改密码失败");
				model.getData().put("cfm_pwd", "两次输入的密码不同");
			}else if(!MD5Utils.MD5(cur_pwd+acc_salt).equals(acc_pwd)){//输入的原密码不相等
				model = new ResultModel("修改密码失败");
				model.getData().put("cur_pwd", "当前密码不正确");
			}else{
				new_pwd = MD5Utils.MD5(new_pwd+acc_salt);
				account = new QtAccount();
				//可考虑在修改 密码成功后，重新生成盐
				account.set("acc_no", acc_no);
				account.set("acc_pwd", new_pwd);
				boolean res = service.updateAccountAndInfo(account, null);//修改密码成功后需要重新设置cookie
				if(res)
					model = new ResultModel();
				else
					model = new ResultModel(ResultMessageEnum.OPT_ERROR);
			}
		}
		if(model.isSuccess())//修改资料成功后，删除缓存
			CacheKit.remove("accountCache", acc_no);
		renderJson(model);
	}
}
