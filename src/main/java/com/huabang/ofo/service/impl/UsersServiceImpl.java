package com.huabang.ofo.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huabang.ofo.controller.Tencent;
import com.huabang.ofo.dao.HbAccountsMapper;
import com.huabang.ofo.dao.HbImagesMapper;
import com.huabang.ofo.dao.HbOrdersMapper;
import com.huabang.ofo.dao.HbUserCashsMapper;
import com.huabang.ofo.dao.HbUsersMapper;
import com.huabang.ofo.domain.HbAccount;
import com.huabang.ofo.domain.HbImage;
import com.huabang.ofo.domain.HbOrder;
import com.huabang.ofo.domain.HbUser;
import com.huabang.ofo.domain.HbUserCash;
import com.huabang.ofo.service.UsersService;

@Service
@Transactional(propagation=Propagation.REQUIRED)
public class UsersServiceImpl implements UsersService,Tencent {

	@Autowired
	private HbUsersMapper hbUserMapper;
	@Autowired
	private HbUserCashsMapper hbUserCashMapper;
	@Autowired
	private HbAccountsMapper hbAccountMapper;
	@Autowired
	private HbImagesMapper hbImageMapper;
	@Autowired
	private HbOrdersMapper hbOrderMapper;
	
	@Override
	public Map<String,Object> login(String telephone) {
		Map<String,Object> map = new HashMap<>();
		//查询该手机号用户是否已经注册过
		HbUser user = this.hbUserMapper.selectByPhone(telephone);
		//注册过
		if(user!=null){
			map.put("userId", user.getUserId());
			map.put("userPhone", user.getUserPhone());
			//查询用户是否已经缴纳押金
			HbUserCash userCash = this.hbUserCashMapper.selectByPrimaryKey(user.getUserId());
			if(userCash==null){
				map.put("msg", "no");
				//跳到交押金页面
				return map; 
			}else{
				map.put("msg", "yes");
				//跳到用车页面
				return map;
			}
		}
		//没有注册过,注释掉的有默认值
		HbUser hbUser = new HbUser();
		hbUser.setUserId(UUID.randomUUID().toString().replace("-", ""));
		hbUser.setUserPhone(telephone);
		hbUser.setUserName(telephone);
//		hbUser.setUserXyapprove(0);  信誉分
//		hbUser.setUserBirthday(null); 生日
//		hbUser.setUserSex(null);  性别
		hbUser.setUserImage(imageUrl+"default.jpg");
//		hbUser.setUserApprove(0);
//		hbUser.setUserStatus(0);
//		hbUser.setUserWeixin(null);
//		hbUser.setUserQq(null);
//		hbUser.setUserInvite(null);
		//保存用户的基本信息
		this.hbUserMapper.insertSelective(hbUser);
		HbAccount account = new HbAccount();
		account.setAccountUserId(hbUser.getUserId());
		account.setAccountPay(0.0);
		account.setAccountPresented(0.0);
		account.setAccountTotel("0.00");
		//保存用户的初始账户余额
		this.hbAccountMapper.insertSelective(account);
		map.put("userId", hbUser.getUserId());
		map.put("userPhone", hbUser.getUserPhone());
		map.put("msg", "no");
		return map;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public HbAccount selectMoney(String telephone) {
		HbAccount hb = this.hbAccountMapper.selectMoney(telephone);
		return hb;
	}

	@Override
	public String savaImage(Integer type, String fileName, String telephone) {
		HbUser user = null;
		//头像
		if(type==0){
			//用户头像
			user = this.hbUserMapper.selectByPhone(telephone);
			user.setUserImage(imageUrl+fileName);
			this.hbUserMapper.updateByPrimaryKeySelective(user);
		}
		HbImage image = new HbImage();
		image.setImagePic(imageUrl+fileName);
		image.setImageType(type+"");
		if(type==0){
			image.setImageUserId(user.getUserId());
		}
		image.setImageUserId(null);
		this.hbImageMapper.insertSelective(image);
		return image.getImagePic();
	}

	@Override
	public int updateByName(String telePhone, String value,int type) {
		HbUser user = this.hbUserMapper.selectByPhone(telePhone);
		//修改昵称
		if(type==0){
			user.setUserName(value);
		}else if(type==1){
			//修改性别
			if("男".equals(value)){
				user.setUserSex("0");
			}else{
				user.setUserSex("1");
			}
		}else if(type==2){
			//修改生日
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try {
				date = format.parse(value);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			user.setUserBirthday(date);
		}else{
			return 0;
		}
		int i = this.hbUserMapper.updateByPrimaryKeySelective(user);
		return i;
	}

	@Override
	public int updateByPhone(String oldPhone, String newPhone) {
		HbUser user = this.hbUserMapper.selectByPhone(oldPhone);
		user.setUserPhone(newPhone);
		int i = this.hbUserMapper.updateByPrimaryKeySelective(user);
		return i;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public String selectUser(String telephone) {
		HbUser user = this.hbUserMapper.selectByPhone(telephone);
		JSONObject object = new JSONObject();
		if(user.getUserApprove() == 0){
			object.put("renzheng", "未认证");
		}else{
			object.put("renzheng", "已认证");
		}
		if(user.getUserSex() == null || user.getUserSex().equals("")){
			user.setUserSex("未设置");
		}else{
			if(user.getUserSex().equals("0")){
				user.setUserSex("男");
			}else{
				user.setUserSex("女");
			}
		}
		object.put("list", user);
		return object.toJSONString();
	}

	@Override
	public String renZheng(String telephone, String cashMoney, int type,Integer money) {
		HbUser user = this.hbUserMapper.selectByPhone(telephone);
		//押金保存
		HbUserCash hbUserCash = new HbUserCash();
		hbUserCash.setUserId(user.getUserId());
		hbUserCash.setUserCash(Double.parseDouble(cashMoney));
		hbUserCash.setUserCashType(type);
		//缴纳押金并充值余额
		if(money!=0){
			HbAccount account = this.hbAccountMapper.selectByUserId(user.getUserId());
			account.setAccountTotel(String.valueOf(Double.parseDouble(account.getAccountTotel()+money)));
			account.setAccountPay(Double.parseDouble(account.getAccountTotel()));
			this.hbAccountMapper.updateByPrimaryKeySelective(account);
		}
		int i = this.hbUserCashMapper.insertSelective(hbUserCash);
		//订单保存
		HbOrder order = new HbOrder();
		order.setOrderId(UUID.randomUUID().toString().replace("-", ""));
		order.setOrderPrice(Double.parseDouble(cashMoney)+money);
		order.setOrderUserid(user.getUserId());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			order.setOrderCreatetime(format.parse(format.format(new Date())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		order.setOrderType(0);
		int j = this.hbOrderMapper.insertSelective(order);
		user.setUserApprove(1);
		//修改用户的认证
		this.hbUserMapper.updateByPrimaryKeySelective(user);
		JSONObject object = new JSONObject();
		if(i>0&&j>0){
			object.put("result", "ok");
		}else{
			object.put("result", "error");
		}
		return object.toJSONString();
	}

	@Override
	public String pay(String telephone, String money) {
		//余额
		HbUser user = this.hbUserMapper.selectByPhone(telephone);
		HbAccount account = this.hbAccountMapper.selectByUserId(user.getUserId());
		account.setAccountTotel(String.valueOf(Double.parseDouble(account.getAccountTotel()+Double.parseDouble(money))));
		account.setAccountPay(Double.parseDouble(account.getAccountTotel()));
		int i = this.hbAccountMapper.updateByPrimaryKeySelective(account);
		//订单
		HbOrder order = new HbOrder();
		order.setOrderId(UUID.randomUUID().toString().replace("-", ""));
		order.setOrderPrice(Double.parseDouble(money));
		order.setOrderUserid(user.getUserId());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			order.setOrderCreatetime(format.parse(format.format(new Date())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		order.setOrderType(1);
		int j = this.hbOrderMapper.insertSelective(order);
		JSONObject object = new JSONObject();
		if(i>0&&j>0){
			object.put("result", "ok");
		}else{
			object.put("result", "error");
		}
		return object.toJSONString();
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public String selectUserMe(String telephone) {
		JSONObject object = new JSONObject();
		HbUser user = this.hbUserMapper.selectByPhone(telephone);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("phone", telephone);
		if(user.getUserApprove() == 0){
			object.put("renzheng", "未认证");
		}else{
			object.put("renzheng", "已认证");
		}
		map.put("xyapprove", user.getUserXyapprove());
		map.put("imageUrl", user.getUserImage());
		object.put("list", map);
		return object.toJSONString();
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public String selectMyMoney(String userId) {
		HbAccount account = this.hbAccountMapper.selectByUserId(userId);
		HbUserCash cash = this.hbUserCashMapper.selectByPrimaryKey(userId);
		JSONObject object = new JSONObject();
		if(cash == null){
			object.put("yajin", 0);
		}else{
			object.put("yajin", cash.getUserCash());
		}
		object.put("money", account.getAccountTotel());
		return object.toJSONString();
	}

}
