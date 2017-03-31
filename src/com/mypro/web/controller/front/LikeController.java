package com.mypro.web.controller.front;

import com.mypro.annotation.Controller;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.enums.ResultMessageEnum;
import com.mypro.model.ResultModel;
import com.mypro.model.front.QtAccount;
import com.mypro.model.front.QtArticleLike;
import com.mypro.service.front.QtLikeService;
import com.mypro.web.admin.BaseController;
/**
 * 点赞
 * @author ibett
 *
 */
@Controller(controllerKey = "/like")
public class LikeController extends BaseController {
	/**
	 * 默认查看喜欢文章的人名单，需要登录才能看
	 */
	public void index(){
		renderJson("");
	}
	/**
	 * 点赞,或取消点赞，同一篇文章每个人每天最多点击5次
	 */
	public void toggle(){
		Integer art_no = getParaToInt(0);//获取文章标题
		int flag = getParaToInt(1, -1);//获取操作,0:取消赞,1:点赞操作
		QtAccount account = getCurrentAccount();
		boolean l = false;
		
		QtArticleLike like = new QtArticleLike().set("art_no", art_no).set("acc_no", account.get("acc_no")).set("like_time", ToolDateTime.getDateByTime());
		if(flag == 1)
			l = QtLikeService.service.saveQtArtLike(like);
		else if(flag == 0)
			l = QtLikeService.service.removeQtArtLike(like);
		ResultModel res = null;
		if(l)
			res = new ResultModel();
		else
			res = new ResultModel(ResultMessageEnum.DB_PROC_RT_FAIL);
		renderJson(res);
	}
}
