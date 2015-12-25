package com.tsu;

import java.math.BigInteger;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;

import com.util.HttpUtil;
import com.util.MyMd5;
import com.util.RSAutil;
import com.util.StringUtil;

public class UserOperate {

	//static final String actionURL = "http://202.205.16.26/connect/ws/ws_action.jsp";
	//static  final String LoginUrl = "http://202.205.16.26/connect/ws/ws_login.jsp";
	static  final String LoginUrl = "http://account.cuc.edu.cn/connect/ws/ws_login.jsp";
	static final String actionURL = "http://account.cuc.edu.cn/connect/ws/ws_action.jsp";
	static final String TAG = "UserOperate";
	private static String CurrenKeyhash = "";
	private static String passwd = "";
	static final String AllLink = "校园网、互联网已连接";
	static final String CamLink = "校园网已连接，互联网未连接";
	static final String AllUnLink = "校园网未连接，互联网未连接";
	
	public static String login(String username,String password) throws Exception
	{
		// to do get current time
		String base64Password = "";
		byte[] encPassword = new byte[]{0} ;
		String module ="";
		String empoent ="";
		passwd = password;
		BigInteger BigIntegerModule = new BigInteger("0");
		BigInteger BigIntegerEmpoent = new BigInteger("0");
		JSONObject jobj = null;
		
		try {
				jobj = HttpPostRequest.getHttpRequestJSONObject(LoginUrl,"1");
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("HttpPostRequest", e.getMessage());
		}
		 
		// 使用服务器返回的JSON对象取得需要的公钥参数
		try {
			module = jobj.getString("module");
			empoent = jobj.getString("empoent");
		} catch (JSONException e) {
			e.printStackTrace();
			Log.v("JSONgetString", e.getMessage());
		}
		
	    BigIntegerModule = new BigInteger(RSAutil.toDecString(module));
		BigIntegerEmpoent = new BigInteger(RSAutil.toDecString(empoent));
		
		// 使用RSA算法的公钥对输入的密码加密
		try {
			
			  encPassword = RSAutil.encrypt(RSAutil.generatePublicKey(BigIntegerModule,BigIntegerEmpoent),password.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("RSA", e.getMessage());
		}
		Log.v("The data after RSA",StringUtil.toHexString(encPassword));
		
		//RSA加密后的字符串不适合直接通过http传输，所以用base64进行一次转码再传输
		base64Password = Base64.encodeToString(encPassword, Base64.DEFAULT);
		Log.v("The data after Base64",StringUtil.toHexString(base64Password.getBytes()));
		String retMessage = "";
		String ts = "";
		String passwdAndTs = "";
		
  		try {
			jobj = HttpUtil.getJSONObject(LoginUrl,"2",username,base64Password,"true");
			retMessage = jobj.getString("success");
			ts = jobj.getString("ts");
			passwdAndTs = passwd + ts;
			CurrenKeyhash = MyMd5.hashMD5(passwdAndTs);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
  		
  		//认真成功
  		if(retMessage.equals("true")){
  			if(checkStatus() == 1){ // 如果校园网没有连接，先连接校园网，再连互联网
  				return connectCampusAndInternet();
  			}
  			else{
  				return connectInternet();
  			}
  		}
  		else{
  			return jobj.getString("msg");
  		}
  	
	}
	
	public static String connectCampus() throws Exception{
		 String success = ""; 
		 String ts = "";
		 String passwdAndTs = "";
		 JSONObject jobj = null;
		 jobj =  HttpUtil.getJSONObject(actionURL,"2","true",CurrenKeyhash);
		 success = jobj.getString("success");
		 ts = jobj.getString("ts");
		 passwdAndTs = passwd + ts;
		 if(success.equals("true")){
			 CurrenKeyhash = MyMd5.hashMD5(passwdAndTs);
			 return "true";
			 }
		 else{
			 return jobj.getString("msg");
		 }
	}
	
	public static String connectCampusAndInternet() throws Exception{
		 String success = ""; 
		 String ts = "";
		 String passwdAndTs = "";
		 JSONObject jobj = null;
		 jobj =  HttpUtil.getJSONObject(actionURL,"2","true",CurrenKeyhash);
		 success = jobj.getString("success");
		 ts = jobj.getString("ts");
		 passwdAndTs = passwd + ts;
		 if(success.equals("true")){
			 CurrenKeyhash = MyMd5.hashMD5(passwdAndTs);
			 return connectInternet();
			 }
		 else{
			 return jobj.getString("msg");
		 }
	}
	public static void disconnectCampus() throws Exception
	{
		 String success = ""; 
		 String ts = "";
		 String passwdAndTs = "";
		 JSONObject jobj = null;
		 jobj =  HttpUtil.getJSONObject(actionURL,"3","true",CurrenKeyhash);
		 success = jobj.getString("success");
		 ts = jobj.getString("ts");
		 passwdAndTs = passwd + ts;
		 if(success.equals("true")){
		 CurrenKeyhash = MyMd5.hashMD5(passwdAndTs);
		 }
	}
		 
	
	public static String connectInternet() throws Exception
	{
		 String msg= "";
		 String success = ""; 
		 String ts = "";
		 String passwdAndTs = "";
		 JSONObject jobj = null;
		 jobj =  HttpUtil.getJSONObject(actionURL,"4","true",CurrenKeyhash);
		 success = jobj.getString("success");
		 ts = jobj.getString("ts");
		 passwdAndTs = passwd + ts;
		 if(success.equals("true")){
			 CurrenKeyhash = MyMd5.hashMD5(passwdAndTs);
			 return "true";
			 }
		 else{
			 Log.v("互联网连接错误：", msg);
			 return jobj.getString("msg");
		 }
	}
	
	public static String disconnectInternet() throws Exception
	{
		 String success = ""; 
		 String ts = "";
		 String passwdAndTs = "";
		 JSONObject jobj = null;
		 jobj =  HttpUtil.getJSONObject(actionURL,"5","true",CurrenKeyhash);
		 success = jobj.getString("success");
		 ts = jobj.getString("ts");
		 passwdAndTs = passwd + ts;
		 if(success.equals("true")){
			 CurrenKeyhash = MyMd5.hashMD5(passwdAndTs);
			 return "true";
			 }
		 else{
			 Log.v(TAG, "断开互联网请求发生错误");
			 String errorMSG = jobj.getString("errno");
			 errorMSG = "errorCode:" + errorMSG +"\n" + "msg:" +jobj.getString("msg");
			 return errorMSG;
		 }
	}
	
	
	public static double getLinkInfo() throws Exception
	{
		 String success = ""; 
		 String ts = "";
		 String passwdAndTs = "";
		 JSONObject jobj = null;
		 double allow_flux = 0;
		 jobj =  HttpUtil.getJSONObject(actionURL,"6","true",CurrenKeyhash);
		 success = jobj.getString("success");
		 ts = jobj.getString("ts");
		 passwdAndTs = passwd + ts;
		 if(success.equals("true")){
			 CurrenKeyhash = MyMd5.hashMD5(passwdAndTs);
			 String keeperStr = jobj.getString("keeper");
			 JSONObject jo = new JSONObject(keeperStr);
			 allow_flux = jo.getDouble("allow_flux");
			 return allow_flux;
			 }
		 else{
			 Log.v(TAG, "act=6  保持连接请求发生错误");
			 return -100;
		 }
	}
	
	public static int  checkStatus() throws Exception
	{
		 boolean Campus = false;
		 boolean Internet = false; 
		 boolean Success = false;
		 JSONObject jobj = null;
		 jobj =  HttpUtil.getJSONObject(actionURL,"1");
		 Success = jobj.getBoolean("success");
		 Campus = jobj.getBoolean("campus");
		 Internet = jobj.getBoolean("internet");
		 if(Success&&Campus&&Internet){
			 return 3;
		 }
		 else if(Success&&!Campus&&!Internet){
			 return 1;
		 }
		 else if(Success&&Campus&&!Internet) // 校园网已连接，互联网未连接
		 {
			 return 2;
		 }
		 else{
			 return 0;
		 }
		//todo show result	
	}
	
	public static String  getUserIp() throws Exception
	{
		 String ip = "";
		 String success = ""; 
		 String ts = "";
		 String passwdAndTs = "";
		 JSONObject jobj = null;
		 jobj =  HttpUtil.getJSONObject(actionURL,"7","true",CurrenKeyhash);
		 success = jobj.getString("success");
		 ts = jobj.getString("ts");
		 passwdAndTs = passwd + ts;
		 if(success.equals("true")){
			 CurrenKeyhash = MyMd5.hashMD5(passwdAndTs);
			 String kps = jobj.getString("kpips");
			 ip = HttpUtil.getUserIpByJSONArray(kps);
			 return ip;
			 }
		 else{
			 Log.v(TAG, "act=7 获取用户在线ip错误");
			 return jobj.getString("msg");
		 }
		 
	}
	
	public static String kickIP(String ip) throws Exception{
		String success = ""; 
		 String ts = "";
		 String passwdAndTs = "";
		 JSONObject jobj = null;
		 jobj =  HttpUtil.getJSONObject(actionURL,"8","true",CurrenKeyhash,ip);
		 success = jobj.getString("success");
		 ts = jobj.getString("ts");
		 passwdAndTs = passwd + ts;
		 if(success.equals("true")){
			 CurrenKeyhash = MyMd5.hashMD5(passwdAndTs);
			 return "true";
			 }
		 else{
			 Log.v(TAG, "act=9 获取用户在线ip错误");
			 return jobj.getString("msg");
		 }
	}
	
	public static String getMobileModle(){
		return android.os.Build.MODEL;
	}
	
	public static String getMobileFactory(){
		return android.os.Build.MANUFACTURER;
	}

}
