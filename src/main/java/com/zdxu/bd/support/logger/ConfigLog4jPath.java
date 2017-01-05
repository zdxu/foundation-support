package com.zdxu.bd.support.logger;

import java.io.IOException;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.zdxu.bd.base.Syscode;

public class ConfigLog4jPath {

	public static ResourceLoader resourcesLoader = new DefaultResourceLoader();

	/**
	 *  初始化日志配置文件
	 *  
	 *  @param configFilename  日志配置文件所在位置
	 *  @author zdxu
	 *  @created 2016年6月21日 下午9:36:06
	 *  @lastModified       
	 *  @history
	 */
	public ConfigLog4jPath(String path) {
		Resource resource = resourcesLoader.getResource(path);
		String configFilePath = "";
		try {
			configFilePath = resource.getFile().getAbsolutePath();
		} catch (IOException e) {
			throw new RuntimeException("加载日志配置文件失败", e);
		}
		//初始配置文件并定时扫描
		DOMConfigurator.configureAndWatch(configFilePath, Syscode.LOG_DELAY);
	}
}
