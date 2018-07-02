package com.huabang.ofo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huabang.ofo.service.ControlService;

@Controller
@RequestMapping("/control")
@ResponseBody
public class ControlController {

	@Autowired
	private ControlService controlServiceImpl;
	
	/**
	 * 都可以缴纳多少押金
	 * @return
	 */
	@PostMapping("/cash")
	public String cash(){
		String result = this.controlServiceImpl.selectCashAll();
		return result;
	}
	
	/**
	 * 都可以充值多少元
	 * @return
	 */
	@PostMapping("/pay")
	public String pay(){
		String result = this.controlServiceImpl.selectPayAll();
		return result;
	}
	
}
