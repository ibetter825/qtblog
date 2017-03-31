package com.mypro.web.controller.admin;

import com.mypro.annotation.Controller;
import com.mypro.web.admin.BaseController;

@Controller(controllerKey = "/admin/desk")
public class DeskController extends BaseController {
	protected static String basePath = baseAdminPath + "/desk";
	public void index(){
		render(basePath+"/desk.html");
	}
}
