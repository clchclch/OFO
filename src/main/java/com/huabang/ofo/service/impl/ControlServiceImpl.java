package com.huabang.ofo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huabang.ofo.dao.HbCashmoneysMapper;
import com.huabang.ofo.dao.HbPaymoneysMapper;
import com.huabang.ofo.domain.HbCashmoney;
import com.huabang.ofo.domain.HbPaymoney;
import com.huabang.ofo.service.ControlService;

@Service
@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
public class ControlServiceImpl implements ControlService {

	@Autowired
	private HbCashmoneysMapper hbCashMoneyMapper;
	@Autowired
	private HbPaymoneysMapper hbPayMoneyMapper;
	
	@Override
	public String selectCashAll() {
		List<HbCashmoney> list = this.hbCashMoneyMapper.selectByExample(null);
		JSONObject object = new JSONObject();
		object.put("list", list);
		return object.toJSONString();
	}
	
	@Override
	public String selectPayAll() {
		List<HbPaymoney> list = this.hbPayMoneyMapper.selectByExample(null);
		JSONObject object = new JSONObject();
		object.put("list", list);
		return object.toJSONString();
	}

}
