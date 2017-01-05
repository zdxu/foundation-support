package com.zdxu.bd.support.exception;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import com.zdxu.bd.base.Message;
import com.zdxu.bd.support.dto.ResultDto;
import com.zdxu.bd.support.utils.ResultUtil;

/**
 * 不必在Controller中对异常进行处理，抛出即可，由此异常解析器统一控制。<br>
 * ajax请求（有@ResponseBody的Controller）发生错误，输出JSON。<br>
 * 页面请求（无@ResponseBody的Controller）发生错误，输出错误页面。<br>
 * 需要与AnnotationMethodHandlerAdapter使用同一个messageConverters<br>
 * Controller中需要有专门处理异常的方法。 date: 2015年11月24日 下午3:19:09 <br/>
 * 
 * @author ycli7
 */
public class AnnotationHandlerMethodExceptionResolver extends
		ExceptionHandlerExceptionResolver {
	/**
	 * 日志接口
	 */
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 日志service
	 */
//	@Autowired
//	private CmsLogService cmsLogService;
	
	private String defaultErrorView;

	public String getDefaultErrorView() {
		return defaultErrorView;
	}

	public void setDefaultErrorView(String defaultErrorView) {
		this.defaultErrorView = defaultErrorView;
	}

	/**
	 * @see org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver#doResolveHandlerMethodException(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse,
	 *      org.springframework.web.method.HandlerMethod, java.lang.Exception)
	 */
	protected ModelAndView doResolveHandlerMethodException(
			HttpServletRequest request, HttpServletResponse response,
			HandlerMethod handlerMethod, Exception exception) {
		// 对操作日志进行保存
//		cmsLogService.saveOperationLog(request, exception);
		ModelAndView returnValue = super.doResolveHandlerMethodException(
				request, response, handlerMethod, exception);

		if (logger.isErrorEnabled()) {
			logger.error("---统一异常处理记录日志---", exception);
		}
		
		//如果产生其他异常，将token重新放入session
		String tokenString = request.getParameter("token");
		if(StringUtils.isNotBlank(tokenString)){
			request.getSession(false).setAttribute("token", tokenString);
		}
		
		//判断请求的方法是否被responsebody注释
		if (isAnnotationByResponseBody(handlerMethod)) {
			try {
				handleResponseStatus(response, handlerMethod);
				// 如果没有ExceptionHandler注解那么returnValue就为空，执行默认操作；如果不为空，使用ExceptionHandler自己的处理方法执行
				if (returnValue == null) {
					returnValue = defaultHandleResponseBody(response);
				}else{
					returnValue = handleResponseBody(returnValue, request, response);
				}
			} catch (Exception e) {
				return null;
			}
		}

		if (returnValue == null) {
			returnValue = new ModelAndView();
			if (returnValue.getViewName() == null) {
				returnValue.setViewName(defaultErrorView);
			}
		}

		return returnValue;

	}

	/**
	 * defaultHandleResponseBody:(默认异常处理方式). <br/>
	 * @author ycli7
	 * @param response HttpServletResponse
	 * @return ModelAndView
	 * @throws IOException IOException
	 */
	private ModelAndView defaultHandleResponseBody(HttpServletResponse response)
			throws IOException {
		ResultDto res = ResultUtil.fail(Message.LOAD_ERROR);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().print(JSONObject.fromObject(res).toString());
		return new ModelAndView();
	}

	/**
	 * handleResponseBody:(处理返回值). <br/>
	 * 
	 * @author ycli7
	 * @param returnValue
	 *            ModelAndView
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "resource" })
	private ModelAndView handleResponseBody(ModelAndView returnValue,
			HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map value = returnValue.getModelMap();
		HttpInputMessage inputMessage = new ServletServerHttpRequest(request);
		List<MediaType> acceptedMediaTypes = inputMessage.getHeaders().getAccept();
		if (acceptedMediaTypes.isEmpty()) {
			acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
		}
		MediaType.sortByQualityValue(acceptedMediaTypes);
		HttpOutputMessage outputMessage = new ServletServerHttpResponse(
				response);
		Class<?> returnValueType = value.getClass();
		List<HttpMessageConverter<?>> messageConverters = super
				.getMessageConverters();
		if (messageConverters != null) {
			for (MediaType acceptedMediaType : acceptedMediaTypes) {
				for (HttpMessageConverter messageConverter : messageConverters) {
					if (messageConverter.canWrite(returnValueType,
							acceptedMediaType)) {
						messageConverter.write(value, acceptedMediaType,
								outputMessage);
						return new ModelAndView();
					}
				}
			}
		}
		if (logger.isWarnEnabled()) {
			logger.warn("Could not find HttpMessageConverter that supports return type ["
					+ returnValueType + "] and " + acceptedMediaTypes);
		}
		return null;
	}

	/**
	 * isAjax:(判断是否是ajax). <br/>
	 * 
	 * @author ycli7
	 * @param handlerMethod
	 *            HandlerMethod
	 * @return 是否是ajax
	 */
	private boolean isAnnotationByResponseBody(HandlerMethod handlerMethod) {
		if (handlerMethod != null) {
			ResponseBody responseBodyAnn = AnnotationUtils.findAnnotation(
					handlerMethod.getMethod(), ResponseBody.class);
			return responseBodyAnn != null;
		}

		return false;
	}

	/**
	 * 处理ResponseStatus注解
	 * （ResponseStatus 修饰自定义异常）
	 * @author ycli7
	 * @param response
	 *            HttpServletResponse
	 * @param method
	 *            HandlerMethod
	 */
	private void handleResponseStatus(HttpServletResponse response,
			HandlerMethod handlerMethod) {
		if (handlerMethod == null) {
			return;
		}
		ResponseStatus responseStatusAnn = AnnotationUtils.findAnnotation(
				handlerMethod.getMethod(), ResponseStatus.class);
		if (responseStatusAnn != null) {
			HttpStatus responseStatus = responseStatusAnn.value();
			String reason = responseStatusAnn.reason();
			if (StringUtils.isBlank(reason)) {
				response.setStatus(responseStatus.value());
			} else {
				try {
					response.sendError(responseStatus.value(), reason);
				} catch (IOException e) {
				}
			}
		}
	}
}
