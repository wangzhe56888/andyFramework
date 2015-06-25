package com.andy.framework.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: 验证工具类
 * 主要使用正则验证方法，集成了邮箱，手机号，身份证等的验证
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-6-9  下午3:17:48
 */

/**
整数或者小数：^[0-9]+\.{0,1}[0-9]{0,2}$
只能输入数字："^[0-9]*$"。
只能输入n位的数字："^\d{n}$"。
只能输入至少n位的数字："^\d{n,}$"。
只能输入m~n位的数字：。"^\d{m,n}$"
只能输入零和非零开头的数字："^(0|[1-9][0-9]*)$"。
只能输入有两位小数的正实数："^[0-9]+(.[0-9]{2})?$"。
只能输入有1~3位小数的正实数："^[0-9]+(.[0-9]{1,3})?$"。
只能输入非零的正整数："^\+?[1-9][0-9]*$"。
只能输入非零的负整数："^\-[1-9][]0-9"*$。
只能输入长度为3的字符："^.{3}$"。
只能输入由26个英文字母组成的字符串："^[A-Za-z]+$"。
只能输入由26个大写英文字母组成的字符串："^[A-Z]+$"。
只能输入由26个小写英文字母组成的字符串："^[a-z]+$"。
只能输入由数字和26个英文字母组成的字符串："^[A-Za-z0-9]+$"。
只能输入由数字、26个英文字母或者下划线组成的字符串："^\w+$"。
验证用户密码："^[a-zA-Z]\w{5,17}$" 正确格式为：以字母开头，长度在6~18之间，只能包含字符、数字和下划线。
验证是否含有^%&',;=?$\"等字符："[^%&',;=?$\x22]+"。
只能输入汉字："^[\u4e00-\u9fa5]{0,}$"
验证Email地址："^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$"。
验证InternetURL："^http://([\w-]+\.)+[\w-]+(/[\w-./?%&=]*)?$"。
验证电话号码："^(\(\d{3,4}-)|\d{3.4}-)?\d{7,8}$"正确格式为："XXX-XXXXXXX"、"XXXX-XXXXXXXX"、"XXX-XXXXXXX"、"XXX-XXXXXXXX"、"XXXXXXX"和"XXXXXXXX"。
手机号码:/^0{0,1}(13[0-9]|15[7-9]|153|156|18[7-9])[0-9]{8}$/
		区号+座机号码+分机号码：regexp="^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$"
		手机(中国移动手机号码)：regexp="^((\d3)|(\d{3}\-))?13[456789]\d{8}|15[89]\d{8}"
		所有手机号码：regexp="^((\d3)|(\d{3}\-))?13[0-9]\d{8}|15[89]\d{8}"(新添加了158,159两个号段)
		((\d{11})|^((\d{7,8})|(\d{4}|\d{3})-(\d{7,8})|(\d{4}|\d{3})-(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1})|(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1}))$)
	 
验证身份证号（15位或18位数字）："^\d{15}|\d{18}$"。
匹配中文字符的正则表达式： [\u4e00-\u9fa5]
匹配双字节字符(包括汉字在内)：[^\x00-\xff]
匹配空行的正则表达式：\n[\s| ]*\r
匹配首尾空格的正则表达式：(^\s*)|(\s*$)
匹配Email地址的正则表达式：\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*
匹配网址URL的正则表达式：http://([\w-]+\.)+[\w-]+(/[\w- ./?%&=]*)?
*/

public class AndyValidateUtil {

	/** 电话号 **/
	private static final String REGEX_MOBILE = "^((\\d3)|(\\d{3}\\-))?13[0-9]\\d{8}|15[89]\\d{8}|18\\d{9}";
	/** 邮箱 **/
	private static final String REGEX_EMAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
	/** 身份证 **/
	private static final String REGEX_ID_CARD = "^\\d{15}|\\d{18}$";
	/** URL地址 **/
	private static final String REGEX_URL = "^(http|https)+://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";
	/** 密码 以字母开头，长度在6~18之间，只能包含字符、数字和下划线**/
	private static final String REGEX_PASSWORD = "^[a-zA-Z]\\w{5,17}$";
	
	/**
	 * 电话号验证
	 * */
	public static boolean validateMobileNumber(String mobileNumber) {
		return validateRegex(REGEX_MOBILE, mobileNumber);
	}
	
	/**
	 * 邮箱验证
	 * */
	public static boolean validateEmail(String email) {
		return validateRegex(REGEX_EMAIL, email);
	}
	
	/**
	 * 身份证验证
	 * */
	public static boolean validateIDCard(String idCardString) {
		return validateRegex(REGEX_ID_CARD, idCardString);
	}
	
	/**
	 * 密码验证 以字母开头，长度在6~18之间，只能包含字符、数字和下划线
	 * */
	public static boolean validatePassword(String password) {
		return validateRegex(REGEX_PASSWORD, password);
	}
	
	/**
	 * URL验证
	 * */
	public static boolean validateURL(String rulString) {
		return validateRegex(REGEX_URL, rulString);
	}
	
	/**
	 * 正则验证方法
	 * @param regex 正则表达式
	 * @param srcString 验证的字符串
	 * */
	public static boolean validateRegex(String regex, String srcString) {
		if (regex == null) {
			return true;
		}
		
		if (srcString == null) {
			return false;
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(srcString);
		return matcher.matches();
	}
}

