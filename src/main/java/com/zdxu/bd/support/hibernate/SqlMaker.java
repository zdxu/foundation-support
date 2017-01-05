/**	
 * <br>
 * Copyright 2011 IFlyTek. All rights reserved.<br>
 * <br>			 
 * Package: com.iflytek.uaac.base.utils <br>
 * FileName: SqlMaker.java <br>
 * <br>
 * @version
 * @author ycli7
 * @created 2014年9月4日
 * @last Modified 
 * @history
 */

package com.zdxu.bd.support.hibernate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * SQL构造工具
 * 
 * @author ycli7
 * @lastModified
 * @history
 */

public class SqlMaker {

	/*** 分隔符号--百分号 ***/
	private static final String SPLIT_FLAG_BFH = "%";

	/**
	 * sql方式的模糊查询
	 * 
	 * @param value
	 *            存放参数的map
	 * @param column
	 *            表列名
	 * @param paramValue
	 *            对应的值
	 * @return 拼接好的sql语句
	 * @author ycli7
	 * @created 2014年9月4日 上午11:13:45
	 * @lastModified
	 * @history
	 */
	public static String popuSqlLike(Map<String, Object> value,
			final String column, final String paramValue) {
		if (StringUtil.isNotBlank(paramValue)) {
			StringBuilder sql = new StringBuilder(200);
			sql.append(" and ").append(column).append(" like :").append(column);
			value.put(column, SPLIT_FLAG_BFH + paramValue.trim()
					+ SPLIT_FLAG_BFH);
			return sql.toString();
		} else {
			return "";
		}
	}

	/**
	 * 
	 *  ｛sql方式的模糊查询not｝
	 *  @param value
	 *  @param column
	 *  @param paramValue
	 *  @return
	 *  @author jczhuo
	 *  @created 2015-5-6 上午11:29:34
	 *  @lastModified       
	 *  @history
	 */
	public static String popuSqlNotLike(Map<String, Object> value,
			final String column, final String paramValue) {
		if (StringUtil.isNotBlank(paramValue)) {
			StringBuilder sql = new StringBuilder(200);
			sql.append(" and ").append(column).append(" not like :").append(column);
			value.put(column, SPLIT_FLAG_BFH + paramValue.trim()
					+ SPLIT_FLAG_BFH);
			return sql.toString();
		} else {
			return "";
		}
	}
	
	/**
	 * sql方式的等值查询
	 * 
	 * @param value
	 *            存放参数的map
	 * @param column
	 *            表列名
	 * @param paramValue
	 *            对应的值
	 * @return 拼接好的sql语句
	 * @author ycli7
	 * @created 2014年9月4日 上午11:13:45
	 * @lastModified
	 * @history
	 */
	public static String popuSqlEq(Map<String, Object> value,
			final String column, final String paramValue) {
		if (StringUtil.isNotBlank(paramValue)) {
			StringBuilder sql = new StringBuilder(200);
			sql.append(" and ").append(column).append("=:").append(column);
			value.put(column, paramValue.trim());
			return sql.toString();
		} else {
			return "";
		}
	}
	
	/**
	 * sql方式的IN查询
	 * 
	 * @param value
	 *            存放参数的map
	 * @param column
	 *            表列名
	 * @param paramValue
	 *            对应的值
	 * @return 拼接好的sql语句
	 * @lastModified
	 * @history
	 */
	public static String popuSqlIn(Map<String, Object> value,
			final String column, final String paramValue) {
		if (StringUtil.isNotBlank(paramValue)) {
			StringBuilder sql = new StringBuilder(200);
			String cl[]=column.split("[.]");
			String list=cl[1]+"List";
			sql.append(" and ").append(column).append("  in (:").append(list).append(" )");
			value.put(list, paramValue.split(","));
			return sql.toString();
		} else {
			return "";
		}
	}
	/**
	 * 
	 *  ｛sql方式的IN查询ForHql｝
	 *  @param column
	 *  @param paramValue
	 *  @return
	 *  @author jczhuo
	 *  @created 2015-5-7 下午04:46:10
	 *  @lastModified       
	 *  @history
	 */
	public static String popuSqlInForHql(final String column, final String paramValue) {
		if (StringUtil.isNotBlank(paramValue)) {
			StringBuilder sql = new StringBuilder(200);
			String cl[]=column.split("[.]");
			String list=cl[1]+"List";
			sql.append(" and ").append(column).append("  in (:").append(list).append(" )");
//			value.put(list, paramValue.split(","));
			return sql.toString();
		} else {
			return "";
		}
	}
	/**
	 * sql方式的右模糊查询 ｛说明该函数的含义和作用，如果函数较为复杂，请详细说明｝
	 * 
	 * @param value
	 * @param column
	 * @param paramValue
	 * @return
	 * @author sbwang2@iflytek.com
	 * @created 2014年12月23日 上午9:43:34
	 * @lastModified
	 * @history
	 */
	public static String popuSqlRLike(Map<String, Object> value,
			final String column, final String paramValue) {
		if (StringUtil.isNotBlank(paramValue)) {
			StringBuilder sql = new StringBuilder(200);
			sql.append(" and ").append(column).append(" like :").append(column);
			value.put(column, paramValue.trim() + SPLIT_FLAG_BFH);
			return sql.toString();
		} else {
			return "";
		}
	}

	/**
	 * sql方式的左模糊查询 ｛说明该函数的含义和作用，如果函数较为复杂，请详细说明｝
	 * 
	 * @param value
	 * @param column
	 * @param paramValue
	 * @return
	 * @author sbwang2@iflytek.com
	 * @created 2014年12月23日 上午9:44:27
	 * @lastModified
	 * @history
	 */
	public static String popuSqlLLike(Map<String, Object> value,
			final String column, final String paramValue) {
		if (StringUtil.isNotBlank(paramValue)) {
			StringBuilder sql = new StringBuilder(200);
			sql.append(" and ").append(column).append(" like :").append(column);
			value.put(column, SPLIT_FLAG_BFH + paramValue.trim());
			return sql.toString();
		} else {
			return "";
		}
	}

	/**
	 * sql大于等于查询 ｛说明该函数的含义和作用，如果函数较为复杂，请详细说明｝
	 * 
	 * @param value
	 * @param column
	 * @param paramValue
	 * @return
	 * @author sbwang2@iflytek.com
	 * @created 2014年12月23日 上午9:46:43
	 * @lastModified
	 * @history
	 */
	public static String popuSqlGreaterEq(Map<String, Object> value,
			final String column, final String paramValue) {
		if (StringUtil.isNotBlank(paramValue)) {
			StringBuilder sql = new StringBuilder(200);
			sql.append(" and ").append(column).append(">=:")
					.append(column + "geq");
			value.put(column + "geq", paramValue.trim());
			return sql.toString();
		} else {
			return "";
		}
	}

	/**
	 * sql小于等于查询 ｛说明该函数的含义和作用，如果函数较为复杂，请详细说明｝
	 * 
	 * @param value
	 * @param column
	 * @param paramValue
	 * @return
	 * @author sbwang2@iflytek.com
	 * @created 2014年12月23日 上午9:48:19
	 * @lastModified
	 * @history
	 */
	public static String popuSqlLessEq(Map<String, Object> value,
			final String column, final String paramValue) {
		if (StringUtil.isNotBlank(paramValue)) {
			StringBuilder sql = new StringBuilder(200);
			sql.append(" and ").append(column).append("<=:")
					.append(column + "leq");
			value.put(column + "leq", paramValue.trim());
			return sql.toString();
		} else {
			return "";
		}
	}

	/**
	 *  获取Oracle的分页sql.
	 *  @param sql 主查询sql
	 *  @param firstResult 开始位置
	 *  @param maxResults 条数
	 *  @return
	 *  @author hjzhu
	 *  @created 2015年1月15日 上午9:20:51
	 *  @lastModified       
	 *  @history           
	 */
	public static String popuPagingSql4Oracle(String sql, long firstResult, long maxResults) {
		long endIndex = maxResults;
		sql = sql.trim();
		boolean isForUpdate = false;
		if (sql.toLowerCase().endsWith(" for update")) {
			sql = sql.substring(0, sql.length() - 11);
			isForUpdate = true;
		}

		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 );
		if (firstResult>0) {
			endIndex += firstResult;
			pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
		}
		else {
			pagingSelect.append("select * from ( ");
		}
		pagingSelect.append(sql);
		if (firstResult>0) {
			pagingSelect.append(" ) row_ where rownum <= "+endIndex+") where rownum_ > "+firstResult);
		}
		else {
			pagingSelect.append(" ) where rownum <= "+endIndex);
		}

		if ( isForUpdate ) {
			pagingSelect.append( " for update" );
		}
		return pagingSelect.toString();
	}
	
	/**
	 *  获取Date型的开始日期SQL.
	 *  <p>
	 *  24小时制
	 *  </p>
	 *  @param column 列名（tableName.columnName）
	 *  @param date 条件日期
	 *  @return
	 *  @author hjzhu
	 *  @created 2015年1月15日 下午1:28:34
	 *  @lastModified       
	 *  @history           
	 */
	public static String popuBeginDateSql(String column, Date date){
		return " and "+column+" >= to_date('"+new SimpleDateFormat("YYYY-MM-DD").format(date)+" 00:00:00','YYYY-MM-DD HH24:MI:SS')";
	}
	
	/**
	 *  获取Date型的结束日期SQL.
	 *  <p>
	 *  24小时制
	 *  </p>
	 *  @param column 列名（tableName.columnName）
	 *  @param date 条件日期
	 *  @return
	 *  @author hjzhu
	 *  @created 2015年1月15日 下午1:28:34
	 *  @lastModified       
	 *  @history           
	 */
	public static String popuEndDateSql(String column, Date date){
		return " and "+column+" <= to_date('"+new SimpleDateFormat("YYYY-MM-DD").format(date)+" 23:59:59','YYYY-MM-DD HH24:MI:SS')";
	}
	
	/**
	 *  获取字符串型的开始日期SQL.
	 *  <p>
	 *  仅支持24小时制
	 *  </p>
	 *  @param column 列名（tableName.columnName）
	 *  @param date 条件日期
	 *  @param format 日期格式
	 *  @return
	 *  @author hjzhu
	 *  @created 2015年1月15日 下午1:28:34
	 *  @lastModified       
	 *  @history           
	 */
	public static String popuBeginStringSql(String column, Date date, String format){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return " and "+column+" >= '"+new SimpleDateFormat(format).format(new Date(cal.getTimeInMillis()))+"'";
	}
	
	/**
	 *  获取字符串型的结束日期SQL.
	 *  <p>
	 *  仅支持24小时制
	 *  </p>
	 *  @param column 列名（tableName.columnName）
	 *  @param date 条件日期
	 *  @param format 日期格式
	 *  @return
	 *  @author hjzhu
	 *  @created 2015年1月15日 下午1:28:34
	 *  @lastModified       
	 *  @history           
	 */
	public static String popuEndStringSql(String column, Date date, String format){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return " and "+column+" <= '"+new SimpleDateFormat(format).format(new Date(cal.getTimeInMillis()))+"'";
	}
	
	/**
	 *  获取字符串型的开始日期SQL.
	 *  <p>
	 *  仅支持24小时制
	 *  </p>
	 *  @param column 列名（tableName.columnName）
	 *  @param dateStr 条件日期字符串
	 *  @param dateFormat 条件日期格式
	 *  @param format 日期格式
	 *  @return
	 *  @author hjzhu
	 *  @created 2015年1月15日 下午1:28:34
	 *  @lastModified       
	 *  @history           
	 */
	public static String popuBeginStringSql(String column, String dateStr, String dateFormat, String format){
		try {
			Date date = new SimpleDateFormat(dateFormat).parse(dateStr);
			return popuBeginStringSql(column, date, format);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	/**
	 *  获取字符串型的结束日期SQL.
	 *  <p>
	 *  仅支持24小时制
	 *  </p>
	 *  @param column 列名（tableName.columnName）
	 *  @param dateStr 条件日期字符串
	 *  @param dateFormat 条件日期格式
	 *  @param format 日期格式
	 *  @return
	 *  @author hjzhu
	 *  @created 2015年1月15日 下午1:28:34
	 *  @lastModified       
	 *  @history           
	 */
	public static String popuEndStringSql(String column, String dateStr, String dateFormat, String format){
		try {
			Date date = new SimpleDateFormat(dateFormat).parse(dateStr);
			return popuEndStringSql(column, date, format);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	/**
	 * 
	 *  ｛转换开始时间格式，由2015-01-01格式转换为20150101000000｝
	 *  @param data
	 *  @return
	 *  @author jczhuo
	 *  @created 2015-4-22 下午08:30:59
	 *  @lastModified       
	 *  @history
	 */
	public static String beginDate(String data){
		StringBuffer newString = new StringBuffer();
		newString.append(data.replace("-", ""));
		newString.append("0000");
		return newString.toString();
	}
	/**
	 * 
	 *  ｛转换截至时间格式，由原来2015-01-01转换成20150101235959｝
	 *  @param data
	 *  @return
	 *  @author jczhuo
	 *  @created 2015-4-22 下午08:33:01
	 *  @lastModified       
	 *  @history
	 */
	public static String endDate(String data){
		StringBuffer newString = new StringBuffer();
		newString.append(data.replace("-", ""));
		newString.append("235959");
		return newString.toString();
	}
	
	/**
	 * sql方式的开始时间查询
	 * 
	 * @param value
	 *            存放参数的map
	 * @param column
	 *            表列名
	 * @param paramValue
	 *            对应的值
	 * @return 拼接好的sql语句
	 * @author ycli7
	 * @created 2014年9月4日 上午11:13:45
	 * @lastModified
	 * @history
	 */
	public static String popuSqlBeginDate(Map<String, Object> value,
			final String column, final String paramValue) {
		if (StringUtil.isNotBlank(paramValue)) {
			StringBuilder sql = new StringBuilder(200);
			sql.append(" and ").append(column).append(">=:").append(column+"begin");
			value.put(column+"begin", beginDate(paramValue));
			return sql.toString();
		} else {
			return "";
		}
	}
	
	/**
	 * sql方式的结束 时间查询
	 * 
	 * @param value
	 *            存放参数的map
	 * @param column
	 *            表列名
	 * @param paramValue
	 *            对应的值
	 * @return 拼接好的sql语句
	 * @author ycli7
	 * @created 2014年9月4日 上午11:13:45
	 * @lastModified
	 * @history
	 */
	public static String popuSqlEndDate(Map<String, Object> value,
			final String column, final String paramValue) {
		if (StringUtil.isNotBlank(paramValue)) {
			StringBuilder sql = new StringBuilder(200);
			sql.append(" and ").append(column).append("<=:").append(column+"end");
			value.put(column+"end", endDate(paramValue));
			return sql.toString();
		} else {
			return "";
		}
	}
}
