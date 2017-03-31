package com.mypro.service.front;

import com.mypro.model.front.QtReport;

public class QtReportService {
	public final static QtReportService service = new QtReportService();
	public boolean saveQtReport(QtReport report){
		return report.save();
	}
}
