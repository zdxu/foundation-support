/**	
 * <br>
 * Copyright 2013 IFlyTek. All rights reserved.<br>
 * <br>			 
 * Package: com.iflytek.support.jdbc.transform <br>
 * FileName: Transformers.java <br>
 * <br>
 * @version
 * @author xkfeng@iflytek.com
 * @created 2013-11-13
 * @last Modified 
 * @history
 */

package com.zdxu.bd.support.hibernate;

import org.hibernate.transform.ResultTransformer;



/**
 *  hibernate Transformers
 *  
 *  @author xkfeng@iflytek.com
 */

public class ResultTransformers {
	
	/**
	 * 
	 *  aliasToBean 别名转换成java属性规则如：COLUMN_NAME -> columnName
	 *  @param target Class
	 *  @return Transformers
	 *  @author xkfeng@iflytek.com
	 *  @created 2013-11-13 上午09:00:50
	 *  @lastModified       
	 *  @history
	 */
	public static ResultTransformer aliasToBean(@SuppressWarnings("rawtypes") Class target) {
		return new AliasToBeanResultTransformer(target);
	}
}
