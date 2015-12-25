package com.util;

import android.annotation.SuppressLint;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.Period;

public class DateTextUtil {
	public static final String FORMAT_STR_DATE = "yyyy-MM-dd";
	public static final String FORMAT_STR_TIMESTAMP = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_STR_TIMESTAMP_S = "yyyy-MM-dd HH:mm";
	public static final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat(FORMAT_STR_DATE);
	public static final SimpleDateFormat FORMAT_TIMESTAMP = new SimpleDateFormat(FORMAT_STR_TIMESTAMP);
	public static final Calendar THIS_YEAR = Calendar.getInstance();
	
	@SuppressLint("SimpleDateFormat")
	public static String toString(Date date,String format){
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}
	
	public static String toTimeStampString(int second){
		Period p =new Period(second*1000);
		return p.getHours() + ":" + p.getMinutes() + ":" + p.getSeconds();
	}
	
	public static String getFormatCurentTime(){
		Date date = new Date();
		return toString(date,FORMAT_STR_TIMESTAMP);
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getLoginTime(String start,String now) throws ParseException{
		String rusult = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date begin = df.parse(start); 
		Date end =df.parse(now);
		long l= begin.getTime()- end.getTime();
		long day = l/(24*60*60*1000);
		long hour = (l/(60*60*1000)-day*24);
		long min = ((l/(60*1000))-day*24*60-hour*60);
		long s = (l/1000-day*24*60*60-hour*60*60-min*60); 
		rusult = day+"天"+hour+"小时"+min+"分"+s+"秒";
		return rusult;
	}
	
	public static String standardize(String text) {
		StringBuilder sb = new StringBuilder();
		String[] parts = text.trim().split(" ");
		String[] dateParts = parts[0].split("-");
		
		if (dateParts[0].length() == 4) {
			if (Integer.valueOf(dateParts[0]) > THIS_YEAR.get(Calendar.YEAR)) 
				dateParts[0] = String.valueOf(THIS_YEAR.get(Calendar.YEAR));
		} else if (Integer.valueOf(dateParts[0]) < 10) {
			dateParts[0] = "200" + Integer.valueOf(dateParts[0]);
		} else if (Integer.valueOf(dateParts[0]) >= 10 && Integer.valueOf(dateParts[0]) < 2000) {
			dateParts[0] = "20" + Integer.valueOf(dateParts[0]);
		}
		
		if (Integer.valueOf(dateParts[1]) < 1)
			dateParts[1] = "1";
		else if (Integer.valueOf(dateParts[1]) > 12)
			dateParts[1] = "12";
		if (Integer.valueOf(dateParts[1]) < 10)
			dateParts[1] = "0" + Integer.valueOf(dateParts[1]);
		
		// 这里并没有保证每个月的最大日期总是正确
		if (Integer.valueOf(dateParts[2]) < 1)
			dateParts[2] = "1";
		else if (Integer.valueOf(dateParts[2]) > 31)
			dateParts[1] = "31";
		if (Integer.valueOf(dateParts[2]) < 10)
			dateParts[2] = "0" + Integer.valueOf(dateParts[2]);
		
		sb.append(dateParts[0] + "-");
		sb.append(dateParts[1] + "-");
		sb.append(dateParts[2]);
		
		if (parts.length == 2) {
			sb.append(" ");
			String[] timeParts = parts[1].split(":");
			if (Integer.valueOf(timeParts[0]) < 10)
				timeParts[0] = "0" + Integer.valueOf(timeParts[0]);
			if (Integer.valueOf(timeParts[1]) < 10) 
				timeParts[1] = "0" + Integer.valueOf(timeParts[1]);
			sb.append(timeParts[0] + ":");
			sb.append(timeParts[1] + ":");
			if (timeParts.length == 3) {
				if (Integer.valueOf(timeParts[2]) < 10)
					timeParts[2] = "0" + Integer.valueOf(timeParts[2]);
				sb.append(timeParts[2]);
			} else {
				sb.append("00");
			}
		} else {
			sb.append(" 00:00:00");
		}

		return sb.toString();
	}
}
