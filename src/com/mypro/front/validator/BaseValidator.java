package com.mypro.front.validator;

import com.jfinal.validate.Validator;

public abstract class BaseValidator extends Validator {
	
	//可以下划线，中英文混合，不能以下划线开头和结尾
	private static final String nickNamePattern="(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]+";
	//校验手机号码正则表达式
	private static final String phoneNumberPattern="\\b(1[3,5,7,8,9]\\d{9})\\b";
	//密码正则表达式
	private static final String accPwdPattern = "[a-zA-Z0-9_-]{6,18}";
	private static final String searchKetPattern = "[\u4e00-\u9fa5\\w]*";
	private static final String accNamePattern = "[a-zA-Z]\\w{1,10}";//用户账号字母开头，加数字，最多10个字符
	private static final String captchaPattern = "[A-Za-z0-9]{4}";//验证码
	//中英文混合不能超过10个字符
	private static final String typeNamePattern = "[a-zA-Z0-9\u4e00-\u9fa5]{1,10}";
	//匹配除空格之外的任何字符，最多40个字符
	//private static final String articleTilePattern = "\\S{1,40}";//"[a-zA-Z0-9_().,，\u4e00-\u9fa5]{1,30}";
	/**
	 * 验证用户昵称，(并且不能超过15个字符，长度先不管)
	 */
	protected void validateNickNameString(String field, String errorKey, String errorMessage) {
		Integer type = controller.getParaToInt("type");
		if(type != 1)//不是修改基础资料
			return;
		validateRegex(field, nickNamePattern, errorKey, errorMessage);
	}
	/**
	 * 用户名
	 * @param field
	 * @param errorKey
	 * @param errorMessage
	 */
	protected void validateAccNameString(String field, String errorKey, String errorMessage) {
		validateRegex(field, accNamePattern, errorKey, errorMessage);
	}
	protected void validateCaptchaString(String field, String errorKey, String errorMessage) {
		validateRegex(field, captchaPattern, errorKey, errorMessage);
	}
	/**
	 * 验证密码
	 * @param field
	 * @param errorKey
	 * @param errorMessage
	 */
	protected void validateAccPwdString(String field, String errorKey, String errorMessage) {
		Integer type = controller.getParaToInt("type", 3);
		if(type != 3)//不是修改密码
			return;
		validateRegex(field, accPwdPattern, errorKey, errorMessage);
	}
	/**
	 * 验证搜索的关键词
	 * @param field
	 * @param errorKey
	 * @param errorMessage
	 */
	protected void validateSearchKeyString(String field, String errorKey, String errorMessage) {
		validateRegex(field, searchKetPattern, errorKey, errorMessage);
	}
	protected void validatePhoneNumberString(String field, String errorKey, String errorMessage) {
		validateRegex(field, phoneNumberPattern, errorKey, errorMessage);
	}
	/**
	 * 验证用户文章自定义分类
	 * @param field
	 * @param errorKey
	 * @param errorMessage
	 */
	protected void validateTypeNameString(String field, String errorKey, String errorMessage) {
		validateRegex(field, typeNamePattern, errorKey, errorMessage);
	}
	/**
	 * 文章标题不能超哦过40个字符，英文一个字符，中文当两个字符
	 * @param field
	 * @param errorKey
	 * @param errorMessage
	 */
	protected void validateArticleTitleString(String field, String errorKey, String errorMessage) {
		String val = controller.getPara(field);
		if(val.replaceAll("[^\\x00-\\xff]", "01").length() > 80)
			addError(errorKey, errorMessage);
		//validateRegex(field, articleTilePattern, errorKey, errorMessage);
	}
}
