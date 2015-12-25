package com.tsu;

import org.json.JSONObject;

import com.util.HttpUtil;

public class HttpPostRequest {

	public static JSONObject 	getHttpRequestJSONObject(String url,String arg) throws Exception{
		
		return HttpUtil.getJSONObject(url, arg);
	}
}
