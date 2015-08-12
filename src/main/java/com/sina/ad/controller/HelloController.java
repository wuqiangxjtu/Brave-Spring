package com.sina.ad.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sina.ad.service.ServiceA;
import com.sina.ad.utils.ResponseJson;
import com.sina.ad.utils.StatusCode;


@Controller
@RequestMapping("/index")
public class HelloController {
	
	@Autowired
	ServiceA serviceA;
	
	@ResponseBody
	@RequestMapping("/hello")
	public ResponseJson hello() {
		serviceA.method1();
		ResponseJson response = new ResponseJson();
		response.setStatus(StatusCode.OPERATE_SUCCESS);
		response.addData("hello", "wuqiang");
		return response;
	}
	
	
	@ResponseBody
	@RequestMapping("/happy")
	public ResponseJson happy() {
		serviceA.method2();
		ResponseJson response = new ResponseJson();
		response.setStatus(StatusCode.OPERATE_SUCCESS);
		response.addData("happy", "wuqiang");
		return response;
	}
}
