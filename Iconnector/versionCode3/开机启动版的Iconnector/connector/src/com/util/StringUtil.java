package com.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class StringUtil {
	
	private static char[] HEXCHAR = { '0', '1', '2', '3', '4', '5', '6', '7','8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	   
		//把长String分割较短的俩部分
		public static List<String> splitMessages(List<String> messages) {
	        List<String> splitedMessages = new ArrayList<String>(messages.size() * 2);
	        for (String message : messages) {
	            int half = (int) Math.ceil(((double) message.length()) / ((double) 2));
	            splitedMessages.add(message.substring(0, half));
	            if (half < message.length()) {
	                splitedMessages.add(message.substring(half, message.length()));
	            }
	        }

	        return splitedMessages;

	    }
		
		   public static String bigIntegerToString(List<BigInteger> list) {
		        StringBuilder plainText = new StringBuilder();
		        for (BigInteger bigInteger : list) {
		            plainText.append(new String(bigInteger.toByteArray()));
		        }
		        return plainText.toString();
		    }
		    
		    public static byte[] bigIntegerToByteArray(List<BigInteger> list){
		    	StringBuilder plainText = new StringBuilder();
		    	String text;
		    	for(BigInteger bigInteger:list){
		    		plainText.append(new String(bigInteger.toByteArray()));
		    	}
		    	text = plainText.toString();
		    	return text.getBytes();
		    }
		    
		    //将字节数组变为ASSIC码表示的字符串
		    public static String toHexString(byte[] b) {
		    	
		    	//构造一个不带任何字符的字符串生成器，其初始容量由 capacity 参数指定
		  		  StringBuilder sb = new StringBuilder(b.length * 2);
		  		  for (int i = 0; i < b.length; i++) {
		  		  sb.append(HEXCHAR[(b[i] & 0xf0) >>> 4]);
		  		  sb.append(HEXCHAR[b[i] & 0x0f]);   
		  		  }   
		  		  return sb.toString();   
		    }
		    
		    public static String convertCharToBits(char c) {
				String result = "" ;
				int n = (int)c ;
				for (int bit=15 ; bit>=0 ; bit--) {
					if ( n>=Math.pow(2,bit) ) { result+="1" ; n-=Math.pow(2,bit) ; }
					else { result+="0" ; }
				}
				return result ;
			}
		    
		    public static String convertStringToBits(String s) {
				String result = "" ;
				for (int i=0 ; i<s.length() ; i++) {
					result += convertCharToBits(s.charAt(i)) ;
				}
				return result ;
			}
		    
			public static char convertBitsToChar(String bits) {
				int result = 0 ;
				for (int bit=0 ; bit<16 ; bit++) {
						if ( bits.charAt(bit)=='1' ) {
							result += Math.pow(2,15-bit) ; 
							}
				}
				return (char)result ;
			}
			
			public static String convertBitsToString(String bits) {
				String result = "" ;
				String block ;
				while (bits.length()!=0) {
					block = bits.substring(0,16) ;
					bits = bits.substring(16) ;
					result += convertBitsToChar(block) ;
				}
				return result ;
			}
			
}
