package com.mypro.web.controller.front;

import com.mypro.annotation.Controller;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.enums.ResultMessageEnum;
import com.mypro.model.ResultModel;
import com.mypro.model.front.QtAccount;
import com.mypro.model.front.QtArticleColet;
import com.mypro.service.front.QtColetService;
import com.mypro.web.admin.BaseController;
/**
 * 收藏
 * @author ibett
 *
 */
@Controller(controllerKey = "/colet")
public class ColetController extends BaseController {
	/**
	 * 默认查看收藏文章的人名单，需要登录才能看
	 */
	public void index(){
		renderJson("");
	}
	/**
	 * 收藏,或取消收藏，同一篇文章每个人每天最多点击5次
	 */
	public void toggle(){
		Integer art_no = getParaToInt(0);//获取文章标题
		int flag = getParaToInt(1, -1);//获取操作,0:取消收藏,1:收藏操作
		QtAccount account = getCurrentAccount();
		boolean l = false;
		
		QtArticleColet colet = new QtArticleColet().set("art_no", art_no).set("acc_no", account.get("acc_no")).set("collect_time", ToolDateTime.getDateByTime());
		if(flag == 1)
			l = QtColetService.service.saveQtArtColet(colet);
		else if(flag == 0)
			l = QtColetService.service.removeQtArtColet(colet);
		ResultModel res = null;
		if(l)
			res = new ResultModel();
		else
			res = new ResultModel(ResultMessageEnum.DB_PROC_RT_FAIL);
		renderJson(res);
	}
}
