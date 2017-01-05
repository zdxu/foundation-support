/**	
 * <br>
 * Copyright 2014 IFlyTek.All rights reserved.<br>
 * <br>			 
 * Package: com.iflytek.yzt.base <br>
 * FileName: GlobalConfig.java <br>
 * <br>
 * @version
 * @author CYF
 * @created 2014-9-23
 * @last Modified 
 * @history
 */
package com.zdxu.bd.support.utils;

import java.lang.reflect.Method;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.support.PropertiesLoaderSupport;

/**
 * {读取配置信息的类}
 * 
 * @author CYF
 * @created 2014-9-23 下午2:34:15
 * @lastModified
 * @history
 */
public class GlobalConfig {

	private static Logger logger = LoggerFactory.getLogger(GlobalConfig.class);
	
	private static Properties properties = new Properties();

	static {
		// 初始化，配置属性到本地properties中
		try {
			//读取applicationCotext对象
			ApplicationContext applicationContext = SpringContextUtils.getApplicationContext();
			// get the names of BeanFactoryPostProcessor  
            String[] postProcessorNames = applicationContext  
                    .getBeanNamesForType(BeanFactoryPostProcessor.class,true,true);  
              
            for (String ppName : postProcessorNames) {  
                // get the specified BeanFactoryPostProcessor  
                BeanFactoryPostProcessor beanProcessor=  
                applicationContext.getBean(ppName, BeanFactoryPostProcessor.class);  
                // check whether the beanFactoryPostProcessor is   
                // instance of the PropertyResourceConfigurer  
                // if it is yes then do the process otherwise continue  
                if(beanProcessor instanceof PropertyResourceConfigurer){  
                    PropertyResourceConfigurer propertyResourceConfigurer=  
                            (PropertyResourceConfigurer) beanProcessor;  
                      
                    // get the method mergeProperties   
                    // in class PropertiesLoaderSupport  
                    Method mergeProperties=PropertiesLoaderSupport.class.  
                        getDeclaredMethod("mergeProperties");  
                    // get the props  
                    mergeProperties.setAccessible(true);  
                    Properties props=(Properties) mergeProperties.  
                    invoke(propertyResourceConfigurer);  
                      
                    // get the method convertProperties   
                    // in class PropertyResourceConfigurer  
                    Method convertProperties=PropertyResourceConfigurer.class.  
                    getDeclaredMethod("convertProperties", Properties.class);  
                    // convert properties  
                    convertProperties.setAccessible(true);  
                    convertProperties.invoke(propertyResourceConfigurer, props);  
                      
                    properties.putAll(props);  
                }  
            }  
		} catch (Exception e) {
			// 初始化属性读取失败
			logger.error("初始化属性读取失败", e);
		}
	}

	public static String getString(String key) {
		return properties.getProperty(key);
	}
	
	public static String getString(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
}
