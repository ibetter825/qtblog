package com.mypro.enums;

public enum ResultMessageEnum {
	/**
	 * 必填项值为空
	 */
	PARAM_REQUIRED_IS_EMPTY("必填项值为空"),
	
	/**
	 * 会话过期
	 */
	SESSION_TIMEOUT("会话已过期请重新登陆"),
	
	/**
	 * FTP会话过期
	 */
	FTP_SESSION_TIMEOUT("FTP会话已过期请重新登陆"),
	
	/**
	 * 机构权限不足
	 */
	AUTH_ORG_SHORTAGE("机构权限不足"),
	
	/**
	 * 信息已存在
	 */
	DATA_ALREADY_EXISTS("信息已存在"),
	
	/**
	 * 信息不存在或已删除
	 */
	DATA_NO_EXISTS("信息不存在或已删除"),
	
	/**
	 * 信息校验失败
	 */
	DATA_CHECK_FAIL("信息校验失败"),
	
	/**
	 * 数据库存储（函数）过程返回失败(主要针对有out参数的存储过程)
	 */
	DB_PROC_RT_FAIL("数据处理过程中，遭遇失败"),
	
	/**
	 * 异常返回消息
	 */
	EXCEPTION("信息处理异常"),
	
	OPT_ERROR("操作出现错误");

	private String msg;//失败消息
	
	private ResultMessageEnum(String msg){
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
