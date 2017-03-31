/**
 * 
 */
package com.mypro.common.tools;

import org.apache.commons.lang.StringUtils;

/**
 * @author jack.li
 *
 */
public class AuthCodeUtils {

	public static final String encryptAuthCode(String sep, Object... args) {
		return DesSecretUtils.encrypt(StringUtils.join(args, sep));
	}
	
	public static final String[] decryptAuthCode(String authcode, String sep) {
		String temp = DesSecretUtils.decrypt(authcode);
		return temp.split(sep);
	}
}
