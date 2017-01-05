/**	
 * <br>
 * Copyright 2013 IFlyTek. All rights reserved.<br>
 * <br>			 
 * Package: com.iflytek.support.jdbc.utils <br>
 * FileName: AliasUtils.java <br>
 * <br>
 * @version
 * @author xkfeng@iflytek.com
 * @created 2013-11-13
 * @last Modified 
 * @history
 */

package com.zdxu.bd.support.hibernate;

import org.apache.commons.lang3.StringUtils;

/**
 * AliasUtils
 * 
 * @author xkfeng@iflytek.com
 */

public class AliasUtils {

	/**
	 * 
	 * 数据库字段转换java Field,如：COLUMN_NAME -> columnName
	 * 
	 * @param columnName
	 *            数据库字段名
	 * @return java Field name
	 * @author xkfeng@iflytek.com
	 * @created 2013-8-20 下午03:23:09
	 * @lastModified
	 * @history
	 */
	public static String getFieldName(String columnName) {
		if (columnName.indexOf("_") == -1) {
			if (StringUtils.equals(columnName.toUpperCase(), columnName)) {
				return columnName.toLowerCase();
			}
			return columnName;
		}
		String[] columnNames = columnName.split("_");
		StringBuilder fieldName = new StringBuilder();
		for (String string : columnNames) {
			if (StringUtils.isNotBlank(string)) {
				if (StringUtils.isBlank(fieldName.toString())) {
					fieldName.append(StringUtils.lowerCase(string));
				} else {
					fieldName.append(StringUtils.upperCase(string.substring(0,
							1)) + StringUtils.lowerCase(string.substring(1)));
				}
			}
		}
		return fieldName.toString();
	}

}
