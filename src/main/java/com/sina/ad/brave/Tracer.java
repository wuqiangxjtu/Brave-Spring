package com.sina.ad.brave;

import java.util.EmptyStackException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.kristofa.brave.SpanCollector;
import com.github.kristofa.brave.TraceFilter;
import com.twitter.zipkin.gen.Annotation;
import com.twitter.zipkin.gen.Span;

public class Tracer {

	private final static Random RANDOM_GENERATOR = new Random();

	private final SpanCollector spanCollector;

	private final ThreadState state;

	private AtomicBoolean IS_SAMPLE = new AtomicBoolean(true);

	public Tracer(ThreadState state, SpanCollector spanCollector,
			TraceFilters traceFilters) {
		this.state = state;
		this.spanCollector = spanCollector;

		for (TraceFilter traceFilter : traceFilters.getTraceFilters()) {
			if (!traceFilter.trace(state.getEndpoint().getService_name())) {
				IS_SAMPLE.set(false);
			}
		}

	}

	public void collect() {
		if (IS_SAMPLE.get()) {
			this.spanCollector.collect(state.pop());
		}

	}

	/**
	 * 获取新的Span
	 * 
	 * @param spanName
	 * @return
	 */
	public void newSpan(String spanName) {
		if (IS_SAMPLE.get()) {
			long newSpanId = RANDOM_GENERATOR.nextLong();
			try {
				Span currentSpan = null;
				currentSpan = state.peek();
				Span newSpan = new Span(currentSpan.getTrace_id(), spanName,
						newSpanId, null, null);
				newSpan.setParent_id(currentSpan.getId());
				state.push(newSpan);

			} catch (EmptyStackException e) {
				Span newSpan = new Span(newSpanId, spanName, newSpanId, null,
						null);
				state.push(newSpan);
			}

		}
	}

	public void submitAnnotation(String annotationName, long startTime,
			long endTime) {
		if (IS_SAMPLE.get()) {
			try {
				Span span = state.peek();
				if (span != null) {
					Annotation annotation = new Annotation();
					int duration = (int) (endTime - startTime);
					annotation.setTimestamp(startTime * 1000);
					annotation.setHost(state.getEndpoint());
					annotation.setDuration(duration * 1000);
					// Duration is currently not supported in the ZipkinUI, so
					// also
					// add
					// it as part of the annotation name.
					annotation.setValue(annotationName + "=" + duration + "ms");
					addAnnotation(span, annotation);
				}
			} catch (EmptyStackException e) {
				e.printStackTrace();
			}
		}
	}

	public void submitAnnotation(String annotationName) {
		if (IS_SAMPLE.get()) {
			try {
				Span span = state.peek();
				if (span != null) {
					Annotation annotation = new Annotation();
					annotation.setTimestamp(currentTimeMicroseconds());
					annotation.setHost(state.getEndpoint());
					annotation.setValue(annotationName);
					addAnnotation(span, annotation);
				}
			} catch (EmptyStackException e) {
				e.printStackTrace();
			}
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
