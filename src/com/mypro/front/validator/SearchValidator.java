package com.mypro.front.validator;

import com.jfinal.core.Controller;
import com.mypro.context.QtAccountContext;
import com.mypro.model.front.QtArticle;
import com.mypro.model.front.QtNav;
import com.mypro.service.front.QtNavService;

/**
 * 文章检索验证器
 * @author ibett
 *
 */
public class SearchValidator extends BaseValidator {

	@Override
	protected void validate(Controller c) {
		validateAccPwdString("s", "s", "匹配由数字、26个英文字母、中文字符或者下划线组成的字符串");
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("content", QtArticle.getArtcilesHtml(null, false));
		c.setAttr("ctitle", "<strong>"+c.getPara("s")+" 的检索结果</strong>");
		c.setAttr("navs", QtNav.getHeaderNavHtml(QtNavService.service.queryNavs()));
		c.setAttr("opts", QtNav.getHeaderOptsHtml(QtAccountContext.getCurrentQtAccount()));
		
		c.render("/WEB-INF/view/front/module.html");
	}

}
