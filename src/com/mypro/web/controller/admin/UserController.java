package com.mypro.web.controller.admin;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.jfinal.aop.Duang;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Page;
import com.mypro.annotation.Controller;
import com.mypro.common.DictKeys;
import com.mypro.common.tools.ToolSecurityPbkdf2;
import com.mypro.context.SysUserContext;
import com.mypro.enums.ResultMessageEnum;
import com.mypro.model.PageResultModel;
import com.mypro.model.PagerModel;
import com.mypro.model.QueryModel;
import com.mypro.model.ResultModel;
import com.mypro.model.admin.SysUser;
import com.mypro.model.admin.SysUserInfo;
import com.mypro.service.admin.SysUserService;
import com.mypro.web.admin.BaseController;

@Controller(controllerKey = "/admin/user")
public class UserController extends BaseController {
	protected static String basePath = baseAdminPath + "/user";
	/**
	 * 查询用户列表,返回JSON
	 */
	@SuppressWarnings("unchecked")
	public void list(){
		QueryModel rq = new QueryModel(this, "user", "info");
		PagerModel pager = new PagerModel(this);
		if(pager.getPageSize() != -1){
			Page<SysUser> page = SysUserService.service.queryUserList(pager, rq);
			PageResultModel model = new PageResultModel();
			model.setRows(page.getList());
			model.setTotal(page.getTotalRow());
			model.setRowCount(page.getPageSize());
			model.setCurrent(pager.getCurPage());
			renderJson(model);
		}else{
			List<SysUser> users = SysUserService.service.queryUserList(rq);
			ResultModel model = new ResultModel();
			model.getData().put("users", users);
			renderJson(model);
		}
	}
	public void save(){
		SysUser user = getModel(SysUser.class, "user");
		SysUserInfo info = getModel(SysUserInfo.class, "info");
		String type = getPara("opt_type");
		SysUserService service = Duang.duang(SysUserService.class);
		Boolean success = false; 
		if(type.equals("add"))
			success = service.saveSysUser(user,info);
		else if(type.equals("modify")){
			info.set("user_no", user.get("user_no", SysUserContext.getCurrentSysUser().getStr("user_no")));
			success = service.updateSysUser(user, info);
		}
		ResultModel model = null;
		if(success)
			model = new ResultModel();
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	@SuppressWarnings("unchecked")
	public void form(){
		String user_no = getPara("ids");
		ResultModel model = null;
		if(StringUtils.isNotEmpty(user_no)){
			SysUser user = SysUserService.service.queryUserAndInfoByUserNo(user_no);
			model = new ResultModel();
			model.getData().put("dto", user);
		}else
			model = new ResultModel("菜单编号不能为空！");
		renderJson(model);
	}
	public void profile(){
		String user_no = SysUserContext.getCurrentSysUser().getStr("user_no");
		SysUser user = null;
		if(StringUtils.isNotEmpty(user_no))
			user = SysUserService.service.queryUserAndInfoByUserNo(user_no);
		setAttr("user", user);
		renderFreeMarker(basePath+"/profile.html");
	}
	public void remove(){
		String ids = getPara("ids");
		String[] user_nos = StringUtils.isEmpty(ids)?null:ids.split(",");
		int res = SysUserService.service.deleteSysMenu(user_nos);
		ResultModel model = null;
		if(res == user_nos.length)
			model = new ResultModel();
		else if(res < user_nos.length && res != 0)
			model = new ResultModel("2", "部分用户删除失败!");
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	public void reset(){
		SysUser user = getModel(SysUser.class, "user");
		ResultModel model = null;
		Boolean success = false; 
		byte[] user_salt;
		try {
			user_salt = ToolSecurityPbkdf2.generateSalt();
			String new_pwd = PropKit.get(DictKeys.sys_user_init_pwd);
			if(user.getStr("user_no") == null){//用户自己修改密码
				new_pwd = getPara("user_pwd");
				user.set("user_no",SysUserContext.getCurrentSysUser().get("user_no"));
			}//else 管理员重置用户密码
			byte[] user_pwd = ToolSecurityPbkdf2.getEncryptedPassword(new_pwd, user_salt);
			user.set("user_pwd", user_pwd).set("user_salt", user_salt);
			success = SysUserService.service.updateSysUser(user);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		if(success)
			model = new ResultModel();
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	/**
	 * 修改密码
	 */
	public void pwd(){
		String user_pwd = getPara("user_pwd");
		ResultModel model = null;
		Boolean success = false; 
		byte[] user_salt = null;
		try {
			user_salt = ToolSecurityPbkdf2.generateSalt();
			byte[] pwd = ToolSecurityPbkdf2.getEncryptedPassword(user_pwd, user_salt);
			SysUser user = new SysUser();
			user.set("user_no",SysUserContext.getCurrentSysUser().get("user_no")).set("user_pwd", pwd).set("user_salt", user_salt);
			success = SysUserService.service.updateSysUser(user);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		if(success)
			model = new ResultModel();
		else
			model = new ResultModel(ResultMessageEnum.EXCEPTION);
		renderJson(model);
	}
	public void valiPwd(){
		String old_pwd = getPara("fieldValue");
		String id = getPara("fieldId");
		SysUser user = SysUserContext.getCurrentSysUser();
		byte[] user_pwd = user.getBytes("user_pwd");
		byte[] user_salt = user.getBytes("user_salt");
		try {
			if(ToolSecurityPbkdf2.authenticate(old_pwd, user_pwd, user_salt))
				renderJson("[\""+id+"\",true]");
			else
				renderJson("[\""+id+"\",false]");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			renderJson("[\""+id+"\",false]");
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			renderJson("[\""+id+"\",false]");
		}
	}
	/**
	 * 验证user_name是否已经存在
	 */
	public void vali(){
		String user_name = getPara("fieldValue");
		String id = getPara("fieldId");
		SysUser user = SysUserService.service.queryUserByUserName(user_name);
		if(user != null)
			renderJson("[\""+id+"\",false]");
		else
			renderJson("[\""+id+"\",true]");
	}
}
