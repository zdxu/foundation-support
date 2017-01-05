package com.zdxu.bd.support.hibernate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.transform.ResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.zdxu.bd.support.orm.Page;
import com.zdxu.bd.support.utils.ReflectionExtendUtils;

/**
 * Hibernate Dao的泛型基类.
 * <p/>
 * 继承于Spring的<code></code>,提供分页函数和若干便捷查询方法，并对返回值作了泛型类型转换.
 * 
 * @author zoey
 * @see
 * @see HibernateEntityDao
 */
@SuppressWarnings("unchecked")
public class HibernateGenericDao {

	/**
	 * 日志实体
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * session工厂
	 */
	@Autowired
	protected SessionFactory sessionFactory;

	public Session getSession() {
		try {
			return sessionFactory.getCurrentSession();
		} catch (Exception e) {
			return sessionFactory.openSession();
		}
	}

	public SessionFactory getSessionFactory() {
		return getSession().getSessionFactory();
	}

	/**
	 * 根据ID获取对象. 实际调用Hibernate的session.load()方法返回实体或其proxy对象. 如果对象不存在，抛出异常.
	 * 
	 * @param <T>
	 *            类
	 * @param entityClass
	 *            实体类
	 * @param id
	 *            序列化ID
	 * @return 类
	 */
	public <T> T get(Class<T> entityClass, Serializable id) {
		Assert.notNull(id, "id不能为空");
		return (T) getSession().load(entityClass, id);
		// return (T) getHibernateTemplate().load(entityClass, id);
	}

	/**
	 * 获取全部对象.
	 * 
	 * @param <T>
	 *            类
	 * @param entityClass
	 *            实体类
	 * @return 类
	 */
	public <T> List<T> getAll(Class<T> entityClass) {
		return find(entityClass);
		// return getHibernateTemplate().loadAll(entityClass);
	}

	/**
	 * 获取全部对象,带排序字段与升降序参数.
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param orderBy
	 * @param isAsc
	 * @return 类
	 */
	public <T> List<T> getAll(Class<T> entityClass, String orderBy,
			boolean isAsc) {
		Assert.hasText(orderBy);
		return find(entityClass, orderBy, isAsc);
	}

	/**
	 * 保存对象.
	 * 
	 * @param entity
	 */
	@Transactional
	public void save(Object entity) {
		Assert.notNull(entity, "entity不能为空");
		getSession().save(entity);
		logger.debug("save entity: {}", entity);
		// getHibernateTemplate().saveOrUpdate(o);
	}

	/**
	 * 保存对象.
	 * 
	 * @param entity
	 */
	@Transactional
	public void saveOrUpdate(Object entity) {
		Assert.notNull(entity, "entity不能为空");
		getSession().saveOrUpdate(entity);
		logger.debug("save entity: {}", entity);
		// getHibernateTemplate().saveOrUpdate(o);
	}

	/**
	 * 删除对象
	 * 
	 * @param entity
	 */
	@Transactional
	public void remove(Object entity) {
		Assert.notNull(entity, "entity不能为空");
		getSession().delete(entity);
		logger.debug("delete entity: {}", entity);
		// getHibernateTemplate().delete(o);
	}

	/**
	 * 根据ID删除对象.
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param id
	 */
	@Transactional
	public <T> void removeById(Class<T> entityClass, Serializable id) {
		Assert.notNull(id, "id不能为空");
		remove(get(entityClass, id));
		logger.debug("delete entity {},id is {}", entityClass.getSimpleName(),
				id);
		// remove(get(entityClass, id));
	}

	// ///////////////////////////////////////////////////
	// 查询操作
	// ///////////////////////////////////////////////////

	/**
	 * 根据属性名和属性值查询对象.匹配方式为相等.
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param propertyName
	 * @param value
	 * @return 符合条件的对象列表
	 */
	public <T> List<T> findBy(Class<T> entityClass, final String propertyName,
			final Object value) {
		Assert.hasText(propertyName, "propertyName不能为空");
		Criterion criterion = Restrictions.eq(propertyName, value);
		return find(entityClass, criterion);
	}

	/**
	 * 根据属性名和属性值查询对象.匹配方式为相等,带排序参数.
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param propertyName
	 * @param value
	 * @param orderBy
	 * @param isAsc
	 * @return 符合条件的对象列表
	 */
	public <T> List<T> findBy(Class<T> entityClass, String propertyName,
			Object value, String orderBy, boolean isAsc) {
		Assert.hasText(propertyName, "propertyName不能为空");
		Assert.hasText(orderBy);
		Criterion criterion = Restrictions.eq(propertyName, value);
		return this.find(entityClass, orderBy, isAsc, criterion);
	}

	/**
	 * 按HQL查询对象列表.
	 * 
	 * @param <X>
	 * @param hql
	 * @param values
	 *            数量可变的参数,按顺序绑定.
	 * @return 符合条件的对象列表
	 */
	public <X> List<X> find(final String hql, final Object... values) {
		return createQuery(hql, values).list();
	}

	/**
	 * 按HQL查询对象列表.
	 * 
	 * @param <X>
	 * @param hql
	 * @param values
	 *            数量可变的参数,按顺序绑定.
	 * @return 符合条件的对象列表
	 */
	public <X> List<X> find(final String hql, final Map<String, Object> values) {
		return createQuery(hql, values).list();
	}

	/**
	 * 执行HQL进行批量修改/删除操作.
	 * 
	 * @param hql
	 * @param values
	 * @return @Transactional
	 */
	@Transactional
	public int batchExecute(final String hql, final Object... values) {
		return createQuery(hql, values).executeUpdate();
	}

	/**
	 * 执行HQL进行批量修改/删除操作.
	 * 
	 * @param hql
	 * @param values
	 * @return 更新记录数.
	 */
	@Transactional
	public int batchExecute(final String hql, final Map<String, Object> values) {
		return createQuery(hql, values).executeUpdate();
	}

	// /////////////////////////////////////////////

	/**
	 * 按HQL分页查询.
	 * 
	 * @param <T>
	 * @param page
	 *            分页参数.不支持其中的orderBy参数.
	 * @param hql
	 *            hql语句.
	 * @param values
	 *            数量可变的查询参数,按顺序绑定.
	 * 
	 * @return 分页查询结果, 附带结果列表及所有查询时的参数.
	 */
	public <T> Page<T> findPage(final Page<T> page, final String hql,
			final Object... values) {
		Assert.notNull(page, "page不能为空");
		Query q = createQuery(hql, values);
		if (page.isAutoCount()) {
			long totalCount = countHqlResult(hql, values);
			page.setTotalCount(totalCount);
		}
		setPageParameter(q, page);
		@SuppressWarnings("rawtypes")
		List result = q.list();
		page.setResult(result);
		return page;
	}

	/**
	 * 按sql进行分页查询.
	 * 
	 * @param <T>
	 * @param page
	 *            分页参数.不支持其中的orderBy参数.
	 * @param sql
	 *            sq语句.
	 * @param cl
	 *            maping对象.
	 * @param values
	 *            数量可变的查询参数,按顺序绑定.
	 * @return 分页查询结果, 附带结果列表及所有查询时的参数.
	 */
	public <T> Page<T> findSqlPage(final Page<T> page, final String sql,
			@SuppressWarnings("rawtypes") final Class cl, final Object... values) {
		Assert.notNull(page, "page不能为空");
		SQLQuery q = createSqlQuery(sql, values);
		if (page.isAutoCount()) {
			long totalCount = countSqlResult(sql, values);
			page.setTotalCount(totalCount);
		}
		setSqlPageParameter(q, page);
		@SuppressWarnings("rawtypes")
		List result = q
				.setResultTransformer(ResultTransformers.aliasToBean(cl))
				.list();
		page.setResult(result);
		return page;
	}

	/**
	 * 
	 * @param <T>
	 * @param page
	 * @param sql
	 * @param cl
	 * @param values
	 * @return @Deprecated
	 */
	@Deprecated
	public <T> Page<T> findDistinctSqlPage(final Page<T> page,
			final String sql, @SuppressWarnings("rawtypes") final Class cl, final Object... values) {
		Assert.notNull(page, "page不能为空");
		SQLQuery q = createSqlQuery(sql, values);
		if (page.isAutoCount()) {
			long totalCount = countDistinctSqlResult(sql, values);
			page.setTotalCount(totalCount);
		}
		setSqlPageParameter(q, page);
		@SuppressWarnings("rawtypes")
		List result = q
				.setResultTransformer(ResultTransformers.aliasToBean(cl))
				.list();
		page.setResult(result);
		return page;
	}

	/**
	 * 设置分页参数到SqlQuery对象,辅助函数.
	 * 
	 * @param <T>
	 * @param q
	 * @param page
	 * @return <T>
	 */
	protected <T> Query setSqlPageParameter(final SQLQuery q, final Page<T> page) {
		// hibernate的firstResult的序号从0开始
		q.setFirstResult(page.getFirstOfPage() - 1);
		q.setMaxResults(page.getPageSize());
		return q;
	}

	/**
	 * 根据查询SQL与参数列表创建Query对象.
	 * 
	 * 本类封装的find()函数全部默认返回对象类型为T,当不为T时使用本函数.
	 * 
	 * @param queryString
	 * @param values
	 *            数量可变的参数,按顺序绑定.
	 * @return SQLQuery
	 */
	public SQLQuery createSqlQuery(final String queryString,
			final Object... values) {
		Assert.hasText(queryString, "queryString不能为空");
		SQLQuery query = getSession().createSQLQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}

	/**
	 * 执行count查询获得本次sql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的sql语句,复杂的sql查询请另行编写count语句查询.
	 * 
	 * @param sql
	 * @param values
	 * @return long
	 */
	public long countSqlResult(final String sql, final Object... values) {
		Long count = 0L;
		String fromHql = sql;
		// select子句与order by子句会影响count查询,进行简单的排除.
		fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "order by");

		String countHql = "select count(*) " + fromHql;

		try {
			count = ((BigDecimal) findSqlUnique(countHql, values)).longValue();
		} catch (RuntimeException e) {
			throw new RuntimeException("hql can't be auto count, sql is:"
					+ countHql, e);
		}
		return count;
	}

	/**
	 * 执行count查询获得本次sql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的sql语句,复杂的sql查询请另行编写count语句查询.
	 * 
	 * @param sql
	 * @param values
	 * @return long
	 */
	public long countDistinctSqlResult(final String sql, final Object... values) {
		Long count = 0L;
		String fromHql = sql;
		// select子句与order by子句会影响count查询,进行简单的排除.
		// fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
		// fromHql = StringUtils.substringBefore(fromHql, "order by");

		String countHql = "select count(*) from (" + fromHql + ")";

		try {
			count = ((BigDecimal) findSqlUnique(countHql, values)).longValue();
		} catch (RuntimeException e) {
			throw new RuntimeException("hql can't be auto count, sql is:"
					+ countHql, e);
		}
		return count;
	}

	/**
	 * 按SQL查询唯一对象.
	 * 
	 * 
	 * @param <X>
	 * @param sql
	 * @param values
	 *            数量可变的参数,按顺序绑定.
	 * @return <X>
	 */
	public <X> X findSqlUnique(final String sql, final Object... values) {
		return (X) createSqlQuery(sql, values).uniqueResult();
	}

	/**
	 * 按HQL分页查询.
	 * 
	 * @param <T>
	 * @param page
	 *            分页参数.
	 * @param hql
	 *            hql语句.
	 * @param values
	 *            命名参数,按名称绑定.
	 * 
	 * @return 分页查询结果, 附带结果列表及所有查询时的参数.
	 */
	public <T> Page<T> findPage(final Page<T> page, final String hql,
			final Map<String, Object> values) {
		Assert.notNull(page, "page不能为空");
		Query q = createQuery(hql, values);
		if (page.isAutoCount()) {
			long totalCount = countHqlResult(hql, values);
			page.setTotalCount(totalCount);
		}
		setPageParameter(q, page);
		@SuppressWarnings("rawtypes")
		List result = q.list();
		page.setResult(result);
		return page;
	}

	/**
	 * 按Criteria分页查询.
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param page
	 *            分页参数.
	 * @param criterions
	 *            数量可变的Criterion.
	 * 
	 * @return 分页查询结果.附带结果列表及所有查询时的参数.
	 */
	public <T> Page<T> findPage(Class<T> entityClass, final Page<T> page,
			final Criterion... criterions) {
		Assert.notNull(page, "page不能为空");
		Criteria c = createCriteria(entityClass, criterions);
		if (page.isAutoCount()) {
			int totalCount = countCriteriaResult(c);
			page.setTotalCount(totalCount);
		}
		setPageParameter(c, page);
		@SuppressWarnings("rawtypes")
		List result = c.list();
		page.setResult(result);
		return page;
	}

	/**
	 * 设置分页参数到Query对象,辅助函数.
	 * 
	 * @param <T>
	 * @param q
	 * @param page
	 * @return <T>
	 */
	protected <T> Query setPageParameter(final Query q, final Page<T> page) {
		// hibernate的firstResult的序号从0开始
		q.setFirstResult(page.getFirstOfPage() - 1);
		q.setMaxResults(page.getPageSize());
		return q;
	}

	/**
	 * 设置分页参数到Criteria对象,辅助函数.
	 * 
	 * @param <T>
	 * @param c
	 * @param page
	 * @return <T>
	 */
	protected <T> Criteria setPageParameter(final Criteria c, final Page<T> page) {
		// hibernate的firstResult的序号从0开始
		c.setFirstResult(page.getFirstOfPage() - 1);
		c.setMaxResults(page.getPageSize());

		if (page.isOrderBySetted()) {
			String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
			String[] orderArray = StringUtils.split(page.getOrder(), ',');

			// Assert.isTrue(orderByArray.length == orderArray.length,
			// "分页多重排序参数中,排序字段与排序方向的个数不相等");

			for (int i = 0; i < orderByArray.length; i++) {
				if (Page.ASC.equals(orderArray[i])) {
					c.addOrder(Order.asc(orderByArray[i]));
				} else {
					c.addOrder(Order.desc(orderByArray[i]));
				}
			}
		}
		return c;
	}

	/**
	 * 根据查询HQL与参数列表创建Query对象.
	 * 
	 * 本类封装的find()函数全部默认返回对象类型为T,当不为T时使用本函数.
	 * 
	 * @param queryString
	 * @param values
	 *            数量可变的参数,按顺序绑定.
	 * @return Query
	 */
	public Query createQuery(final String queryString, final Object... values) {
		Assert.hasText(queryString, "queryString不能为空");
		Query query = getSession().createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}

	/**
	 * 根据查询HQL与参数列表创建Query对象.
	 * 
	 * @param queryString
	 * @param values
	 *            命名参数,按名称绑定.
	 * @return Query
	 */
	public Query createQuery(final String queryString,
			final Map<String, Object> values) {
		Assert.hasText(queryString, "queryString不能为空");
		Query query = getSession().createQuery(queryString);
		if (values != null) {
			query.setProperties(values);
		}
		return query;
	}

	// ////////////////////////////////////////////////////////////////////

	/**
	 * 按HQL查询唯一对象.
	 * 
	 * @param <X>
	 * @param hql
	 * @param values
	 *            数量可变的参数,按顺序绑定.
	 * @return Query
	 */
	public <X> X findUnique(final String hql, final Object... values) {
		return (X) createQuery(hql, values).uniqueResult();
	}

	/**
	 * 按HQL查询唯一对象.
	 * 
	 * @param <X>
	 * @param hql
	 * @param values
	 *            命名参数,按名称绑定.
	 * @return <X>
	 */
	public <X> X findUnique(final String hql, final Map<String, Object> values) {
		return (X) createQuery(hql, values).uniqueResult();
	}

	/**
	 * 按Criteria查询唯一对象.
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param criterions
	 *            数量可变的Criterion.
	 * @return <T>
	 */
	public <T> T findUnique(Class<T> entityClass, final Criterion... criterions) {
		return (T) createCriteria(entityClass, criterions).uniqueResult();
	}

	/**
	 * 根据属性名和属性值查询唯一对象.
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param propertyName
	 * @param value
	 * @return 符合条件的唯一对象 or null if not found.
	 */
	public <T> T findUnique(Class<T> entityClass, String propertyName,
			Object value) {
		Assert.hasText(propertyName);
		return (T) createCriteria(entityClass,
				Restrictions.eq(propertyName, value)).uniqueResult();
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 按Criteria查询对象列表.
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param criterions
	 *            数量可变的Criterion.
	 * @return <T>
	 */
	public <T> List<T> find(Class<T> entityClass, final Criterion... criterions) {
		return createCriteria(entityClass, criterions).list();
	}

	/**
	 * 按Criteria查询对象列表.
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param orderBy
	 * @param isAsc
	 * @param criterions
	 *            数量可变的Criterion.
	 * @return <T>
	 */
	public <T> List<T> find(Class<T> entityClass, String orderBy,
			boolean isAsc, final Criterion... criterions) {
		return createCriteria(entityClass, orderBy, isAsc, criterions).list();
	}

	/**
	 * 创建Criteria对象.
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param criterions
	 *            可变的Restrictions条件列表,见{@link #createQuery(String,Object...)}
	 * @return <T>
	 */
	public <T> Criteria createCriteria(Class<T> entityClass,
			Criterion... criterions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		return criteria;
	}

	/**
	 * 创建Criteria对象，带排序字段与升降序字段.
	 * 
	 * @param <T>
	 * @param entityClass
	 * @param orderBy
	 * @param isAsc
	 * @param criterions
	 *            数量可变的Criterion.
	 * @return <T>
	 */
	public <T> Criteria createCriteria(Class<T> entityClass, String orderBy,
			boolean isAsc, Criterion... criterions) {
		Assert.hasText(orderBy);
		Criteria criteria = createCriteria(entityClass, criterions);
		if (isAsc) {
			criteria.addOrder(Order.asc(orderBy));
		} else {
			criteria.addOrder(Order.desc(orderBy));
		}
		return criteria;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 执行count查询获得本次Hql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
	 * 
	 * @param hql
	 * @param values
	 * @return long
	 */
	public long countHqlResult(final String hql, final Object... values) {
		Long count = 0L;
		String fromHql = hql;
		// select子句与order by子句会影响count查询,进行简单的排除.
		fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "order by");

		String countHql = "select count(*) " + fromHql;

		try {
			count = findUnique(countHql, values);
		} catch (RuntimeException e) {
			throw new RuntimeException("hql can't be auto count, hql is:"
					+ countHql, e);
		}
		return count;
	}

	/**
	 * 执行count查询获得本次Hql查询所能获得的对象总数.
	 * 
	 * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
	 * 
	 * @param hql
	 * @param values
	 * @return long
	 */
	public long countHqlResult(final String hql,
			final Map<String, Object> values) {
		Long count = 0L;
		String fromHql = hql;
		// select子句与order by子句会影响count查询,进行简单的排除.
		fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "order by");

		String countHql = "select count(*) " + fromHql;

		try {
			count = findUnique(countHql, values);
		} catch (RuntimeException e) {
			throw new RuntimeException("hql can't be auto count, hql is:"
					+ countHql, e);
		}

		return count;
	}

	/**
	 * 执行count查询获得本次Criteria查询所能获得的对象总数.
	 * 
	 * @param c
	 * @return int
	 */
	@SuppressWarnings("rawtypes")
	public int countCriteriaResult(final Criteria c) {
		CriteriaImpl impl = (CriteriaImpl) c;

		// 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
		Projection projection = impl.getProjection();
		ResultTransformer transformer = impl.getResultTransformer();

		List<CriteriaImpl.OrderEntry> orderEntries = null;
		Field orderEntries_field = ReflectionExtendUtils.findField(impl.getClass(), "orderEntries");
		orderEntries = (List) ReflectionExtendUtils.getField(orderEntries_field, impl);
		ReflectionExtendUtils.setField(orderEntries_field, impl, new ArrayList(10));

		// 执行Count查询
		int totalCount = (Integer) c.setProjection(Projections.rowCount())
				.uniqueResult();

		// 将之前的Projection,ResultTransformer和OrderBy条件重新设回去
		c.setProjection(projection);

		if (projection == null) {
			c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		if (transformer != null) {
			c.setResultTransformer(transformer);
		}
		ReflectionExtendUtils.setField(orderEntries_field, impl, orderEntries);

		return totalCount;
	}

	/**
	 * 判断对象某些属性的值在数据库中是否唯一.
	 *
	 * @param <T>
	 * @param entityClass
	 * @param entity
	 * @param uniquePropertyNames
	 *            在POJO里不能重复的属性列表,以逗号分割 如"name,loginid,password"
	 * @return <T>
	 */
	public <T> boolean isUnique(Class<T> entityClass, Object entity,
			String uniquePropertyNames) {
		Assert.hasText(uniquePropertyNames);
		Criteria criteria = createCriteria(entityClass).setProjection(
				Projections.rowCount());
		String[] nameList = uniquePropertyNames.split(",");
		try {
			// 循环加入唯一列
			for (String name : nameList) {
				criteria.add(Restrictions.eq(name,
						PropertyUtils.getProperty(entity, name)));
			}

			// 以下代码为了如果是update的情况,排除entity自身.
			String idName = getIdName(entityClass);

			// 取得entity的主键值
			Serializable id = getId(entityClass, entity);

			// 如果id!=null,说明对象已存在,该操作为update,加入排除自身的判断
			if (id != null) {
				criteria.add(Restrictions.not(Restrictions.eq(idName, id)));
			}

		} catch (IllegalAccessException e) {
			logger.error("异常:{}", e.getMessage());
		} catch (InvocationTargetException e) {
			logger.error("异常:{}", e.getMessage());
		} catch (NoSuchMethodException e) {
			logger.error("异常:{}", e.getMessage());
		}
		return (Long) criteria.uniqueResult() == 0;
	}

	/**
	 * 取得对象的主键值,辅助函数.
	 * 
	 * @param entityClass
	 * @param entity
	 * @return Serializable
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Serializable getId(@SuppressWarnings("rawtypes") Class entityClass, Object entity)
			throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		Assert.notNull(entity);
		Assert.notNull(entityClass);
		return (Serializable) PropertyUtils.getProperty(entity,
				getIdName(entityClass));
	}

	/**
	 * 取得对象的主键名,辅助函数.
	 * 
	 * @param clazz
	 * @return String
	 */
	public String getIdName(@SuppressWarnings("rawtypes") Class clazz) {
		Assert.notNull(clazz);
		ClassMetadata meta = getSessionFactory().getClassMetadata(clazz);
		Assert.notNull(meta, "Class " + clazz
				+ " not define in hibernate session factory.");
		String idName = meta.getIdentifierPropertyName();
		Assert.hasText(idName, clazz.getSimpleName()
				+ " has no identifier property define.");
		return idName;
	}

	/**
	 * 获取数据库服务器的日期,“yyyy-mm-dd hh24:mi:ss”格式
	 * 
	 * @return 日期
	 */
	public Date getSystemDateTime() {
		Query query = getSession().createSQLQuery(
				" SELECT to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') FROM DUAL");
		String str = (String) query.uniqueResult();
		try {
			return DateUtils.parseDate(str, "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			throw new RuntimeException("获取数据库服务器时间失败：", e);
		}
	}

	/**
	 * 获取数据库服务器的日期,“yyyy-mm-dd hh24:mi:ss”格式
	 * 
	 * @return String
	 */
	public String getSystemDateTimeString() {
		Query query = getSession().createSQLQuery(
				" SELECT to_char(sysdate,'yyyy-mm-dd hh24:mi:ss') FROM DUAL");
		String str = (String) query.uniqueResult();
		return str;
	}

	/**
	 * 获取数据库服务器的日期,“yyyymmddhh24miss”格式
	 * 
	 * @return String
	 */
	public String getSystemDateTimeGabString() {
		Query query = getSession().createSQLQuery(
				" SELECT to_char(sysdate,'yyyymmddhh24miss') FROM DUAL");
		String str = (String) query.uniqueResult();
		return str;
	}

	/**
	 * 获取几小时前后的数据库服务器的日期,“yyyymmddhh24miss”格式
	 * 
	 * @param hour
	 *            小时
	 * @return String
	 */
	public String getSystemDateTimeGabString(int hour) {
		String time = (hour < 0 ? "" : "+" ) + hour;
		String sql = " SELECT to_char(sysdate,'yyyymmddhh24miss') FROM DUAL";
		if (hour != 0) {
			sql = " SELECT to_char(sysdate " + time
					+ "/ 24,'yyyymmddhh24miss') FROM DUAL";
		}
		Query query = getSession().createSQLQuery(sql);
		String str = (String) query.uniqueResult();
		return str;
	}

	/**
	 * 获取一条件下的记录数量,该方法比较快，不会产生大量的对象在内存中
	 * 
	 * @param queryString
	 * @return Long
	 */
	public Long getCount(final String queryString) {
		Query query = getSession().createQuery(queryString);
		return (Long) query.uniqueResult();
	}

	/**
	 * 统计总条数
	 * 
	 * @param queryString
	 *            select count(*) from XXX
	 * @param values
	 * @return Long
	 */
	public Long getCount(final String queryString, final Object... values) {
		Query query = getSession().createQuery(queryString);
		int i = 0;
		for (Object o : values) {
			query.setParameter(i, o);
			i++;
		}
		return (Long) query.uniqueResult();
	}

	/**
	 * 带分页的SQL执行
	 * 
	 * @param <T>
	 * 
	 * @param querySql
	 *            HQL
	 * @param sp
	 *            起始页
	 * @param size
	 *            每页大小
	 * @param values
	 *            参数
	 * @return <T>
	 */
	public <T> List<T> getTopQuery(String querySql, int sp, int size,
			final Object... values) {
		Query query = getSession().createQuery(querySql);
		query.setFirstResult(sp);
		query.setMaxResults(size);
		int i = 0;
		for (Object o : values) {
			query.setParameter(i, o);
			i++;
		}
		return query.list();
	}

	/**
	 * 调用无返回值的存储过程.
	 * 
	 * @param proName
	 */
	@Transactional
	public void callProcedure(String proName, Object... params) {
		String placeHolder = "";
		String procedureSql = "";
		Query query = null;
		if (params == null) {
			procedureSql = "{Call " + proName + "()}";
			query = getSession().createSQLQuery(procedureSql);
		} else {
			for (int i = 0; i < params.length; i++) {
				placeHolder += "?";
				if (i < params.length - 1) {
					placeHolder += ",";
				}
			}
			procedureSql = "{Call " + proName + "(" + placeHolder + ")}";
			query = getSession().createSQLQuery(procedureSql);
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i, params[i]);
			}
		}
		query.executeUpdate();
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
	@SuppressWarnings("rawtypes")
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
}
