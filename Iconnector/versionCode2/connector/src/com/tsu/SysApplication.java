package com.tsu;

import java.util.LinkedList;
import java.util.List;
import android.app.Activity;   
import android.app.Application;   
import android.util.Log;

// ��̬ģʽ
public class SysApplication extends Application{
	private static String LOGV = "SysApplication";
	private List<Activity> mlist = new LinkedList<Activity>(); 
	private static SysApplication instance;
	//˽�й�������ֻ���Լ������Լ�
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
	
	// ���е�Activity��ֹͣ������˳�ϵͳ
	public void exitApplication(){
		try{
			for(Activity act : mlist){
				if(null != act){
					act.finish();
				}
			}
			
		}catch(Exception e){
			Log.v(LOGV, "�˳�ʧ�ܣ�");
		}finally{
			System.exit(0);
		}
	}
	
	@Override
	public void onLowMemory(){
		super.onLowMemory();
		System.gc(); 			//����ϵͳ����
	}
}
