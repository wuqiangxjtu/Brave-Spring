package com.sina.ad.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.sina.ad.brave.Brave;
import com.sina.ad.brave.BraveConstants;
import com.twitter.zipkin.gen.zipkinCoreConstants;

@Component
@Aspect
public class LogAspect {
	
	@Pointcut("(execution(* com.sina.ad.controller..*.*(..))) || (execution(* com.sina.ad.service..*.*(..)))" )
	public void pointCut() {
		
	}

	@Before("pointCut()")
	public void before(JoinPoint joinPoint) {
//		System.out.println(joinPoint.getSignature() + "--- before advice ---");
		Brave.newSpan(joinPoint.getSignature().toString());
		Brave.submitAnnotation(zipkinCoreConstants.SERVER_RECV);
//		Brave.submitAnnotation(BraveConstants.METHOD_CALL);
	}
	
	@After("pointCut()")
	public void after(JoinPoint joinPoint) {
//		System.out.println(joinPoint.getSignature() + "--- after advice ---");
		Brave.submitAnnotation(zipkinCoreConstants.SERVER_SEND);
//		Brave.submitAnnotation(BraveConstants.METHOD_RETURN);
		Brave.collect();
	}
	
//	@Around("pointCut()")
	public void around(ProceedingJoinPoint pjp) throws Throwable { 
		System.out.println(pjp.getSignature() + "  --- around start advice ---");
		try {
			pjp.proceed();
		}catch(Throwable ex) {
			System.out.println("error in around");
			throw ex;
		}
		System.out.println(pjp.getSignature() + "--- around end advice ---");
	}
}
