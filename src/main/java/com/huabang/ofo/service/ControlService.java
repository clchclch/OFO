package com.huabang.ofo.service;

public interface ControlService {
	/**
	 * 都可以缴纳多少押金
	 * @return
	 */
	String selectCashAll();
	/**
	 * 都可以充值多少元
	 * @return
	 */
	String selectPayAll();

}
