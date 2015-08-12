package com.sina.ad.brave;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.github.kristofa.brave.SpanCollector;

@Component
public class Brave {

	public static ThreadLocal<Tracer> TRACER = new ThreadLocal<Tracer>();
	
	/**
	 * Trace初始化
	 * @param request
	 * @param spanCollector
	 */
	public static void beginTrace(final HttpServletRequest request, final SpanCollector spanCollector) {
		beginTrace(request.getLocalAddr(), request.getLocalPort(), getServiceName(request), spanCollector);
	}
	
	public static void beginTrace(String ip, int port, String serviceName,SpanCollector spanCollector) {
		Brave.TRACER.set(new Tracer(new ThreadState(ip,
				port, serviceName), spanCollector));
	}
	
	/**
	 * 新建一个span
	 * @param request
	 */
	public static void newSpan(final HttpServletRequest request) {
		newSpan(getSpanName(null, request));
	}
	
	public static void newSpan(String serviceName) {
		Brave.TRACER.get().newSpan(serviceName);
	}
	
	/**
	 * 给当前span添加annotation
	 * @param annotationName
	 */
	public static void submitAnnotation(String annotationName) {
		Brave.TRACER.get().submitAnnotation(annotationName);
	}
	
	/**
	 * 利用spanController发送trace log
	 */
	public static void collect() {
		Brave.TRACER.get().collect();
	}
	
	public static void clear() {
		Brave.TRACER.remove();
	}

	private static String getSpanName(final String name,
			final HttpServletRequest request) {
		if (name == null || name.trim().isEmpty()) {
			return request.getRequestURI();
		}
		return name;
	}
	
	private static String getServiceName(final HttpServletRequest request) {
		return getSpanName(null, request);
	}

}
