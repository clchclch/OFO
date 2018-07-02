package com.huabang.ofo.controller;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.huabang.ofo.domain.HbAccount;
import com.huabang.ofo.service.UsersService;
import com.huabang.ofo.utils.FileUtil;
import com.huabang.ofo.utils.SendMsgUtils2;

@Controller
@RequestMapping("/user")
public class UsersController{

	@Autowired
	private UsersService userServiceImpl;
	/**
	 * 发送验证码
	 * 
	 * @param Phone
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/send")
	@ResponseBody
	public String customer_sendMsg(@RequestParam Map<String,String> map) throws Exception {
		JSONObject jsonObject = new JSONObject();
		String telephone = (String)map.get("telephone");
		if(telephone == null || "".equals(telephone)){
			jsonObject.put("msg", "请输入手机号");
			return jsonObject.toJSONString();
		} 
		String regex = "^1[3|4|5|7|8][0-9]\\d{8}$";
		Pattern p = Pattern.compile(regex);  
		Matcher m = p.matcher(telephone);  
		if(!m.matches()){
			jsonObject.put("msg", "对不起，输入的手机号有误");
			return jsonObject.toJSONString();
		}
		String checkcode = RandomStringUtils.randomNumeric(4);
		try {
//			SendSmsResponse sms = SendMsgUtils.sendSms(telephone, checkcode);
			Boolean sms = SendMsgUtils2.sendSms(telephone, checkcode);
//			if(sms){
//				jsonObject.put("msg", "发送失败，请重试");
//				return jsonObject.toJSONString();
//			}
			jsonObject.put("msg", "发送成功，请注意查收");
			jsonObject.put("code", checkcode);
			return jsonObject.toJSONString();
		} catch (Exception e) {
			jsonObject.put("msg", "发送失败，请重试");
			return jsonObject.toJSONString();
		}
	}

	/**
	 * 用户登陆/注册
	 * 
	 * @param telephone
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public String login(@RequestParam Map<String,String> map) {
		JSONObject jsonObject = new JSONObject();
		String telephone = (String)map.get("telephone");
		String code = (String)map.get("code");
		if(code == null || "".equals(code)){
			jsonObject.put("result", "请输入验证码");
			return jsonObject.toJSONString();
		}
//		if(!session.getAttribute("code").equals(code)){
//			jsonObject.put("result", "验证码错误，请重新输入");
//			return jsonObject.toJSONString();
//		}
		Map<String, Object> result = this.userServiceImpl.login(telephone);
		jsonObject.put("result", result);
		return jsonObject.toJSONString();
	}
	
	/**
	 * 查看用户的基本信息
	 * @param telephone
	 * @return
	 */
	@PostMapping("/selectUser")
	@ResponseBody
	public String selectUser(@RequestParam Map<String,String> map){
		String telephone = (String)map.get("telephone");
		String result = this.userServiceImpl.selectUser(telephone);
		return result;
	}
	
	/**
	 * 我  页面的数据
	 * @param telephone
	 * @return
	 */
	@PostMapping("/selectUserMe")
	@ResponseBody
	public String userMe(@RequestParam Map<String,String> map){
		String telephone = (String)map.get("telephone");
		String result = this.userServiceImpl.selectUserMe(telephone);
		return result;
	}

	/**
	 * 上传图片
	 * 
	 * @param file
	 * @param request
	 * @return 
	 * @return
	 */
	@ResponseBody
	@PostMapping(value = "/upload")
	public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request,@RequestParam Map<String,String> map) {
		String path = request.getSession().getServletContext().getRealPath("src/main/resources/static/file/image/");
		String telephone = (String)map.get("telephone");
		Integer type = Integer.parseInt(map.get("type"));
		String fileName = file.getOriginalFilename();
		fileName = FileUtil.renameToUUID(fileName).replace("-", "");
		try {
			FileUtil.uploadFile(file.getBytes(), path, fileName);
			//保存数据库
			String image = this.userServiceImpl.savaImage(type,fileName,telephone);
			JSONObject object = new JSONObject();
			object.put("imageUrl", image);
			return object.toJSONString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*JSONString jsonString = new JSONString();
		jsonString.put("msg", fileName);
		return jsonString.toJSONString();*/
		return null;
	}

	/**
	 * 余额查询
	 * @param telephone
	 * @return
	 */
	@PostMapping("/money")
	@ResponseBody
	public String selectYuE(@RequestParam Map<String,String> map){
		String telephone = (String)map.get("telephone");
		HbAccount hbAccount = this.userServiceImpl.selectMoney(telephone);
		JSONObject object = new JSONObject();
		object.put("list", hbAccount);
		return object.toJSONString();
	}
	
	/**
	 * 修改用户基本信息
	 * @param telePhone
	 * @param name
	 * @return
	 */
	@PostMapping("/name")
	@ResponseBody
	public String updateName(@RequestParam Map<String,String> map){
		JSONObject jsonObject = new JSONObject();
		String telePhone = (String)map.get("telePhone");
		String value = (String)map.get("value");
		Integer type = Integer.parseInt(map.get("type"));
		int result = this.userServiceImpl.updateByName(telePhone,value,type);
		if(result>0){
			jsonObject.put("result", "修改成功");
			return jsonObject.toJSONString();
		}
		jsonObject.put("result", "修改失败");
		return jsonObject.toJSONString();
	}
	/**
	 * 更换手机号
	 * @param oldPhone
	 * @param newPhone
	 * @return
	 */
	@PostMapping("/phone")
	@ResponseBody
	public String updatePhone(@RequestParam Map<String,String> map){
		JSONObject jsonObject = new JSONObject();
		String oldPhone = (String)map.get("oldPhone");
		String newPhone = (String)map.get("newPhone");
		int result = this.userServiceImpl.updateByPhone(oldPhone,newPhone);
		if(result>0){
			jsonObject.put("result", "ok");
			return jsonObject.toJSONString();
		}
		jsonObject.put("result", "error");
		return jsonObject.toJSONString();
	}
	/**
	 * 认证/缴纳押金
	 * @param telephone
	 * @param money
	 * @param type
	 * @return
	 */
	@PostMapping("/renZheng")
	@ResponseBody
	public String renZheng(@RequestParam Map<String,String> map){
		String cashMoney = (String)map.get("cashMoney");
		String telephone = (String)map.get("telephone");
		Integer money = Integer.parseInt(map.get("money"));
		Integer type = Integer.parseInt(map.get("type"));
		String result = this.userServiceImpl.renZheng(telephone,cashMoney,type,money);
		return result;
	}
	/**
	 * 充值
	 * @param telephone
	 * @param money
	 * @return
	 */
	@PostMapping("/pay")
	@ResponseBody
	public String pay(@RequestParam Map<String,String> map){
		String telephone = (String)map.get("telephone");
		String money = (String)map.get("money");
		String result = this.userServiceImpl.pay(telephone,money);
		return result;
	}
	
	/**
	 * 我的钱包
	 */
	@PostMapping("/selectMyMoney")
	@ResponseBody
	public String select(@RequestParam Map<String,String> map){
		String userId = map.get("userId");
		String result = this.userServiceImpl.selectMyMoney(userId);
		return result;
	}
	
	
	
	
}
