package com.util;

import java.security.MessageDigest;
public class MyMd5 {
	static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
		'a', 'b', 'c', 'd', 'e', 'f' }; 
	
	 public static String hashMD5(String message){
		 char str[];
		 byte []strByte;
		 try{
			 MessageDigest mdTemp = MessageDigest.getInstance("MD5"); 
			 mdTemp.reset();
			 mdTemp.update(message.getBytes());
			 byte[] md = mdTemp.digest(); 
			 int j = md.length; 
			 strByte = new byte[j];
			 strByte = md;
			 str = new char[j * 2]; 
			 int k = 0; 
			 for (int i = 0; i < j; i++) {
			 byte b = md[i]; 
			 str[k++] = hexDigits[b >> 4 & 0xf]; 
			 str[k++] = hexDigits[b & 0xf]; 
			   } 
			 
		 }catch(Exception e){
			 e.getMessage();
			 return null;
		 }
		 return HexEncode(strByte);
		 //return new String(strByte);
		 //return new String(str);
	 }
	 
	 public static String HexEncode(byte[] toencode) { 
	        StringBuilder sb = new StringBuilder(toencode.length * 2); 
	        for(byte b: toencode){ 
	            sb.append(Integer.toHexString((b & 0xf0) >>> 4)); 
	            sb.append(Integer.toHexString(b & 0x0f)); 
	        } 
	        return sb.toString(); 
	    } 

	 public static void main(String[] args) { 
		 System.out.println("caidao的MD5加密后：\n"+MyMd5.hashMD5("caidao")); 
		 System.out.println("http://www.baidu.com/的MD5加密后：\n"+MyMd5.hashMD5("http://www.baidu.com/")); 
		 } 
}
