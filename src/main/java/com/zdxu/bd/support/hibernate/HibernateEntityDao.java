package com.zdxu.bd.support.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.springframework.transaction.annotation.Transactional;

import com.zdxu.bd.support.utils.ReflectionExtendUtils;

 

/**
 * 负责为单个Entity对象提供CRUD操作的Hibernate DAO基类.
 * <p/>
 * 子类只要在类定义时指定所管理Entity的Class, 即拥有对单个Entity对象的CRUD操作.
 * <pre>
 * public class UserManager extends HibernateEntityDao<User> {
 * }
 * </pre>
 * @author wzgao
 *
 * @param <T>
 */
public class HibernateEntityDao<T> extends HibernateGenericDao implements EntityDao<T> {

	/**
	 * DAO所管理的Entity类型.
	 */
	protected Class<T> entityClass;

	/**
	 * 在构造函数中将泛型T.class赋给entityClass.
	 */
	public HibernateEntityDao() {
		entityClass = ReflectionExtendUtils.getSuperClassGenricType(getClass());
	}

	/**
	 * 取得entityClass.JDK1.4不支持泛型的子类可以抛开Class<T> entityClass,重载此函数达到相同效果。
	 * @return 类
	 */
	protected Class<T> getEntityClass() {
		return entityClass;
	}

	/**
	 * 根据ID获取对象.
	 * @param id 序列化ID
	 * @return 类
	 */
	public T get(Serializable id) {
		return get(getEntityClass(), id);
	}

	/**
	 * 获取全部对象
	 * @return 结果
	 */
	public List<T> getAll() {
		return getAll(getEntityClass());
	}

	/**
	 * 获取全部对象,带排序参数.
	 * @param orderBy 排序
	 * @param isAsc 
	 * @return 结果
	 */
	public List<T> getAll(String orderBy, boolean isAsc) {
		return getAll(getEntityClass(), orderBy, isAsc);
	}

	/**
	 * 根据ID移除对象.
	 * @param id 序列化ID
	 */
	@Transactional
	public void removeById(Serializable id) {
		removeById(getEntityClass(), id);
	}

	/**
	 * 取得Entity的Criteria.
	 * @param criterions Criterion
	 * @return Criteria
	 */
	public Criteria createCriteria(Criterion... criterions) {
		return createCriteria(getEntityClass(), criterions);
	}

	/**
	 * 取得Entity的Criteria,带排序参数.
	 * @param orderBy 排序
	 * @param isAsc 
	 * @param criterions Criterion
	 * @return Criteria
	 */
	public Criteria createCriteria(String orderBy, boolean isAsc, Criterion... criterions) {
		return createCriteria(getEntityClass(), orderBy, isAsc, criterions);
	}

	/**
	 * 根据属性名和属性值查询对象.
	 * @param propertyName 属性名
	 * @param value 值
	 * @return 符合条件的对象列表
	 */
	public List<T> findBy(String propertyName, Object value) {
		return findBy(getEntityClass(), propertyName, value);
	}

	/**
	 * 根据属性名和属性值查询对象,带排序参数.
	 * @param propertyName 属性名
	 * @param value 值
	 * @param orderBy 排序
	 * @param isAsc 
	 * @return 符合条件的对象列表
	 */
	public List<T> findBy(String propertyName, Object value, String orderBy, boolean isAsc) {
		return findBy(getEntityClass(), propertyName, value, orderBy, isAsc);
	}

	/**
	 * 根据属性名和属性值查询单个对象.
	 * @param propertyName 属性名
	 * @param value 值
	 * @return 符合条件的唯一对象 or null
	 */
	public T findUniqueBy(String propertyName, Object value) {
		return findUnique(getEntityClass(), propertyName, value);
	}

	/**
	 * 判断对象某些属性的值在数据库中唯一.
	 * @param entity 实体
	 * @param uniquePropertyNames 在POJO里不能重复的属性列表,以逗号分割 如"name,loginid,password"
	 * @return 布尔型
	 */
	public boolean isUnique(Object entity, String uniquePropertyNames) {
		return isUnique(getEntityClass(), entity, uniquePropertyNames);
	}
}
