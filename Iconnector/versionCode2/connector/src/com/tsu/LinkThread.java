package com.tsu;


import android.os.Handler;
import android.os.Message;

import com.util.DateTextUtil;

public class LinkThread extends Thread {

	private Handler handler;
	boolean flag = false;
	public LinkThread(Handler hand){
		super();
		flag = true;
		this.handler = hand;
		this.start();
	}
	
	public void stopThread(){
		flag = false;
		}
	@Override
	public void run(){
		while(flag){
			try {
				double flux = UserOperate.getLinkInfo();
				if(flux == -100){
					flag = false;
				}
			    Message msg = Message.obtain();
			    msg.arg1 = (int)(flux/1024/1024); //先换算成MB单位的
			    msg.what = 2;
			    handler.sendMessage(msg);
			    Thread.sleep(180*1000);  //休息
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
