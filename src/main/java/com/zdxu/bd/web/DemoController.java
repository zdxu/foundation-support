package com.zdxu.bd.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;

import com.zdxu.bd.exception.TestException1;
import com.zdxu.bd.support.utils.GlobalConfig;

@ControllerAdvice
@RequestMapping("/test")
public class DemoController {

	ResponseStatusExceptionResolver s;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 验证服务是否运行
	 * @param params
	 * @return
	 */
	@RequestMapping("/index.do")
	@ResponseBody
	public String demo() {
		System.out.println(GlobalConfig.getString("jdbc.username"));
		throw new RuntimeException("过滤器  验证统一异常处理");
		//return "application is running...";
	}
	
	@RequestMapping("/aaa")
	@ResponseBody
	public String demo1() {
		System.out.println("111111111111");
		//throw new RuntimeException("过滤器   验证统一异常处理");
		throw new TestException1();
	}
	
	@RequestMapping("/bbb")
	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "111")
	public String demo2() {
		return "111";
		//throw new TestException1();
	}
	
	@RequestMapping("/demo3")
	@ResponseBody
	public String demo3() {
		logger.info("测试日志是否响应！");
		return "test demo3"+GlobalConfig.getString("jdbc.pool.maxActive");
	}
	
	
}
