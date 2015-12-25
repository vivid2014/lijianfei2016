package com.tsu;

import java.util.LinkedList;
import java.util.List;
import android.app.Activity;   
import android.app.Application;   
import android.util.Log;

// 单态模式
public class SysApplication extends Application{
	private static String LOGV = "SysApplication";
	private List<Activity> mlist = new LinkedList<Activity>(); 
	private static SysApplication instance;
	//私有构造器，只能自己构造自己
	private SysApplication(){
		
	}
	
	public synchronized static  SysApplication getInstance(){
		if(null == instance){
			instance = new SysApplication(); 
		}
		return instance;
	}
	 
	public void addActivity(Activity activity){
		mlist.add(activity);
	}
	
	// 所有的Activity都停止活动后再退出系统
	public void exitApplication(){
		try{
			for(Activity act : mlist){
				if(null != act){
					act.finish();
				}
			}
			
		}catch(Exception e){
			Log.v(LOGV, "退出失败！");
		}finally{
			System.exit(0);
		}
	}
	
	@Override
	public void onLowMemory(){
		super.onLowMemory();
		System.gc(); 			//告诉系统回收
	}
}
