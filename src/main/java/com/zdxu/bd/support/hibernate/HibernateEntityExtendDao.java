/**	
 * <br>
 * Copyright 2011 IFlyTek. All rights reserved.<br>
 * <br>			 
 * Package: com.iflytek.uaac.manager.persistence <br>
 * FileName: HibernateEntityExtendDao.java <br>
 * <br>
 * @version
 * @author xkfeng@iflytek.com
 * @created 2014-9-3
 * @last Modified 
 * @history
 */

package com.zdxu.bd.support.hibernate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.util.Assert;

import com.zdxu.bd.support.orm.Page;

/**
 * 扩展HibernateEntityDao
 *  
 *  @author xkfeng@iflytek.com
 *  @lastModified       
 *  @history           
 */

public class HibernateEntityExtendDao<T> extends HibernateEntityDao<T> {
	
	/**
	 * 按sql进行分页查询.
	 * @param <T> 
	 * @param page 分页参数.不支持其中的orderBy参数.
	 * @param sql sq语句.
	 * @param cl maping对象.
	 * @param values 数量可变的查询参数,按顺序绑定.
	 * @return 分页查询结果, 附带结果列表及所有查询时的参数.
	 */
	@SuppressWarnings({ "unchecked", "hiding", "rawtypes" })
	public <T> Page <T> querySqlPage(final Page<T> page, final String sql, final Class cl, final Object... values) {
		Assert.notNull(page, "page不能为空"); 
		SQLQuery q = createSqlQuery(sql, values);
		if (page.isAutoCount()) {
			long totalCount = this.countSqlResult(sql, values);
			page.setTotalCount(totalCount);
		} 
		setSqlPageParameter(q, page);
		List result = q.setResultTransformer(ResultTransformers.aliasToBean(cl)).list();
		page.setResult(result);
		return page;
	}
	
	/**
	 * 按sql进行分页查询.
	 * @param <T> 
	 * @param page 分页参数.不支持其中的orderBy参数.
	 * @param sql sq语句.
	 * @param cl maping对象.
	 * @param values 数量可变的查询参数,按名称绑定.
	 * @return 分页查询结果, 附带结果列表及所有查询时的参数.
	 */
	@SuppressWarnings({ "unchecked", "hiding", "rawtypes" })
	public <T> Page <T> querySqlPage(final Page<T> page, final String sql, final Class cl,final Map<String, Object> values) {
		Assert.notNull(page, "page不能为空"); 
		SQLQuery q = createSqlQuery(sql, values);
		if (page.isAutoCount()) {
			long totalCount = countSqlResult(sql, values);
			page.setTotalCount(totalCount);
		} 
		setSqlPageParameter(q, page);
		List result = q.setResultTransformer(ResultTransformers.aliasToBean(cl)).list();
		page.setResult(result);
		return page;
	}
	
	/**
	 * 按sql进行分页查询.
	 * @param <T> 
	 * @param page 分页参数.不支持其中的orderBy参数.
	 * @param sql sq语句.
	 * @param cl maping对象.
	 * @param values 数量可变的查询参数,按名称绑定.
	 * @return 分页查询结果, 附带结果列表及所有查询时的参数.
	 */
	@SuppressWarnings({ "unchecked", "hiding", "rawtypes" })
	public <T> Page <T> querySqlPages(final Page<T> page, final String sql, final Class cl,final Map<String, Object> values) {
		Assert.notNull(page, "page不能为空"); 
		SQLQuery q = createSqlQuery(sql, values);
		if (page.isAutoCount()) {
			long totalCount = countSqlResults(sql, values);
			page.setTotalCount(totalCount);
		} 
		setSqlPageParameter(q, page);
		List result = q.setResultTransformer(ResultTransformers.aliasToBean(cl)).list();
		
		Transformers.aliasToBean(cl);
		
		page.setResult(result);
		return page;
	}
	/**
	 * 根据查询SQL与参数列表创建Query对象.
	 * 
	 * 本类封装的find()函数全部默认返回对象类型为T,当不为T时使用本函数.
	 * @param queryString 
	 * @param values 数量可变的参数,按名称绑定.
	 * @return SQLQuery 
	 */
	public SQLQuery createSqlQuery(final String queryString,final Map<String, Object> values) {
		Assert.hasText(queryString, "queryString不能为空");
		SQLQuery query = getSession().createSQLQuery(queryString);
//		if (values != null) {
//			for (int i = 0; i < values.length; i++) {
//				query.setParameter(i, values[i]);
//			}
//		}
		if (values != null) {
			query.setProperties(values);
		}
		return query;
	}
	
	/**
	 * 执行count查询获得本次sql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的sql语句,复杂的sql查询请另行编写count语句查询.
	 * @param sql 
	 * @param values 数量可变的参数,按名称绑定.
	 * @return long
	 */
	public long countSqlResult(final String sql,final Map<String, Object> values) {
		Long count = 0L;
		String fromHql = sql;
		//select子句与order by子句会影响count查询,进行简单的排除.
		fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "order by");

		String countHql = "select count(*) " + fromHql;

		try {
		 count = ((BigDecimal) findSqlUnique(countHql, values)).longValue();
		} catch (RuntimeException e) {
			throw new RuntimeException("hql can't be auto count, sql is:" + countHql, e);
		}
		return count;
	}
	
	/**
	 * 执行count查询获得本次sql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的sql语句,复杂的sql查询请另行编写count语句查询.
	 * @param sql 
	 * @param values 数量可变的参数,按名称绑定.
	 * @return long
	 */
	public long countSqlResult(final String sql,final Object... values) {
		Long count = 0L;
		String fromHql = sql;
		//select子句与order by子句会影响count查询,进行简单的排除,考虑可能包含group by.
		fromHql = "select 1 from " + StringUtils.substringAfter(fromHql, "from");
		//fromHql = StringUtils.substringBefore(fromHql, "order by");

		String countHql = "select count(1) from(" + fromHql+")";

		try {
		 count = ((BigDecimal) findSqlUnique(countHql, values)).longValue();
		} catch (RuntimeException e) {
			throw new RuntimeException("hql can't be auto count, sql is:" + countHql, e);
		}
		return count;
	}
	
	/**
	 * 执行count查询获得本次sql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的sql语句,复杂的sql查询请另行编写count语句查询.
	 * @param sql 
	 * @param values 数量可变的参数,按名称绑定.
	 * @return long
	 */
	public long countSqlResults(final String sql,final Map<String, Object> values) {
		Long count = 0L;
		String fromHql = sql;
		//select子句与order by子句会影响count查询,进行简单的排除,考虑可能包含group by.
		fromHql = "select 1 from " + StringUtils.substringAfter(fromHql, "from");
		//fromHql = StringUtils.substringBefore(fromHql, "order by");

		String countHql = "select count(1) from(" + fromHql+")";

		try {
		 count = ((BigDecimal) findSqlUnique(countHql, values)).longValue();
		} catch (RuntimeException e) {
			throw new RuntimeException("hql can't be auto count, sql is:" + countHql, e);
		}
		return count;
	}
	
	/**
	 * 按SQL查询唯一对象.
	 * 
	 * 
	 * @param <X> 
	 * @param sql 
	 * @param values 数量可变的参数,按名称绑定.
	 * @return <X>
	 */
	@SuppressWarnings("unchecked")
	public <X> X findSqlUnique(final String sql,final Map<String, Object> values) {
		return (X) createSqlQuery(sql, values).uniqueResult();
	}
	
	/**
	 * 按sql进行分页查询.
	 * @param <T> 
	 * @param page 分页参数.不支持其中的orderBy参数.
	 * @param sql sq语句.
	 * @param cl maping对象.
	 * @param values 数量可变的查询参数,按顺序绑定.
	 * @return 分页查询结果, 附带结果列表及所有查询时的参数.
	 */
	@SuppressWarnings({ "unchecked", "hiding", "rawtypes" })
	public <T> Page <T> querySqlPage(final Page<T> page, final String sql, final Class cl, final String tsql,final Map<String, Object> values) {
		Assert.notNull(page, "page不能为空"); 
		SQLQuery q = createSqlQuery(sql,values);
		if (page.isAutoCount()) {
			long totalCount = this.countSqlResult(tsql,values);
			page.setTotalCount(totalCount);
		} 
		setSqlPageParameter(q, page);
		List result = q.setResultTransformer(ResultTransformers.aliasToBean(cl)).list();
		page.setResult(result);
		return page;
	}
}




