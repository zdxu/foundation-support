/**	
 * <br>
 * Copyright 2011 IFlyTek. All rights reserved.<br>
 * <br>			 
 * Package: com.iflytek.ywq.sqcj.base <br>
 * FileName: SqlTemplateHelper.java <br>
 * <br>
 * @version
 * @author hjzhu
 * @created 2015年1月27日
 * @last Modified 
 * @history
 */

package com.zdxu.bd.support.hibernate;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL模板处理工具类.
 * 
 * @author hjzhu
 * @lastModified
 * @history
 */

public class SqlTemplateHelper {
	/**
	 * 枚举中方法名
	 */
	private static final String GET_INDEX = "getIndex";
	/**
	 * 枚举中方法名
	 */
	private static final String GET_CODE = "getCode";

	/**
	 * 使用提供的参数按顺序替换sql模板中的占位符.
	 * 
	 * @param sqlTemplate
	 *            sql模板
	 * @param args
	 *            用于替换占位符的参数
	 * @return
	 * @author hjzhu
	 * @created 2015年1月27日 上午9:18:07
	 * @lastModified
	 * @history
	 */
	public static String replaceSql(String sqlTemplate, Object... args) {
		Set<String> placeholders = getPlaceholders(sqlTemplate);
		if (args == null || args.length < placeholders.size()) {
			throw new RuntimeException("传入参数与占位符数量不匹配！");
		}
		Iterator<String> iter = placeholders.iterator();
		// 只有一个数组参数
		if (placeholders.size() == 1 && args.length > 1) {
			return sqlTemplate.replace(iter.next(), convertToStr(args));
		}
		int index = 0;
		String placeholder = null;
		while (iter.hasNext()) {
			placeholder = iter.next();
			// 参数为null时，忽略该占位符（可能存在必须保留该占位符字符串的情况）
			if (args[index] == null) {
				index++;
				continue;
			}
			if (args[index].getClass().isArray()) {
				// 数组参数
				sqlTemplate = sqlTemplate.replace(placeholder,
						convertToStr(args[index]));
			} else {
				sqlTemplate = sqlTemplate.replace(placeholder,
						args[index].toString());
			}
			index++;
		}
		return sqlTemplate;
	}

	/**
	 * 使用提供的参数按顺序替换sql模板中的占位符.
	 * 
	 * @param sqlTemplate
	 *            sql模板
	 * @param args
	 *            用于替换占位符的对象参数
	 * @return
	 * @author hjzhu
	 * @created 2015年1月27日 上午9:18:07
	 * @lastModified
	 * @history
	 */
	public static String replaceSqlUsingObject(String sqlTemplate,
			Object... args) {
		Set<String> placeholders = getPlaceholders(sqlTemplate);
		if (args == null || args.length == 0) {
			return sqlTemplate;
		}
		Iterator<String> iter = placeholders.iterator();
		String placeholder = null;
		Object replacement = null;
		while (iter.hasNext()) {
			placeholder = iter.next();
			for (Object obj : args) {
				replacement = valueOf(obj, getMethodName(placeholder));
				// 参数都为null时，忽略该占位符（可能存在必须保留该占位符字符串的情况）
				if (replacement == null) {
					continue;
				}
				// 数组参数
				if (replacement.getClass().isArray()) {
					sqlTemplate = sqlTemplate.replace(placeholder,
							convertToStr(replacement));
				} else {
					sqlTemplate = sqlTemplate.replace(placeholder,
							replacement.toString());
				}
			}
		}
		return sqlTemplate;
	}

	/**
	 * 使用提供的枚举参数，按枚举顺序替换sql模板中的占位符.
	 * <p>
	 * 1. 枚举必须具有GET_INDEX方法和GET_CODE方法
	 * <p>
	 * 2.占位符格式要求：${枚举的index}，大小写敏感.
	 * <p>
	 * 3.当提供多个枚举参数时，按枚举顺序替换，后面的不会覆盖前面的.
	 * <p>
	 * 4.当没有找到对应的get方法或get方法返回值为null时，将忽略该占位符.
	 * 
	 * @param sqlTemplate
	 *            sql模板
	 * @param cls
	 *            枚举class
	 * @return
	 * @author hjzhu
	 * @created 2015年1月27日 上午11:03:23
	 * @lastModified
	 * @history
	 */
	public static String replaceSqlUsingEnum(String sqlTemplate,
			Class<?>... cls) {
		if (cls != null && cls.length > 0) {
			Object[] values = null;
			Object index = null;
			Object code = null;
			for (Class<?> c : cls) {
				if (c != null && c.isEnum()) {
					values = c.getEnumConstants();
					for (Object obj : values) {
						index = valueOf(obj, GET_INDEX);
						if (index == null) {
							continue;
						}
						code = valueOf(obj, GET_CODE);
						if (code == null) {
							continue;
						}
						sqlTemplate = sqlTemplate.replace(
								"${" + index.toString() + "}", code.toString());
					}
				}
			}
		}
		return sqlTemplate;
	}

	/**
	 * 从占位符中获取get方法名.
	 * 
	 * @param placeholder
	 *            占位符
	 * @return
	 * @author hjzhu
	 * @created 2015年1月27日 上午9:37:39
	 * @lastModified
	 * @history
	 */
	private static String getMethodName(String placeholder) {
		String regex = "\\$\\{(:?\\w+)\\}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(placeholder);
		if (matcher.find()) {
			String name = matcher.group(1);
			return "get" + name.substring(0, 1).toUpperCase()
					+ name.substring(1);
		}
		throw new RuntimeException("该字符串不符合占位符格式要求！");
	}

	/**
	 * 获取sql模板中的占位符列表.
	 * <p>
	 * 1.占位符格式要求：${NAME}.
	 * <p>
	 * 2.当同一个占位符出现多次时，按第一次出现的索引计算，其后出现的不占索引.
	 * 
	 * @param sqlTemplate
	 *            sql模板
	 * @return
	 * @author hjzhu
	 * @created 2015年1月26日 下午9:34:33
	 * @lastModified
	 * @history
	 */
	private static Set<String> getPlaceholders(String sqlTemplate) {
		Set<String> placeholders = new LinkedHashSet<String>();
		String regex = "\\$\\{:?\\w+\\}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(sqlTemplate);
		while (matcher.find()) {
			placeholders.add(matcher.group());
		}
		return placeholders;
	}

	/**
	 * 转换为逗号分隔的字符串.
	 * <p>
	 * 若第一个值为String型，则所有值都加上引号。
	 * </p>
	 * 
	 * @param params
	 *            参数数组
	 * @return
	 * @author hjzhu
	 * @created 2015年1月23日 下午7:39:53
	 * @lastModified
	 * @history
	 */
	private static String convertToStr(Object... args) {
		if (args == null || args.length == 0) {
			return "-1";
		}
		Object[] params = args;
		if (args.length == 1 && args[0].getClass().isArray()) {
			params = (Object[]) args[0];
		}
		if (params == null || params.length == 0) {
			return "-1";
		}
		StringBuffer buffer = new StringBuffer();
		boolean isString = (params[0] instanceof String);
		for (Object param : params) {
			if (isString) {
				param = "'" + param + "'";
			}
			buffer.append(param).append(",");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		return buffer.toString();
	}

	/**
	 * 调用obj的methodName方法，获取其返回值.
	 * 
	 * @param obj
	 *            Object对象
	 * @param methodName
	 *            方法名
	 * @return
	 * @author hjzhu
	 * @created 2014年12月25日 上午11:28:34
	 * @lastModified
	 * @history
	 */
	public static Object valueOf(Object obj, String methodName) {
		Method method = null;
		try {
			method = obj.getClass().getMethod(methodName);
			return method.invoke(obj);
		} catch (Exception e) {
			return null;
		}
	}
}
