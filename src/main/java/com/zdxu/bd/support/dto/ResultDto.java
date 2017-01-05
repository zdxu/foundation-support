package com.zdxu.bd.support.dto;

import java.io.Serializable;

/**
 * 结果DTO
 * 
 */

public class ResultDto implements Serializable {

	private static final long serialVersionUID = 4413631466570978224L;

	/**
	 * 结果标示
	 */
	private String flag;

	/**
	 * 结果详细
	 */
	private String result;

	/**
	 * 需要传回页面的数据
	 */
	private Object data;

	public ResultDto() {

	}

	public ResultDto(String flag, String result) {
		this.flag = flag;
		this.result = result;
	}

	public ResultDto(String flag, String result, Object data) {
		this.flag = flag;
		this.result = result;
		this.data = data;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}

