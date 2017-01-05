package com.zdxu.bd.test;

import java.util.ArrayList;

import com.zdxu.bd.support.utils.ReflectionExtendUtils;



public class Test1 {
	
	public static void main(String[] args) {
		
		System.out.println(String.class instanceof Class);
		
		System.out.println(ReflectionExtendUtils.getSuperClassGenricType(new ArrayList<String>().getClass()));
	}
	
	public static String first(final Demo s) {
		s.setName("");
		return s.getName();
	}
	
	public static class Demo {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}
}
