package com.zdxu.bd.test;

public class Test2 {
	
	public static class Demo<T> {
		private T name;
		
		public Demo(T name) {
			this.name = name;
		}

		public T getName() {
			return name;
		}

		public void setName(T name) {
			this.name = name;
		}
	}
	
	public static void main(String[] args) {
		//getData(d1);
		//getData(d3);
		
		
	}
	
	public static void getData(Demo<? super Number> d) {
		System.out.println("data数据："+d.getName());
	}
	
	

}
