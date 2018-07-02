package com.huabang.ofo.service.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.huabang.ofo.dao.HbDetailsMapper;
import com.huabang.ofo.dao.HbJourneysMapper;
import com.huabang.ofo.dao.HbSharesMapper;
import com.huabang.ofo.dao.HbUsersMapper;
import com.huabang.ofo.domain.HbDetail;
import com.huabang.ofo.domain.HbJourney;
import com.huabang.ofo.domain.HbShare;
import com.huabang.ofo.domain.HbUser;
import com.huabang.ofo.service.BikeService;
import com.huabang.ofo.utils.LatAndLntUtils;

@Service
@Transactional(propagation=Propagation.REQUIRED)
public class BikeServiceImpl implements BikeService {

	@Autowired
	private HbJourneysMapper hbJourneyMapper;
	@Autowired
	private HbSharesMapper hbShareMapper;
	@Autowired
	private HbUsersMapper hbUserMapper;
	@Autowired
	private HbDetailsMapper hbDetailMapper;
	
	@Override
	public String useBike(String shareId,String telePhone) {
		HbShare share = this.hbShareMapper.selectByPrimaryKey(shareId);
		HbUser user = this.hbUserMapper.selectByPhone(telePhone);
		JSONObject object = new JSONObject();
		if (share == null) {
			object.put("result", "车牌号不正确");
			return object.toJSONString();
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		HbJourney journey = new HbJourney();
		journey.setJourneyId(UUID.randomUUID().toString().replace("-", ""));
		journey.setJourneyShareid(shareId);
		journey.setJourneyUserid(user.getUserId());
		try {
			journey.setJourneyCreatetime(format.parse(format.format(new Date())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		journey.setJourneyStartpot(share.getSharePot());
		int i = this.hbJourneyMapper.insertSelective(journey);
		if(i>0){
			object.put("result", "使用成功");
			return object.toJSONString();
		}
		object.put("result", "开启失败,请重试");
		return object.toJSONString();
	}

	@Override
	public String endUseBike(String shareId,String pot) {
		HbShare share = this.hbShareMapper.selectByPrimaryKey(shareId);
		share.setSharePot(pot);
		this.hbShareMapper.updateByPrimaryKeySelective(share);
		JSONObject object = new JSONObject();
		HbJourney journey = this.hbJourneyMapper.selectByShareId(shareId);
		long startDate = journey.getJourneyCreatetime().getTime();
		long endDate = new Date().getTime();
		long time = endDate-startDate;
		long string = time/1000/60;
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DecimalFormat    df   = new DecimalFormat("######0");
		String minute = df.format(string);
		//60分钟起步，1元。超出每30分钟收费0.6，不足按30分钟
		int j = Integer.parseInt(minute);
//		HbDetail detail1 = this.hbDetailMapper.selectByMinute(60);
		List<HbDetail> lis = this.hbDetailMapper.selectByExample(null);
		HbDetail detail1 = lis.get(0);
		journey.setJourneyTime(j);
		if(j<=detail1.getDetailMinute()){
			journey.setJourneyMoney(detail1.getDetailMoney());
		}else{
			HbDetail detail2 = lis.get(1);
			journey.setJourneyMoney((j-60)/detail2.getDetailMinute()*detail2.getDetailMoney()+detail1.getDetailMoney());
		}
		HbShare s = new HbShare();
		s.setSharePot(journey.getJourneyStartpot());
		String[] split = pot.split(",");
		double d = LatAndLntUtils.latAndLnt(s, split[0], split[1]);
		journey.setJourneyDistance(d);
		journey.setJourneyEndpot(pot);
		try {
			journey.setJourneyEndtime(format2.parse(format2.format(new Date(endDate))));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int i = this.hbJourneyMapper.updateByPrimaryKeySelective(journey);
		if(i>0){
			object.put("result", "ok");
			return object.toJSONString();
		}
		object.put("result", "error");
		return object.toJSONString();
	}

}
