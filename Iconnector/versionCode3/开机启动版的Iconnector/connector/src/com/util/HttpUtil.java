package com.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;

public class HttpUtil {
	
	public static final String TAG = "HttpUtil";
	public static String getLocalMobileIpAddress() {
		
			StringBuilder IFCONFIG=new StringBuilder();
       try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            		NetworkInterface intf = en.nextElement();
			            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
				                InetAddress inetAddress = enumIpAddr.nextElement();
				                if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
				                	IFCONFIG.append(inetAddress.getHostAddress().toString()+"\n");
				              }
				
			            }
	        }
    } catch (SocketException ex) {
        Log.e("LOG_TAG", ex.toString());
    }
   return IFCONFIG.toString();
		    
	}      
	
 public static String  getLocalIpAddress(){
	 InetAddress inet;
	try {
		inet = InetAddress.getLocalHost();
	} catch (UnknownHostException e) {
		Log.v(TAG, "getLocalHost出错");
		e.printStackTrace();
		return null;
	}
     return  inet.getHostAddress();
 }
    
	public static JSONObject getJSONObject(String url,String arg) throws Exception{
		JSONObject retJSONobj = null;
    	HttpPost post = new HttpPost(url);
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("act",arg));
        
    	try{
    		post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
    	}catch(UnsupportedEncodingException e){
    		
    		e.printStackTrace();
    	}
    	// 发送请求   
		HttpResponse httpResponse = null;
		try {
			 httpResponse = new DefaultHttpClient().execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 得到应答的字符串，这也是一个 JSON 格式保存的数据 
		String retSrc="";
		try {
			retSrc = EntityUtils.toString(httpResponse.getEntity());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
		
		try {
			retJSONobj = new JSONObject( retSrc);
		} catch (JSONException e) {
			e.printStackTrace();
		}  
		
    	return retJSONobj;
	}
	
	
	//向一个网络接口（url）post 四个参数，返回一个Json对象
    public static JSONObject 	getJSONObject (String url,String actNumber,String username,String password, String stateless) throws Exception{
    	JSONObject retJSONobj = null;
    	HttpPost post = new HttpPost(url);
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("act",actNumber));
    	params.add(new BasicNameValuePair("userid",username));
    	params.add(new BasicNameValuePair("passwd",password));
        params.add(new BasicNameValuePair("stateless",stateless));
    	try{
    		post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
    	}catch(UnsupportedEncodingException e){
    		Log.v(TAG, "http post 异常");
    		e.printStackTrace();
    	}
    	// 发送请求   
		HttpResponse httpResponse = null;
		try {
			 httpResponse = new DefaultHttpClient().execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 得到应答的字符串，这也是一个 JSON 格式保存的数据   
		String retSrc="";
		try {
			retSrc = EntityUtils.toString(httpResponse.getEntity());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
		
		try {
			retJSONobj = new JSONObject( retSrc);
		} catch (JSONException e) {
			e.printStackTrace();
		}  
		
    	return retJSONobj;
    	
    }
    
    
    // 踢下ip
    public static JSONObject getJSONObject (String url,String actNumber,String username,String password, String stateless,String ip) throws Exception{
    	JSONObject retJSONobj = null;
    	HttpPost post = new HttpPost(url);
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("act",actNumber));
    	params.add(new BasicNameValuePair("userid",username));
    	params.add(new BasicNameValuePair("passwd",password));
        params.add(new BasicNameValuePair("stateless",stateless));
        params.add(new BasicNameValuePair("cip",ip));
        
    	try{
    		post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
    	}catch(UnsupportedEncodingException e){
    		Log.v(TAG, "http post 异常");
    		e.printStackTrace();
    	}
    	// 发送请求   
		HttpResponse httpResponse = null;
		try {
			 httpResponse = new DefaultHttpClient().execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 得到应答的字符串，这也是一个 JSON 格式保存的数据   
		String retSrc="";
		try {
			retSrc = EntityUtils.toString(httpResponse.getEntity());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
		
		try {
			retJSONobj = new JSONObject( retSrc);
		} catch (JSONException e) {
			e.printStackTrace();
		}  
		
    	return retJSONobj;
    	
    }
    
    //
	public static JSONObject getJSONObject(String url,String arg,String stateless,String hashString) throws Exception{
		JSONObject retJSONobj = null;
    	HttpPost post = new HttpPost(url);
    	List<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("act",arg));
    	params.add(new BasicNameValuePair("stateless",stateless));
    	params.add(new BasicNameValuePair("keyhash",hashString));
    	try{
    		post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
    	}catch(UnsupportedEncodingException e){
    		
    		e.printStackTrace();
    	}
    	// 发送请求   
		HttpResponse httpResponse = null;
		try {
			 httpResponse = new DefaultHttpClient().execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 得到应答的字符串，这也是一个 JSON 格式保存的数据 
		String retSrc="";
		try {
			retSrc = EntityUtils.toString(httpResponse.getEntity());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
		
		try {
			retJSONobj = new JSONObject( retSrc);
		} catch (JSONException e) {
			e.printStackTrace();
		}  
		
		//测试用代码，取得应答数据，检查是否正确
	/*	String module="";
		String empoent="";
		try {
			module = retJSONobj.getString("module");
			empoent = retJSONobj.getString("empoent");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
    	return retJSONobj;
	}
	
	public static String getUserIpByJSONArray(String kpips){
		JSONObject obj = null;
		String CurIp = null;
		long CURtime = 0;
		int index = 0;
		try {
			JSONArray array = new JSONArray(kpips);
			int length = array.length();
			// 当前登录的ip，也就是startTime最大的
		    for(int i = 0;i < length; i++){
		    	JSONObject jo = array.getJSONObject(i);
		    	long Ttime = jo.getLong("startTime");
		    	if(CURtime < Ttime){
		    		CURtime = Ttime;
		    		index = i;
		    	}
		    }
			obj = array.getJSONObject(index); //最后一个是刚刚登陆的ip
			CurIp = obj.getString("ipstr");
		} catch (JSONException e) {
			Log.v(TAG, "获得JSONArray 异常");
		}
		return CurIp;
	}

}
