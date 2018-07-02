package com.huabang.ofo.utils.alipay.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;

public class AliPayUtil {
	public static String URL = "https://openapi.alipay.com/gateway.do"; //支付网关测试环境
	public static  String APP_ID = "2016091500517988"; //appid
	public static String SellerId = "2088102175759801";  //收款放账户
							//商户的私钥
	public static  String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDG93vdSnYBF9mNLiSvemmf/y4meDu0+HbPAdyoy1CyNjDqkHngaWB3ma7K7jp9Ij88kSymCbfEezakK9dIDsIoNSkSwuHFOaH60HJU4tROYXmNYMJP9K5Gs4PNXAK06l9j2eH43RYUm3RkInO7k6ELZfuBwtNm6163dYQ6appFzYyJOFCOHsAGNWH2BXCUrMSOCfDr/CUvXt6vv4Vauui5pIGRXzIPPxXmoymfTFuEg2MlU0L78DLqaFPXFSpPLzd4fav/WFKshATodJaR+ZX3G4eX75/HjqUOYsIjXwTtNrLgT4CvJyez3xhpl/2+GiRsV6uVVumdDSW3eQcL5sztAgMBAAECggEAWKig3hu294e8HkfoevUOKghYdTXwTRTGRDntLJ3kaHTzWSJl3JS4MrMdizmagHYJPaDnB1mfNIM+9uq+NFK3O5lsovqKBgtHXW8S8zZcKk0PD+uEyoIaQlDts02Du0AQn3rQUceadjZLJ++eKICBK4YXC1xYPS02eIN6F1m3QtCqfSI70L/m8/LrE1y+DK/TJxr2ciLW0m5uYa+tKwvh3QIgpDQEWTDJ2cU0G9Jg3Tp3ERm2/g8NZrfnShUbFp+h7rW/a1FdVs5AWt/eTnxQl/bs0BQg1jb9zTIJMjzPYUm2nRxkAWrib1V/8R4nXM0V072mPiHmWbz+tuMRu9ZsdQKBgQDwXGGeY0712NlgxfPmTIcHq5ZuTqBoD/1seJlX0BOYNlKdOw+rIu7HXchkG40oCc9tm3yWkvOI/UN4c1QoEv0C8yXndnUK9o4aFBO7k9USsln2V9+rcIySOM49a4k+GYr91trGuxiFVnQ4sZyWrLzXzbeY2ZXmZVdhA9NAJ/Gi1wKBgQDT6Zzo0ySmPukWh/OVFjmRhMh58+ndq1BK0+f92NbNStbVtBPBDNOQJRXx0aOmz6mNEP+DteSEfyANk4+7d8GbXzip/XIGBiPcEy+13Vw67WDuNQ1uNrKC3W+k7nvP8V6wIR/uAAYmWyOKbTtLnwXOkBhxPDaCwrRIbI4ue1KZ2wKBgAwLVQjuyQIgv6PNx47+vrVvr7YPUpJkWrqYwHZaAuFZt4IRd5OHyJdM58G8b7JV64L+i5uv+Wwf5MWgYdMwQGVpcbjLgsnt2OkNoH+BxOeO8FsbxCV/GkwpdIw3W0U1WnWl0oiqz2u/DUvZ+pLCTSQzNsP+AzjsUxiVbHyGg+E5AoGAPl7e0OP8QHX5sqj292VgMkVLoCE7LwQSEMQH3v3oUun5C1MCEWrOBfn0WIUkTp+GPr/HBKQPizPDYluRDVM1B57iFZ5D5iP+f/KhszxgvfNJ+TJiLpMovT2hc4DBrjVTcjF9rVYSeU2Py1lU/2/hjKd6mjFntdrKpxpUSrf/WssCgYEAswAaHFGkJxzjTQD6FCOZ2m8OzCQT7/RZv+Y/QZrQXTxUgxXduQjsXFoCQbzn1gLdsHZT7/BJIvjMVGHXQtmkZt5nAp2Wh9+uEIXA3NLaxLa9yMN7e36Cww7oG1HEAfxAgupAMzmz240ePqJi/QwfJqoPfQ5uNleeg58fydp2F/A=";
	public static  String FORMAT = "json";
	public static  String CHARSET = "utf-8";
							//支付宝公钥
	public static  String ALIPAY_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	public static  String SIGN_TYPE = "RSA2";
	
	/*private ALiPayService aLiPayService;

	public AliPayUtil(ALiPayService aLiPayService) {
		this.aLiPayService = aLiPayService;
	}
*/
	private AlipayClient alipayClient = new DefaultAlipayClient(URL, APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET,
			ALIPAY_PUBLIC_KEY, SIGN_TYPE);

	@PostMapping("/pay")
	@ResponseBody
	public String pay(HttpServletRequest req){ 
		//进入支付
		System.err.println("进入支付，服务器端封装订单信息，返回客户端吊起支付");
		/**
		 * {
		 * 		"timeout_express":"30m",
		 * 		"seller_id":"",
		 * 		"product_code":"QUICK_MSECURITY_PAY",
		 * 		"total_amount":"0.02",
		 * 		"subject":"1",
		 * 		"body":"我是测试数据",
		 * 		"out_trade_no":"314VYGIAGG7ZOYY"
		 * }
		 */
		// 实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
		// SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		model.setBody("我是测试数据");
		model.setSubject("App支付测试Java");
		// 请保证OutTradeNo值每次保证唯一
		model.setOutTradeNo(System.currentTimeMillis()+"");
		model.setTimeoutExpress("30m");
		model.setSellerId(SellerId);
		model.setTotalAmount("0.01");
		model.setProductCode("QUICK_MSECURITY_PAY");
		request.setBizModel(model);
		request.setNotifyUrl("http://10.103.2.147:8080/distribution/app_alipayCallBack");
		// 这里和普通的接口调用不同，使用的是sdkExecute
		try {
			/**
			 * 保存订单信息
			 */
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			/*PayOrder order = new PayOrder();
			order.setPayId(System.currentTimeMillis()+"");
			order.setPayCreatetime(format2.format(new Date()));
			order.setPayMoney(req.getParameter("money"));//单位元
			order.setPayTitle(req.getParameter("title"));
			order.setPayType("1");
			order.setPayUserId(req.getParameter("userId"));
			this.aLiPayService.saveOrder(order);*/
			AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
			String body = response.getBody();
			return body;
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return "";
	}
}
