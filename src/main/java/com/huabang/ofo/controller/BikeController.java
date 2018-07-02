package com.huabang.ofo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huabang.ofo.service.BikeService;

@Controller
@RequestMapping("/bike")
@ResponseBody
public class BikeController {

	@Autowired
	private BikeService bikeServiceImpl;
	
	/**
	 * 开始用车
	 * @param shareId
	 * @param telePhone
	 * @param lat
	 * @param lng
	 * @return
	 */
	@PostMapping("/start")
	public String useBike(@RequestParam Map<String,String> map){
		String shareId = (String)map.get("shareId");
		String telePhone = (String)map.get("telePhone");
		/*String lat = (String)map.get("lat");
		String lng = (String)map.get("lng");*/
		
		
		/*String pot = lat + lng;*/
		String result = this.bikeServiceImpl.useBike(shareId,telePhone);
		return result;
	}
	/**
	 * 结束用车
	 * @param shareId
	 * @param telePhone
	 * @param lat
	 * @param lng
	 * @return
	 */
	@PostMapping("/end")
	public String endUseBike(@RequestParam Map<String,String> map){
		String shareId = (String)map.get("shareId");
		String lat = (String)map.get("lat");
		String lng = (String)map.get("lng");
		String pot = lat +","+ lng;
		String result = this.bikeServiceImpl.endUseBike(shareId,pot);
		return result;
	}
	
}
