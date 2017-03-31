package com.mypro.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.mypro.enums.ResultMessageEnum;

public class ResultModel {
	/**
	 * 操作成功代码
	 */
	public static final String SUCCESS = "0";
	/**
	 * 操作成功消息
	 */
	public static final String SUCCESS_MSG = "操作成功";
	/**
	 * 操作失败消息前缀
	 */
	public static final String FAIL_MSG_STARTS = "操作失败，原因：";
	/**
	 * 操作失败代码(可自定义多种失败代码,如:-2、-3...)
	 */
	public static final String FAIL = "-1";
	
	private String code = SUCCESS;//是否操作成功
	private String msg = SUCCESS_MSG;//操作反馈消息
	@SuppressWarnings("rawtypes")
	private Map data = new LinkedHashMap();//返回数据
	
	public ResultModel(){}
	
	public ResultModel(String failMsg){
		this.code = FAIL;
		this.msg = failMsg;
	}
	
	public ResultModel(ResultMessageEnum resultMessageEnum){
		this.code = FAIL;
		this.msg = FAIL_MSG_STARTS + resultMessageEnum.getMsg();
	}
	
	public ResultModel(String code, String msg){
		this.code = code;
		this.msg = msg;
	}
	
	/**
	 * 是否成功
	 * 
	 * @return true:成功
	 */
	public boolean isSuccess(){
		return SUCCESS.equals(code);
	}
	
	public void setSuccessMsg(String msg) {
		this.code = SUCCESS;
		this.msg = msg;
	}
	
	public void setFailMsg(String msg) {
		this.code = FAIL;
		this.msg = FAIL_MSG_STARTS + msg;
	}
	
	public void setFailMsg(ResultMessageEnum resultMessageEnum) {
		this.code = FAIL;
		this.msg = FAIL_MSG_STARTS + resultMessageEnum.getMsg();
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}

	@SuppressWarnings("rawtypes")
	public Map getData() {
		return data;
	}

	public void setData(@SuppressWarnings("rawtypes") Map data) {
		this.data = data;
	}
}
