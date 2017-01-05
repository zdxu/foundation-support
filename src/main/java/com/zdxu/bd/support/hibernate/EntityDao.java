package com.zdxu.bd.support.hibernate;

import java.io.Serializable;
import java.util.List;

/**
 * 针对单个Entity对象的操作定义.不依赖于具体ORM实现方案.
 * @author wzgao
 *
 * @param <T>
 */
public interface EntityDao<T> {
	/**
	 * 根据Id获得某个类型对象
	 * @param id Id
	 * @return T
	 */
	T get(Serializable id);

	/**
	 * 获得所有对象集合
	 * @return List<T>
	 */
	List<T> getAll();

	/**
	 * 保存一个对象 
	 * @param o Object
	 */
	void save(Object o);

	/**
	 * 删除一个对象
	 * @param o Object
	 */
	void remove(Object o);

	/**
	 * 根据Id删除一个对象
	 * @param id Id
	 */
	void removeById(Serializable id);

	/**
	 * 获取Entity对象的主键名.
	 * @param clazz 类
	 * @return 警告
	 */
	String getIdName(@SuppressWarnings("rawtypes") Class clazz);
}
