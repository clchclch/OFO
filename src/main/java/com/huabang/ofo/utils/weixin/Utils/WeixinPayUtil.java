package com.huabang.ofo.utils.weixin.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.huabang.ofo.utils.weixin.entity.Unifiedorder;
import com.huabang.ofo.utils.weixin.entity.WeiXinConfig;
import com.huabang.ofo.utils.weixin.entity.Weiwinreturn;


 
/**
 * 微信支付测试
 * @author xiebin
 * @date 2015年11月26日上午9:58:19
 */
public class WeixinPayUtil {

	public static void main(String[] args) throws DocumentException{
		perpay(null, "213131");
	}
	
	/**
	 * 预支付
	 * @param req
	 * @param out_trade_no
	 * @return
	 * @throws DocumentException
	 */
	public static Object perpay(HttpServletRequest req, String out_trade_no) throws DocumentException {
		String ip=getIpAddress();
		Map rMap=new HashMap();
		Weiwinreturn weixinreturn = new Weiwinreturn();
		//参数组
		String appid =WeiXinConfig.appid;
		String mch_id =WeiXinConfig.mch_id;
		String nonce_str = RandCharsUtils.getRandomString(16);  //随机字符串
		String body = req.getParameter("body");
		
		String total_fee=req.getParameter("total_fee") ;   //单位是分  
		String spbill_create_ip =ip;  
		String notify_url = WeiXinConfig.notify_url;
		String trade_type = "APP";
		
		
		//参数：开始生成签名
		SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
		parameters.put("appid", appid);
		parameters.put("mch_id", mch_id);
		parameters.put("nonce_str", nonce_str);
		parameters.put("body", body);
		parameters.put("out_trade_no", out_trade_no);
		parameters.put("total_fee", total_fee);
		parameters.put("spbill_create_ip", spbill_create_ip);
		parameters.put("notify_url", notify_url);
		parameters.put("trade_type", trade_type);
		String sign = WXSignUtils.createSign("UTF-8", parameters);

		Unifiedorder unifiedorder = new Unifiedorder();
		unifiedorder.setAppid(appid);
		unifiedorder.setMch_id(mch_id);
		unifiedorder.setNonce_str(nonce_str);
		unifiedorder.setSign(sign);
		unifiedorder.setBody(body);
		unifiedorder.setOut_trade_no(out_trade_no);
		unifiedorder.setTotal_fee(total_fee);
		unifiedorder.setSpbill_create_ip(spbill_create_ip);
		unifiedorder.setNotify_url(notify_url);
		unifiedorder.setTrade_type(trade_type);

		
		//构造xml参数
		String xmlInfo = HttpXmlUtils.xmlInfo(unifiedorder);
		System.out.println("统一下单传递的xml字符串为:\n"+xmlInfo);
		String wxUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		String method = "POST";
		String xmlStr = HttpXmlUtils.httpsRequest(wxUrl, method, xmlInfo).toString();
		System.out.println("统一下单返回结果:\n"+xmlStr);
		
		Document doc = DocumentHelper.parseText(xmlStr);
		Element rootElt = doc.getRootElement(); // 获取根节点 
		String return_code=rootElt.elementTextTrim("return_code");
		String return_msg=rootElt.elementTextTrim("return_msg");
		if(return_code.equalsIgnoreCase("SUCCESS")){
			//反校验签名，暂无
			
			String prepay_id=rootElt.elementTextTrim("prepay_id");
			String timestamp = String.valueOf(System.currentTimeMillis()/1000);
			SortedMap rParam=new TreeMap();
			rParam.put("appid", appid);
			rParam.put("partnerid", mch_id);
			rParam.put("prepayid", prepay_id);
			rParam.put("package", "Sign=WXPay");
			rParam.put("noncestr", nonce_str);
			rParam.put("timestamp", timestamp);
			//进行签名，返回给客户端
			String sign2 = WXSignUtils.createSign("UTF-8", rParam);
			rParam.put("sign", sign2);
			rMap.put("code", 1);
			rMap.put("msg", "统一下单成功");
			rMap.put("content", rParam);
			weixinreturn.setCode("1");
			weixinreturn.setContent((TreeMap<String, String>) rParam);
			weixinreturn.setMsg("统一下单成功");
			System.out.println("maptostring:"+rParam.toString());
		}else{
			rMap.put("code", 101);
			rMap.put("msg", "统一下单失败");
			weixinreturn.setCode("101");
			weixinreturn.setMsg("统一下单失败");
		}
		
		return weixinreturn;
	}
	/**
	  * 获取客户端的ip地址
	  * @param request
	  * @return
	  */
	 public static String getIpAddress() {
		 try{
			 HttpServletRequest request =  ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
				String ip = request.getHeader("x-forwarded-for");
				if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
					ip = request.getHeader("Proxy-Client-IP");
				}
				if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
					ip = request.getHeader("WL-Proxy-Client-IP");
				}
				if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
					ip = request.getHeader("HTTP_CLIENT_IP");
				}
				if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
					ip = request.getHeader("HTTP_X_FORWARDED_FOR");
				}
				if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
					ip = request.getRemoteAddr();
				}
				return ip;
		 }catch (Exception e) {
			return null;
		}
	}
}
