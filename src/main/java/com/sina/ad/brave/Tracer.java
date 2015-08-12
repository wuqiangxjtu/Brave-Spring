package com.sina.ad.brave;

import java.util.EmptyStackException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.kristofa.brave.SpanCollector;
import com.twitter.zipkin.gen.Annotation;
import com.twitter.zipkin.gen.Span;

public class Tracer {

	private final static Random RANDOM_GENERATOR = new Random();
	
	private final SpanCollector spanCollector;

	private final ThreadState state;
	
	private AtomicBoolean IS_SAMPLE = new AtomicBoolean(true);

	public Tracer(ThreadState state, SpanCollector spanCollector) {
		this.state = state;
		this.spanCollector = spanCollector;
		
		
	}
	
	public void collect() {
		this.spanCollector.collect(state.pop());
	}
	

	/**
	 * 获取新的Span
	 * 
	 * @param spanName
	 * @return
	 */
	public void newSpan(String spanName) {
		
		long newSpanId = RANDOM_GENERATOR.nextLong();
		try {
			Span currentSpan = null;
			currentSpan = state.peek();
			Span newSpan = new Span(currentSpan.getTrace_id(), spanName,
					newSpanId, null, null);
			newSpan.setParent_id(currentSpan.getId());
			state.push(newSpan);
			
		}catch(EmptyStackException e) {
			Span newSpan = new Span(newSpanId, spanName,
					newSpanId, null, null);
			state.push(newSpan);
		}
		
	
	}

	public void submitAnnotation(String annotationName, long startTime,
			long endTime) {
		Span span = state.peek();
		if (span != null) {
			Annotation annotation = new Annotation();
			int duration = (int) (endTime - startTime);
			annotation.setTimestamp(startTime * 1000);
			annotation.setHost(state.getEndpoint());
			annotation.setDuration(duration * 1000);
			// Duration is currently not supported in the ZipkinUI, so also add
			// it as part of the annotation name.
			annotation.setValue(annotationName + "=" + duration + "ms");
			addAnnotation(span, annotation);
		}
	}

	public void submitAnnotation(String annotationName) {
		Span span = state.peek();
		if (span != null) {
			Annotation annotation = new Annotation();
			annotation.setTimestamp(currentTimeMicroseconds());
			annotation.setHost(state.getEndpoint());
			annotation.setValue(annotationName);
			addAnnotation(span, annotation);
		}
	}

	long currentTimeMicroseconds() {
		return System.currentTimeMillis() * 1000;
	}

	private void addAnnotation(Span span, Annotation annotation) {
		synchronized (span) {
			span.addToAnnotations(annotation);
		}
	}
	
	

}
