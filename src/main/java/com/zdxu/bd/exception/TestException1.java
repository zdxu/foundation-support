package com.zdxu.bd.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "自定义注解异常")
public class TestException1 extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3225978017367613686L;

}
