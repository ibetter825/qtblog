package com.mypro.web.controller.front;

import com.jfinal.aop.Before;
import com.mypro.annotation.Controller;
import com.mypro.front.validator.TypeValidator;
import com.mypro.model.ResultModel;
import com.mypro.model.front.QtAccount;
import com.mypro.model.front.QtArticleType;
import com.mypro.service.front.QtArticleTypeService;
import com.mypro.web.admin.BaseController;

/**
 * 用户自定义分类
 * @author ibett
 *
 */
@Controller(controllerKey = "/type")
//@Clear
public class ArticleTypeController extends BaseController {
	
	public void del(){
		Integer type_id = getParaToInt(0);
		boolean res = QtArticleTypeService.service.removeQtArticleType(new QtArticleType().set("type_id", type_id));
		ResultModel model = null;
		if(res)
			model = new ResultModel();
		else
			model = new ResultModel("删除失败");
		renderJson(model);
	}
	
	@SuppressWarnings("unchecked")
	@Before(TypeValidator.class)
	public void save(){
		QtArticleType type = getModel(QtArticleType.class, "type");
		Integer type_id = type.get("type_id", null);
		boolean result = false;
		QtAccount account = getCurrentAccount();
		Integer acc_no = account.getInt("acc_no");
		type.set("acc_no", acc_no);
		if(type_id != null){//修改
			result = QtArticleTypeService.service.updateQtArticleType(type);
		}else
			result = QtArticleTypeService.service.saveQtArticleType(type);
		if(result){
			ResultModel model = new ResultModel();
			model.getData().put("type", type);
			renderJson(model);
		}else
			renderJson(new ResultModel("操作失败"));
	}
}
