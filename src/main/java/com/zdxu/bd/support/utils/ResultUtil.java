package com.zdxu.bd.support.utils;

import com.zdxu.bd.support.dto.ResultDto;

/**
 * 结果构造工具
 */

public class ResultUtil {
	
	/**
	 * 返回的标志为布尔值
	 */
	public static final Boolean YES = true;
	
	/**
	 * 返回的标志不为布尔值
	 */
	public static final Boolean NO = false;

	/**
	 * 成功
	 */
	public static final String TRUE = "true";

	/**
	 * 失败
	 */
	public static final String FALSE = "false";

	/**
	 * 
	 * 封装成功消息
	 * 
	 * @param result
	 *            消息
	 * @return
	 * @author hyzha
	 * @created 2014年9月2日 下午4:14:22
	 * @lastModified
	 * @history
	 */
	public static ResultDto success(String result) {
		ResultDto dto = new ResultDto();
		dto.setFlag(TRUE);
		dto.setResult(result);
		return dto;
	}

	/**
	 * 
	 * 封装失败消息
	 * 
	 * @param result
	 *            消息
	 * @return
	 * @author hyzha
	 * @created 2014年9月2日 下午4:15:12
	 * @lastModified
	 * @history
	 */
	public static ResultDto fail(String result) {
		ResultDto dto = new ResultDto();
		dto.setFlag(FALSE);
		dto.setResult(result);
		return dto;
	}

	/**
	 * 封装成功消息
	 * 
	 * @param result
	 *            消息结果
	 * @param data
	 *            需要返回的数据
	 * @return 结果对象
	 * @created 2014年9月5日 下午2:23:49
	 * @lastModified
	 * @history
	 */
	public static ResultDto success(String result, Object data) {
		return new ResultDto(TRUE, result, data);
	}

	/**
	 * 封装失败消息
	 * 
	 * @param result
	 *            消息结果
	 * @param data
	 *            需要返回的数据
	 * @return 结果对象
	 * @author ycli7
	 * @created 2014年9月5日 下午2:24:04
	 * @lastModified
	 * @history
	 */
	public static ResultDto fail(String result, Object data) {
		return new ResultDto(FALSE, result, data);
	}
}

