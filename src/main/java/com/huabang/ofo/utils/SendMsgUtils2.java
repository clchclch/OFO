package com.huabang.ofo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.security.MessageDigest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.aliyuncs.exceptions.ClientException;

import org.apache.http.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.Cipher;

public class SendMsgUtils2 {


	public static Boolean sendSms(String telephone, String checkcode) throws ClientException {
		String url = "http://120.55.197.77:1210/Services/MsgSend.asmx/SendMsg";
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("userCode", "BYMCF"));
		nvps.add(new BasicNameValuePair("userPass", "@s74#l!f_A"));
		nvps.add(new BasicNameValuePair("DesNo", telephone));//"+checkcode+"
		nvps.add(new BasicNameValuePair("Msg", "尊敬的用户：您正在使用手机注册登陆，验证码为："+checkcode+"【鸡洋洋】"));
		nvps.add(new BasicNameValuePair("Channel", "0"));
		String post = httpPost(url, nvps); // post请求
		System.err.println("返回内容为：："+post);
		Boolean concat = post.contains("-");
//		String getparam = "userCode=用户名&userPass=密码&DesNo=手机号&Msg=短信内容【签名】&Channel=0";
//		String result = httpGet(url, getparam); // get请求
		return concat;
	}

	public static String httpPost(String url, List<NameValuePair> params) {
		String result = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instreams = entity.getContent();
				result = convertStreamToString(instreams);
				System.out.println(result);
			}
		} catch (Exception e) {
		}
		return result;
	}

	public static String httpGet(String url, String params) {
		String result = "";
		try {
			HttpClient client = new DefaultHttpClient();
			if (params != "") {
				url = url + "?" + params;
			}
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = client.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instreams = entity.getContent();
				result = convertStreamToString(instreams);
				System.out.println(result);
			}
		} catch (Exception e) {
		}
		return result;
	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
