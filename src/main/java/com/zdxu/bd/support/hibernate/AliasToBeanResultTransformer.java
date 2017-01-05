/**	
 * <br>
 * Copyright 2013 IFlyTek. All rights reserved.<br>
 * <br>			 
 * Package: com.iflytek.support.jdbc.transform <br>
 * FileName: AliasToBeanResultTransformer.java <br>
 * <br>
 * @version
 * @author xkfeng@iflytek.com
 * @created 2013-11-13
 * @last Modified 
 * @history
 */

package com.zdxu.bd.support.hibernate;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyChainedImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyFieldImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyMapImpl;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.transform.ResultTransformer;


/**
 * hibernate ResultTransformer实现 AliasToBean
 * alias to java Field,如：COLUMN_NAME -> columnName
 *  
 *  @author xkfeng@iflytek.com
 */

@SuppressWarnings("serial")
public class AliasToBeanResultTransformer implements ResultTransformer {
	/**
	 * 转换类
	 */
	@SuppressWarnings("rawtypes")
	private final Class resultClass;
	/**
	 * 
	 */
	private final PropertyAccessStrategy propertyAccessStrategy;
	/**
	 * bean setters
	 */
	private Setter[] setters;

	/**
	 * 
	 * 构造器
	 *  @param resultClass 结果Class
	 *  @author xkfeng@iflytek.com
	 *  @created 2013-12-10 上午09:38:18
	 *  @lastModified       
	 *  @history
	 */
	public AliasToBeanResultTransformer(@SuppressWarnings("rawtypes") Class resultClass) {
		if ( resultClass == null ) {
			throw new IllegalArgumentException( "resultClass cannot be null" );
		}
		this.resultClass = resultClass;
		propertyAccessStrategy = new PropertyAccessStrategyChainedImpl(
				PropertyAccessStrategyBasicImpl.INSTANCE,
				PropertyAccessStrategyFieldImpl.INSTANCE,
				PropertyAccessStrategyMapImpl.INSTANCE
		);
	}

	/**
	 *  结果转换 aliases to tuple
	 *  @param tuple tuple
	 *  @param aliases aliases
	 *  @return java bean 
	 *  @author xkfeng@iflytek.com
	 *  @created 2013-12-10 上午09:36:32
	 *  @lastModified      
	 *  @history
	 */
	public Object transformTuple(Object[] tuple, String[] aliases) {
		Object result;

		try {
			if ( setters == null ) {
				setters = new Setter[aliases.length];
				for ( int i = 0; i < aliases.length; i++ ) {
					String alias = aliases[i];
					if ( alias != null ) {
						try {
							setters[i] = propertyAccessStrategy.buildPropertyAccess( resultClass, alias).getSetter();
						} catch (PropertyNotFoundException e) {
							setters[i] = null;
						}
					}
				}
			}
			result = resultClass.newInstance();

			for ( int i = 0; i < aliases.length; i++ ) {
				if ( setters[i] != null ) {
					if(setters[i].getMethod().getParameterTypes()[0].equals(Long.class) 
							&& tuple[i] !=null &&tuple[i].getClass().equals(BigDecimal.class)){
						tuple[i] = ((BigDecimal) tuple[i]).longValue();
					}else if(setters[i].getMethod().getParameterTypes()[0].equals(String.class) 
							&& tuple[i] !=null &&tuple[i].getClass().equals(BigDecimal.class)){
						tuple[i] = String.valueOf(((BigDecimal) tuple[i]).longValue());
					}else if(setters[i].getMethod().getParameterTypes()[0].equals(String.class) 
							&& tuple[i] !=null &&tuple[i].getClass().equals(Character.class)){
						tuple[i] = tuple[i].toString();
					}
					setters[i].set( result, tuple[i], null );
				}
			}
		}
		catch ( InstantiationException e ) {
			throw new HibernateException( "Could not instantiate resultclass: " + resultClass.getName() );
		}
		catch ( IllegalAccessException e ) {
			throw new HibernateException( "Could not instantiate resultclass: " + resultClass.getName() );
		}

		return result;
	}

	/**
	 * 转换列表
	 *  @param collection 列表
	 *  @return 需要转换的列表
	 *  @author xkfeng@iflytek.com
	 *  @created 2013-12-10 上午09:37:35
	 *  @lastModified      
	 *  @history
	 */
	@SuppressWarnings("rawtypes")
	public List transformList(List collection) {
		return collection;
	}

	/**
	 * hashCode
	 *  @return hashCode
	 *  @author xkfeng@iflytek.com
	 *  @created 2013-12-10 上午09:37:56
	 *  @lastModified      
	 *  @history
	 */
	public int hashCode() {
		int result;
		result = resultClass.hashCode();
		result = 31 * result + propertyAccessStrategy.hashCode();
		return result;
	}
}




