package com.sina.ad.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sina.ad.service.ServiceA;

public class Main {
	public static void main(String[] args) {
		ApplicationContext context =
			    new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});
		
		ServiceA serviceA = (ServiceA)context.getBean("serviceA");
//		serviceA.method2();
		serviceA.method1();
		
	}
}
