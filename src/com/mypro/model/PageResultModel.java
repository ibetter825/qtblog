package com.mypro.model;

import java.util.List;

public class PageResultModel {
	private Boolean success = true;
	private Integer total = 0;
	private Integer rowCount = 0;
	private Integer current = 1;
	@SuppressWarnings("rawtypes")
	private List rows;
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getRowCount() {
		return rowCount;
	}
	public void setRowCount(Integer rowCount) {
		this.rowCount = rowCount;
	}
	public Integer getCurrent() {
		return current;
	}
	public void setCurrent(Integer current) {
		this.current = current;
	}
	@SuppressWarnings("rawtypes")
	public List getRows() {
		return rows;
	}
	@SuppressWarnings("rawtypes")
	public void setRows(List rows) {
		this.rows = rows;
	}
}
