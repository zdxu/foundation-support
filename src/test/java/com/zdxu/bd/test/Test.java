package com.zdxu.bd.test;

import java.lang.reflect.Proxy;

import org.springframework.util.ReflectionUtils;


public class Test<T> {

	public static void main(String[] args) {
		Proxy.getInvocationHandler(new Object());
		//ReflectionUtils.invokeMethod(method, target, args);
	}
}
