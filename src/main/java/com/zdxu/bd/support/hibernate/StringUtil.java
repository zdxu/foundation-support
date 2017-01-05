/**	
 * <br>
 * Copyright 2011 IFlyTek. All rights reserved.<br>
 * <br>			 
 * Package: com.iflytek.utils <br>
 * FileName: StringUtils.java <br>
 * <br>
 * @version
 * @author sbwang@iflytek.com
 * @created 2013-5-29
 * @last Modified 
 * @history
 */

package com.zdxu.bd.support.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符处理类
 * 
 * @author sbwang@iflytek.com
 * @lastModified
 * @history
 */

public class StringUtil extends StringUtils {

	/**
	 * 
	 * 替换非法字符
	 * 
	 * @param str
	 * @return
	 * @author sbwang@iflytek.com
	 * @created 2013-5-29 下午09:49:10
	 * @lastModified
	 * @history
	 */
	public static String replace(String str) {
		if (StringUtils.isNotEmpty(str)) {
			/*
			 * str = str.replace("'", "''").replace("]", "]]").replace("&",
			 * "chr(38)").replace("%", "chr(37)").replace("\\", "chr(92)")
			 * .replace("\"", "chr(34)").replace("_", "chr(95)");
			 */
			str = str.replace("'", "''").replace("%", "\\%")
					.replace("\\", "\\\\").replace("_", "\\_");
		}
		return str;
	}

	/**
	 * 
	 * 去掉尾部全部特定字符串
	 * 
	 * @param str
	 * @param removeStr
	 * @return
	 * @author xkfeng@iflytek.com
	 * @created 2013-7-19 上午09:11:30
	 * @lastModified
	 * @history
	 */
	public static String removeEndStr(String str, String removeStr) {
		String str2 = StringUtils.removeEnd(str, removeStr);
		while (!str2.equals(str)) {
			str = str2;
			str2 = StringUtils.removeEnd(str, removeStr);
		}
		return str2;
	}

	/**
	 * 去零
	 * 
	 * @param code
	 *            原编码
	 * @param length
	 *            “0”的长度
	 * @return 去零后的编码
	 * @author jianye
	 * @created 2014-6-18 09:32:10
	 * @lastModified
	 * @history
	 */
	public static String toNewsCode(String code, int length) {
		if (StringUtils.isNotEmpty(code)) {
			boolean temp = false;
			code = code.replace("\r", "");
			code = code.replace("\n", "");
			String template = String.format("%0" + length + "d", 0);
			while (!temp) {
				if (code.length() > length) {
					String newstr = code.substring(code.length() - length);
					if (template.equals(newstr))
						code = code.substring(0, code.length() - length);
					else
						temp = true;
				} else {
					temp = true;
				}
			}
		}
		return code;
	}

	/**
	 * 根绝关键词数组和需要查询表的列名数组拼接查询的sql条件
	 * 
	 * @param value
	 *            存放参数的map
	 * @param searchKeys
	 *            关键词数组
	 * @param columNames
	 *            列名数组
	 * @return 查询条件的sql语句
	 * @author ycli7
	 * @created 2014年9月4日 上午9:14:16
	 * @lastModified
	 * @history
	 */
	public static String makeSearchSql(Map<String, Object> value,
			String[] searchKeys, String[] columNames) {
		if (searchKeys == null || columNames == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer(100);
		for (int index = 0; index < searchKeys.length; index++) {
			if (isBlank(searchKeys[index])) {
				continue;
			}
			sb.append(" and (");
			for (String col : columNames) {
				sb.append(col).append(" like :key").append(index)
						.append(" or ");
			}
			// 去除最后一个“or”，如果有的话
			int last = sb.lastIndexOf("or");
			if (last > 0) {
				sb.delete(last, last + 2);
			}
			sb.append(")");
			value.put("key" + index, "%" + searchKeys[index] + "%");
		}
		return sb.toString();
	}

	

	/**
	 * 
	 * 获取附件ID组成的字符串数组
	 * 
	 * @param fjidstr
	 * @return
	 * @author hypan
	 * @created 2015年2月8日 下午1:56:27
	 * @lastModified
	 * @history
	 */
	public static String[] getIdArrayByStr(String fjidstr) {
		return StringUtils.split(fjidstr, ",");
	}

	/**
	 * 
	 * 判断两个List内容是否相等
	 * 
	 * @param srcList
	 * @param objList
	 * @return
	 * @author hypan
	 * @created 2015年2月28日 下午1:00:11
	 * @lastModified
	 * @history
	 */
	public static boolean isListEquals(List<String> srcList,
			List<String> objList) {
		if (isBlankList(srcList) && isBlankList(objList)) {

			return Boolean.TRUE;
		}
		if (isNotBlankList(srcList) && isNotBlankList(objList)) {
			return srcList.containsAll(objList) && objList.containsAll(srcList);
		}

		return false;
	}

	/**
	 * 
	 * 转换字符数组成为List
	 * 
	 * @param arrays
	 * @return
	 * @author hypan
	 * @created 2015年2月28日 下午1:45:09
	 * @lastModified
	 * @history
	 */
	public static List<String> getListByArrays(String[] arrays) {
		if (arrays == null) {
			return null;
		}
		return Arrays.asList(arrays);
	}

	/**
	 * 
	 * 判断LIST是否为空
	 * 
	 * @param list
	 * @return
	 * @author hypan
	 * @created 2015年2月28日 下午4:10:32
	 * @lastModified
	 * @history
	 */
	public static boolean isBlankList(List<?> list) {
		if (list == null) {
			return Boolean.TRUE;
		} else if (list.size() <= 0) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	/**
	 * 
	 * 判断LIST是否不为空
	 * 
	 * @param list
	 * @return
	 * @author hypan
	 * @created 2015年2月28日 下午4:10:32
	 * @lastModified
	 * @history
	 */
	public static boolean isNotBlankList(List<?> list) {
		if (list != null && list.size() > 0) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	/**
	 * 
	 *  判断list和数组中的内容是否相同
	 *  @param srcList
	 *  @param objArrays
	 *  @return
	 *  @author hypan
	 *  @created 2015年2月28日 下午4:41:33
	 *  @lastModified
	 *  @history
	 */
	public static boolean isListEqualsArrays(List<String> srcList,
			String[] objArrays) {
		return isListEquals(srcList,getListByArrays(objArrays));
		
	}
	
	/**
	 * 
	 *  将URLEncode转换的UTF-8过滤特殊字符
	 *  @param encodeStr
	 *  @return
	 *  @author hypan
	 *  @created 2015年3月11日 下午3:21:35
	 *  @lastModified
	 *  @history
	 */
	public static  String replaceToUTF8(String encodeStr){
		if(StringUtil.isBlank(encodeStr)){
			return null;
		}else{
			//过滤空格
			encodeStr=encodeStr.replace("+", "%20");
		}
		
		return encodeStr;
	}
	
	
    
	
	public static void main(String[] args) {
		System.out.println(StringUtils.split(null, ","));
	}

}
