package com.sina.ad.brave;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.github.kristofa.brave.SpanCollector;
import com.twitter.zipkin.gen.zipkinCoreConstants;

public class MonitorHandlerInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	SpanCollector spanCollector;
	
	@Autowired
	TraceFilters traceFilters;

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		try{
			Brave.submitAnnotation(zipkinCoreConstants.SERVER_SEND);
			Brave.collect();
		}finally {
			Brave.clear();
		}
		
		super.afterCompletion(request, response, handler, ex);
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		Brave.beginTrace(request, spanCollector,traceFilters);
		Brave.newSpan(request);
		Brave.submitAnnotation(zipkinCoreConstants.SERVER_RECV);
		Thread.sleep(1000);
		return super.preHandle(request, response, handler);
	}





	

}
