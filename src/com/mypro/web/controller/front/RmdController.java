package com.mypro.web.controller.front;

import java.util.List;

import com.jfinal.aop.Clear;
import com.mypro.annotation.Controller;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.model.admin.QtRmd;
import com.mypro.service.front.QtRmdService;
import com.mypro.web.admin.BaseController;
/**
 * 推荐内容
 * @author ibett
 *
 */
@Controller(controllerKey = "/rmd")
public class RmdController extends BaseController {
	@Clear(FrontContextInterceptor.class)
	public void index(){
		String rmd_type = getPara(0,"hot");
		List<QtRmd> rmds = QtRmdService.service.queryQtRmdByType(rmd_type);
		String html = QtRmd.getDetailRmdHtml(rmds);
		renderJsonp("'"+html+"'");
	}
}
