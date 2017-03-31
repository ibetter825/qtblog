package com.mypro.web.controller.front;

import com.jfinal.aop.Clear;
import com.mypro.annotation.Controller;
import com.mypro.common.tools.ToolDateTime;
import com.mypro.front.interceptor.FrontContextInterceptor;
import com.mypro.model.ResultModel;
import com.mypro.model.front.QtAccount;
import com.mypro.model.front.QtReport;
import com.mypro.service.front.QtReportService;
import com.mypro.web.admin.BaseController;
import com.mypro.web.tools.ToolStrYt;
/**
 * 举报
 * @author ibett
 *
 */
@Controller(controllerKey = "/report")
@Clear(FrontContextInterceptor.class)
public class ReportController extends BaseController {
	/**
	 * 添加举报记录
	 */
	public void index(){
		Integer type = getParaToInt(0);//举报类型
		String no = getPara(1, null);//举报对象的编号
		String reason = ToolStrYt.UrlDecode(getPara(2, ""));//举报原因
		QtAccount account = getCurrentAccount();
		Integer acc = 0;
		boolean res = false;
		ResultModel model = null;
		if(account != null)
			acc = account.getInt("acc_no");
		if(no != null){
			QtReport report = new QtReport();
			report.set("report_type", type);
			report.set("add_time", ToolDateTime.getDateByTime());
			report.set("report_acc", acc);
			report.set("report_reason", reason);
			
			switch (type) {
				case 1:
					report.set("comt_no", no);
					break;
				case 2:
					report.set("reply_no", no);
					break;
				case 3:
					report.set("art_no", no);
					break;
				case 4:
					report.set("acc_no", no);
					break;
				default:
					model = new ResultModel("举报的对象不存在");
					break;
			}
			if(model == null){
				res = QtReportService.service.saveQtReport(report);
				if(res)
					model = new ResultModel();
				else
					model = new ResultModel("请稍后再试");
			}
		}else
			model = new ResultModel("举报的对象不存在");
		renderJson(model);
	}
}
